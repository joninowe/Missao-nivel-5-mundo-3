/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cadastroserver;

import controller.MovimentoJpaController;
import controller.PessoaJpaController;
import controller.ProdutoJpaController;
import controller.UsuarioJpaController;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Movimento;
import model.Produto;
import model.Usuario;


public class CadastroThreadv2 extends Thread {
    
    private ProdutoJpaController ctrl;
    private UsuarioJpaController ctrlUsu;
    private MovimentoJpaController ctrlMov;
    private PessoaJpaController ctrlPessoa;
    private Socket s1;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Usuario usuario;
    private Boolean continuaProcesso = true;

    CadastroThreadv2  (ProdutoJpaController ctrl, UsuarioJpaController ctrlUsu, MovimentoJpaController ctrlMov, PessoaJpaController ctrlPessoa, Socket s1) {
	this.ctrl = ctrl;
        this.ctrlUsu = ctrlUsu;
        this.ctrlMov = ctrlMov;
        this.ctrlPessoa = ctrlPessoa;
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
            usuario = ctrlUsu.findUsuario(login, senha);

            //Usuario usuario = ctrlUsu.findUsuario(login, senha);
            if (usuario == null) {
                System.out.println("Usuário inválido.");
                out.writeObject("Usuário inválido.");
                return;
            }
            
            System.out.println("Usuário conectado.");
            out.writeObject("Usuário conectado.");
            out.flush();
       
            
            while (continuaProcesso) {
                continuaProcesso = processaComando();
            }
          
            /*
            System.out.println("Aguardando comandos...");
            String comando = (String) in.readObject();
           
            if (comando.equals("L")) {
                System.out.println("Listando produtos.");
                out.writeObject(ctrl.findProdutoEntities());
            }  
            */

        }catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            Logger.getLogger(CadastroThreadv2.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            close();
            System.out.println("Conexão finalizada.");
        }
        
    }
    

    private Boolean processaComando() throws Exception {
        System.out.println("Aguardando comandos...");
        Character comando = in.readChar();

        switch (comando) {
            case 'L':
                System.out.println("Comando recebido, listando produtos.");
                out.writeObject(ctrl.findProdutoEntities());
                continuaProcesso = true;
                return true;
            case 'E':
                continuaProcesso = true;
                return true;
            case 'S':
                System.out.println("Comando Movimento tipo ["+ comando +"] recebido.");
                int idPessoa = in.readInt();
                int idProduto = in.readInt();
                int quantidade = in.readInt();
                Float valorUnitario = in.readFloat();

                Produto produto = ctrl.findProduto(idProduto);
                if (produto == null) {
                    out.writeObject("Produto inválido.");
                    continuaProcesso = true;
                    return true;
                }
                
                if (comando.equals('E')) {
                    produto.setQuantidade(produto.getQuantidade() + quantidade);
                    continuaProcesso = true;
                    return true;
                } else if (comando.equals('S')) {
                    produto.setQuantidade(produto.getQuantidade() - quantidade);
                    continuaProcesso = true;
                    return true;
                }

                ctrl.edit(produto);

                Movimento movimento = new Movimento();
                movimento.setTipo(comando);
                movimento.setUsuarioidUsuario(usuario);
                movimento.setPessoaIdpessoa(ctrlPessoa.findPessoa(idPessoa));
                movimento.setProdutoIdproduto(produto);
                movimento.setQuantidade(quantidade);
                movimento.setValorUnitario(valorUnitario);

                ctrlMov.create(movimento);
                out.writeObject("Movimento registrado com sucesso.");
                out.flush();
                System.out.println("Movimento registrado com sucesso.");
                continuaProcesso = true;
                return true;
            case 'X':
                continuaProcesso = false;
                return false;
            default:
                System.out.println("Opção inválida!");
                continuaProcesso = false;
                return true;
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
            System.out.println("Falha ao fechar conexão.");
        }
    }
}
