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
        int[] Contador = {};

        //Chamando os Metodos
        Metodos ServerBalanceadorMetodos = new Metodos();

        //Iniciando o Servidor
        try (ServerSocket serverSocket = new ServerSocket(serverPort)) {

            System.out.println("Servidor de Redirecionamento aguardando conexões...");

            while (true) {

                //Chegada de um novo Cliente
                Socket clientSocket = serverSocket.accept();
                System.out.println("\n-----Cliente conectado ao servidor de Redirecionamento------\n");

                

                // Criar uma nova thread para lidar com a conexão do cliente
                Thread thread = new Thread(() -> {
                    try {
                        //Contador de Cliente em cada Servidor atualmente
                        //Servidor_A
                        Contador[0] = ServerBalanceadorMetodos.contador(serverAddress, Sevidor_Port[0]);
                        //Servidor_B
                        Contador[1] = ServerBalanceadorMetodos.contador(serverAddress, Sevidor_Port[1]);

                        if(Contador[0]!=-1 && Contador[1]!=-1){
                            if(Contador[0]<=Contador[1]){
                                //Redirecionar ao servidor_A
                                ServerBalanceadorMetodos.redirecionar(clientSocket, serverAddress, Sevidor_Port[0]);
                            }
                            else{
                                //Redirecionar ao servidor_B
                                ServerBalanceadorMetodos.redirecionar(clientSocket, serverAddress, Sevidor_Port[1]);
                            }
                            
                        }else{
                            String redirectMessage = "Servidor em manutenção, tente mais tarde!";
                            clientSocket.getOutputStream().write(redirectMessage.getBytes());
                        }
                        
                        // Fechar a conexão com o cliente após o redirecionamento
                        clientSocket.close();
                        
                        System.out.println("Cliente redirecionado para o servidor principal.");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

                thread.start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
