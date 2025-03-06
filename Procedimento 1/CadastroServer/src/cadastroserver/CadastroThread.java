/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cadastroserver;

import controller.ProdutoJpaController;
import controller.UsuarioJpaController;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;
import model.Usuario;


public class CadastroThread extends Thread {
    
    private ProdutoJpaController ctrl;
    private UsuarioJpaController ctrlUsu;
    private Socket s1;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    

    CadastroThread  (ProdutoJpaController ctrl, UsuarioJpaController ctrlUsu, Socket s1) {
	this.ctrl = ctrl;
        this.ctrlUsu = ctrlUsu;
        this.s1 = s1;
	}
    
    
    @Override
    public void run(){
      
        String login = "";
        
        try{
            out = new ObjectOutputStream(s1.getOutputStream());
            in = new ObjectInputStream(s1.getInputStream());
            
            System.out.println("Cliente conectado.");

            login = (String) in.readObject();
            String senha = (String) in.readObject();

            Usuario usuario = ctrlUsu.findUsuario(login, senha);
            if (usuario == null) {
                System.out.println("Usuário inválido."); //Login="+ login +", Senha="+ senha
                out.writeObject("Usuário inválido.");
                return;
            }
            
            System.out.println("Usuário conectado.");
            out.writeObject("Usuário conectado.");

            System.out.println("Aguardando comandos...");
            String comando = (String) in.readObject();
            
            if (comando.equals("L")) {
                System.out.println("Listando produtos.");
                out.writeObject(ctrl.findProdutoEntities());
            }           
        }catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            close();
            System.out.println("Conexão finalizada.");
        }
        
    }
    
    private void close() {
        try {
            if (out != null) {
                out.close();
            }
            if (in != null) {
                in.close();
            }
            if (s1 != null) {
                s1.close();
            }
        } catch (IOException ex) {
            System.out.println("Falha ao finalizar conexão.");
        }
    }
}
