/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package cadastroclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;
import model.Produto;




public class CadastroClientv2 {   
    
    private static ObjectOutputStream socketOut;
    private static ObjectInputStream socketIn;
    private static ThreadClient threadClient;
    
    
    /**
     * @param args the command line arguments
     */
    
    
    public static void main(String[] args)throws ClassNotFoundException, IOException {
        Socket socket = new Socket("localhost", 4321);
        socketOut = new ObjectOutputStream(socket.getOutputStream());
        socketIn = new ObjectInputStream(socket.getInputStream());
        
        // Encapsula a leitura do teclado em um BufferedReader
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        
        // Instancia a janela SaidaFrame para apresentação de mensagens
        SaidaFrame saidaFrame = new SaidaFrame();
        saidaFrame.setVisible(true);
        
        // Instancia a Thread para preenchimento assíncrono com a passagem do canal de entrada do Socket
        threadClient = new ThreadClient(socketIn, saidaFrame.texto);
        threadClient.start();
        
        // Login, passando usuário "op1"
        socketOut.writeObject("op1");
        
        // Senha para o login usando "op1"
        socketOut.writeObject("op1");

        // Exibe Menu:
        Character commando = ' ';
        try {
            while (!commando.equals('X')) {
                System.out.println("Escolha uma opção:");
                System.out.println("L - Listar | X - Finalizar | E - Entrada | S - Saída");

                // Lê a opção do teclado usando o reader e converte para Character:
                commando = reader.readLine().charAt(0);

                processaComando(reader, commando);
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            saidaFrame.dispose();
            socketOut.close();
            socketIn.close();
            socket.close();
            reader.close();
        }
    }

    static void processaComando(BufferedReader reader, Character commando) throws IOException {
        // Define comando a ser enviado ao servidor:
        socketOut.writeChar(commando);
        socketOut.flush();
                
        switch (commando) {
            case 'L':
                // Comando é apenas enviado para o servidor.
                break;
            case 'S':
            case 'E':
                // Confirma envio do comando ao servidor:
                socketOut.flush();

                // Lê os dados do teclado:
                System.out.println("Digite o Id da pessoa:");
                int idPessoa = Integer.parseInt(reader.readLine());
                System.out.println("Digite o Id do produto:");
                int idProduto = Integer.parseInt(reader.readLine());
                System.out.println("Digite a quantidade:");
                int quantidade = Integer.parseInt(reader.readLine());
                System.out.println("Digite o valor unitário:");
                long valorUnitario = Long.parseLong(reader.readLine());

                // Envia os dados para o servidor:
                socketOut.writeInt(idPessoa);
                socketOut.flush();
                socketOut.writeInt(idProduto);
                socketOut.flush();
                socketOut.writeInt(quantidade);
                socketOut.flush();
                socketOut.writeLong(valorUnitario);
                socketOut.flush();
                break;
            case 'X':
                threadClient.cancela(); // Cancela a ThreadClient já que o cliente está desconectando.
                break;
            default:
                System.out.println("Opção inválida!");
        }
    }
    
}
