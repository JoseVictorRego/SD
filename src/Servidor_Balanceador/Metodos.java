package Servidor_Balanceador;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class Metodos {
    public int contador(String serverAddress, int serverPort) throws IOException {
        int clientCount; // Valor padrão para retornar em caso de erro

        try (Socket socket = new Socket(serverAddress, serverPort)) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String clientCountString = reader.readLine();
            clientCount = Integer.parseInt(clientCountString);

            System.out.println("Número de clientes conectados: " + clientCount);
                
        } catch (IOException e) {
            clientCount = -1;
            System.out.println("Erro ao se conectar ao servidor: " + e.getMessage());
        }

        return clientCount;
    }
    
    public void redirecionar(Socket clientSocket, String redirectServerIp, int redirectServerPort) throws IOException{
        // Enviar informações de redirecionamento para o cliente
        String redirectMessage = redirectServerIp + ":" + redirectServerPort;
        clientSocket.getOutputStream().write(redirectMessage.getBytes());
    }
}
