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
            clientCount = Integer.parseInt(clientCountString)-1; //diminuir o servidor de balanceamento.
            
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

    public int escolhaRedirecionamento(int a, int b){
        //int Valor=0 Redirecioar para Servidor_A
        //int Valor=1 Redirecioar para Servidor_B
        //int Valor=-1 Servidores desconectados
        
        int[] contador = {a,b};
        int valor;
        
        //Escolha de redirecionamento 
        if(contador[0]!=-1 && contador[1]!=-1){
            if(contador[0]<=contador[1]){
                valor = 0;
            }
            else{
                valor = 1;
            }
            
        }
        else if(contador[0]!=-1 && contador[1]==-1){
            valor = 0;
        }
        else if(contador[0]==-1 && contador[1]!=-1){
            valor = 1;
        }
        else{
            valor = -1;
        }

        return valor;
    }
}
