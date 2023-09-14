package Servidor_Balanceador;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Hashtable;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.InitialDirContext;

public class Metodos {
    public int contador(String serverAddress, int serverPort) throws IOException {
        int clientCount; // Valor padrão para retornar em caso de erro
        //Iniciar a conecão com o servidor
        try (Socket socket = new Socket(serverAddress, serverPort)) {
            //receber valor do contador
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String clientCountString = reader.readLine();
            clientCount = Integer.parseInt(clientCountString) - 1; //diminuir o servidor de balanceamento.
            
        } catch (IOException e) {
            clientCount = -1;
            System.out.println("Erro ao se conectar ao servidor.");
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

    public String getServerIpFromDns(String nomeHost) {
        String servidorDns = "192.168.1.18:52";
        
        Hashtable<String, String> env = new Hashtable<>();
        env.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
        env.put("java.naming.provider.url", "dns://" + servidorDns);

        try {
            InitialDirContext idc = new InitialDirContext(env);
            Attributes attrs = idc.getAttributes(nomeHost, new String[] {"A"});
            return attrs.get("A").get().toString();
        } catch (NamingException e) {
            e.printStackTrace();
            System.out.println("Falha na resolução do nome do host.");
            return "127.0.0.1"; // valor padrão em caso de erro
        }
    }
}
