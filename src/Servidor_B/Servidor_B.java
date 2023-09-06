package Servidor_B;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor_B {
    public static void main(String[] args) {

        int serverPort = 2000; // Porta do servidor
        int[] clientCount = {0}; // Usando um array para armazenar o contador
        String serveNome = "Servidor_B";

        Metodos serverMetodos = new Metodos(); // Funções Necessarias

        // Abrindo Conexão do servidor
        try (ServerSocket serverSocket = new ServerSocket(serverPort)) {
            System.out.println("Servidor aguardando conexões...");

            // Fazendo com que a conexão com o cliente continue em loop
            while (true) {

                // Conectando o cliente
                Socket clientSocket = serverSocket.accept();
                System.out.println("\n-----Cliente conectado------\n");

                // Incrementar o contador de clientes
                clientCount[0]++;
                System.out.println("Clientes conectados: " + clientCount[0]);
                
                // Enviar o valor de clientCount para o cliente
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                out.println(clientCount[0]);

                // Tratar a conexão em uma thread separada para permitir o atendimento a múltiplos clientes
                Thread thread = new Thread(() -> {
                    try {
                        //Chamar função de recebimento de dados.
                        serverMetodos.receberArquivo(clientSocket, serveNome);

                    } catch (IOException e) {
                        System.out.println("-> Servidor perdeu Conexão com o cliente!!" 
                                               +"\nClientes conectados: " + clientCount[0]);
                        
                    } finally{
                        clientCount[0]--;
                    }
                });
                thread.start();
            }
            //chamada do envio do valor do numero de usuarios para o servidor de balanceamento.
        } catch (IOException e) {
            System.out.println("-> Servidor Parou!!");
        }
    }
    
}
