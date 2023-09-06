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
        Metodos ServerBalanceadorMetodos = new Metodos();

        //Iniciando o Servidor
        try (ServerSocket serverSocket = new ServerSocket(serverPort)) {

            System.out.println("Servidor de Redirecionamento aguardando conexões...");

            while (true) {

                //Chegada de um novo Cliente
                Socket clientSocket = serverSocket.accept();
                System.out.println("\n-----Cliente conectado ao servidor de Redirecionamento------\n");
                
                int contadorServeA = ServerBalanceadorMetodos.contador(serverAddress, Sevidor_Port[0]);
                int contadorServeB = ServerBalanceadorMetodos.contador(serverAddress, Sevidor_Port[1]);
                int escolha = ServerBalanceadorMetodos.escolhaRedirecionamento(contadorServeA,contadorServeB);

                // Criar uma nova thread para lidar com a conexão do cliente
                Thread thread = new Thread(() -> {
                    try {
                        if(escolha==-1){
                            System.out.println("\nNão encontrou servidores");
                            String redirectMessage = "Servidor em manutencao, tente mais tarde.";
                            clientSocket.getOutputStream().write(redirectMessage.getBytes());
                        }else{
                            System.out.println(contadorServeA+":"+contadorServeB);
                            ServerBalanceadorMetodos.redirecionar(clientSocket, serverAddress, Sevidor_Port[escolha]);
                        }
                        
                        
                        /*
                        //Servidor_A
                        Contador[0] = ServerBalanceadorMetodos.contador(serverAddress, Sevidor_Port[0]);
                        System.out.println("Valor:"+ Contador[0]);
                        //Contador de Cliente em cada Servidor atualmente
                        System.out.println("\nParte 1, Contador 1: " +serverAddress+", Contador 2: "+Sevidor_Port[0]);
                        
                        //Servidor_B
                        Contador[1] = ServerBalanceadorMetodos.contador(serverAddress, Sevidor_Port[1]);

                        System.out.println("\nParte 1, Contador 1: " +Contador[0]+", Contador 2: "+Contador[1]);

                        //Escolha de redirecionamento
                        int escolha = ServerBalanceadorMetodos.escolhaRedirecionamento(3,3);
                        System.out.println("\nParte 2, resposta: " + resposta);

                        if(resposta==0){
                            System.out.println("Cliente se conectou ao servidor A\n"+serverAddress+"\n"+Sevidor_Port[resposta]);
                            //ServerBalanceadorMetodos.redirecionar(clientSocket, serverAddress, Sevidor_Port[resposta]);
                        }
                        else if(resposta==1){
                            System.out.println("Cliente se conectou ao servidor B\\n" + serverAddress+ "\n" + Sevidor_Port[resposta]);
                            //ServerBalanceadorMetodos.redirecionar(clientSocket, serverAddress, Sevidor_Port[resposta]);
                        }
                        else{
                            System.out.println("\nNão encontrou servidores, valor: "+resposta);
                            String redirectMessage = "Servidor em manutencao, tente mais tarde.";
                            clientSocket.getOutputStream().write(redirectMessage.getBytes());
                        } */
                        
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
