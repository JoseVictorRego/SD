package Servidor_Balanceador;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor_Balanceador {
    public static void main(String[] args) {

        int serverPort = 4000; // Porta do servidor de redirecionamento
        
        //Sevidores
        String serverAddress = "127.0.0.1";  // Endereço do servidor
        int[] Sevidor_Port ={1000,2000};     // Porta do servidor

        //Chamando os Metodos
        Metodos serverBalanceadorMetodos = new Metodos();

        //Iniciando o Servidor
        try (ServerSocket serverSocket = new ServerSocket(serverPort)) {

            System.out.println("Servidor de Redirecionamento aguardando conexões...");

            while (true) {

                //Chegada de um novo Cliente
                Socket clientSocket = serverSocket.accept();
                System.out.println("\n-----Cliente conectado ao servidor de Redirecionamento------\n");
                
                int contadorServeA = serverBalanceadorMetodos.contador(serverAddress, Sevidor_Port[0]);
                int contadorServeB = serverBalanceadorMetodos.contador(serverAddress, Sevidor_Port[1]);

                System.out.println(contadorServeA+":"+contadorServeB);
                
                int escolha = serverBalanceadorMetodos.escolhaRedirecionamento(contadorServeA,contadorServeB);
                System.out.println(escolha);
                
                // Criar uma nova thread para lidar com a conexão do cliente
                Thread thread = new Thread(() -> {
                    try {
                        if(escolha==-1){
                            System.out.println("\nNão encontrou servidores");
                            String redirectMessage = "Servidor em manutencao, tente mais tarde.";
                            clientSocket.getOutputStream().write(redirectMessage.getBytes());
                        }else{
                            System.out.println("Clientes conectados nos servidores-> "+contadorServeA+":"+contadorServeB);
                            serverBalanceadorMetodos.redirecionar(clientSocket, serverAddress, Sevidor_Port[escolha]);
                        }
                        
                        // Fechar a conexão com o cliente após o redirecionamento
                        clientSocket.close();
                        
                        System.out.println("Cliente redirecionado para o servidor principal.");

                    } catch (IOException e) {
                        System.out.println("Perdeu Conexão com o cliente!");
                    }
                });

                thread.start();
            }

        } catch (IOException e) {
            System.out.println("Problemas em iniciar o servidor!");
        }
    }
}
