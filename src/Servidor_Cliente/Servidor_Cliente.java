package Servidor_Cliente;

import java.io.IOException;
import java.net.Socket;

import javax.swing.JOptionPane;

public class Servidor_Cliente {

    public static void main(String[] args){
        
        int serverPort = 5000;             // Porta do servidor
        String serveIp = "127.0.0.1";      // IP do Servidor
        String escolha;

        //Funções de Envio de dados
        Metodos ClienteMetodos = new Metodos(); //Funções Necessarias

        do { //Loop Basico para o Cliente ficar sempre em conexão com o servidor
            try(Socket socket = new Socket(serveIp, serverPort)) {
                
                // Conectando ao servidor
                System.out.println("Conectado ao servidor.");

                // Solicitação do nome do cliente
                String clientName = JOptionPane.showInputDialog("!!Bem-vindo ao nosso servidor!!\n\nDigite o seu nome, para continuar:");

                // Solicitação do caminho do arquivo a ser enviado
                String filePath = JOptionPane.showInputDialog("Digite o caminho completo do arquivo a ser enviado:");

                if(clientName==null || filePath==null){
                    JOptionPane.showMessageDialog(null, "Nome do cliente ou arquivo não indentificado!");
                }

                else{// Enviando o nome do cliente e o arquivo com o nome do arquivo/ para iniciar o envio do arquivo ao servidor.
                    ClienteMetodos.enviarArquivo(socket, clientName, filePath);   
                }

                // Fechando a conexão
                System.out.println("#Conexão encerrada!");

                //Mensagem de escolha de iniciar uma nova conexão ou finalizar conexão.
                escolha = JOptionPane.showInputDialog("Didite qualquer digito para iniciar uma nova conexão ou '0' para Finalizar Conexão:");
                

            } catch (IOException e) { // Caso não haja conexão com o servidor
                JOptionPane.showMessageDialog(null, "Servidor não Encontrado "); break;
            }
        }while(!escolha.equals("0"));
    }
}
