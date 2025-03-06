/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package cadastroserver;
import controller.ProdutoJpaController;
import controller.UsuarioJpaController;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;



public class CadastroServer {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException{
        ServerSocket serverSocket = new ServerSocket(4321);
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("CadastroServerPU");
        ProdutoJpaController ctrl = new ProdutoJpaController(emf);
        UsuarioJpaController ctrlUsu = new UsuarioJpaController(emf);
        //Socket socket = serverSocket.accept();
        //System.out.println("Cliente Conectou");
        
        while (true) {
            // Aguarda um cliente se conectar e aceita a conexão (chamada bloqueante)
            Socket clienteSocket = serverSocket.accept();
            System.out.println("Cliente conectado: " + clienteSocket.getInetAddress());
            
            // CadastroThread V1:
            // CadastroThread thread = new CadastroThread(ctrl, ctrlUsu, clienteSocket);
            
            // CadastroThread V2:
            CadastroThread thread = new CadastroThread(ctrl, ctrlUsu, clienteSocket);
            
            thread.start();
            System.out.println("Aguardando nova conexão...");
        }
        
        }
    } 

