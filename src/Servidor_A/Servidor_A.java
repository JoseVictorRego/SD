package Servidor_A;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor_A {
    public static void main(String[] args) {

        int serverPort = 1000; // Porta do servidor
        int[] clientCount = {0}; // Usando um array para armazenar o contador
        String[] serveConectNames = {"ServeBalanceador", "ServeCliente"}; // Possiveis servidores que podem ser conectado ao servidor
        String serveNome = "Servidor_A"; // Nome do servidor atual, para uso de cliacão de pasta dos arquivos do cliente

        Metodos serverMetodos = new Metodos(); // Funções Necessarias

        // Abrindo Conexão do servidor
        try (ServerSocket serverSocket = new ServerSocket(serverPort)) {
            System.out.println("Servidor aguardando conexões...");

            // Fazendo com que a conexão com o cliente continue em loop
            while (true) {

                // Conectando o cliente
                Socket clientSocket = serverSocket.accept();
                System.out.println("\n-----Cliente conectado------\n");
                
                //Indentifica qual foi o cliente conectado se foi o balanceador ou um cliente comum
                String clienteConectado = serverMetodos.nameClient(clientSocket);

                if(clienteConectado.equals(serveConectNames[0])){
                    // Enviar o valor de clientCount para o Balanceador
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                    out.println(clientCount[0]);

                }else{
                    // Tratar a conexão em uma thread separada para permitir o atendimento a múltiplos clientes
                    Thread thread = new Thread(() -> {
                        // Incrementar o contador de clientes
                        clientCount[0]++;

                        try {
                            //So pra saber a quantidade de clientes conectados
                            System.out.println("Clientes conectados: " + clientCount[0]+"\n");

                            //Chamar função de recebimento de dados.
                            serverMetodos.receberArquivo(clientSocket, serveNome);
            
                        } catch (IOException e) {
                            System.out.println("-> Servidor perdeu Conexão com o cliente!!");
                            
                        } finally{
                            clientCount[0]--;
                            System.out.println("\nClientes conectados: " + clientCount[0]);
                        }
                    });
                    thread.start();
                }
                
            }
            //chamada do envio do valor do numero de usuarios para o servidor de balanceamento.
        } catch (IOException e) {
            System.out.println("-> Servidor Parou!!");
        }
    }
    
}
