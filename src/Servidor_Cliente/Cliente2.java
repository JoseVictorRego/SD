package Servidor_Cliente;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import javax.swing.JOptionPane;

public class Cliente2 {
    public static void main(String[] args){
        
        int serverPort = 4000;             // Porta do servidor
        String serveIp = "127.0.0.1";      // IP do Servidor
        String escolha;

        //Funções de Envio de dados
        Metodos clienteMetodos = new Metodos(); //Funções Necessarias

        do {
            try(Socket socket = new Socket(serveIp, serverPort)) {

                // Conectar ao servidor de redirecionamento
                System.out.println("Conectado ao servidor de redirecionamento.");
                
                // Receber informações de redirecionamento
                BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String redirectInfo = br.readLine();
                
                if(redirectInfo==null){

                    JOptionPane.showMessageDialog(null, " > Servidor em manutencao, tente mais tarde.");
                    System.out.println("\n #Conexão com o servidor de redirecionamento encerrada!");
                }
                else if(redirectInfo.equals("Servidor em manutencao, tente mais tarde.")){
                    JOptionPane.showMessageDialog(null, " > Servidor em manutencao, tente mais tarde.");
                    System.out.println("\n #Conexão com o servidor de redirecionamento encerrada!!");
                }
                else{

                    String[] parts = redirectInfo.split(":");
                    String redirectServerIp = parts[0];
                    int redirectServerPort = Integer.parseInt(parts[1]);
                    System.out.println("#Conexão com o servidor de redirecionamento encerrada!"+redirectServerIp+redirectServerPort);

                    //Conectar ao servidor de envio
                    clienteMetodos.comandoPrincipal(redirectServerIp, redirectServerPort);
                }

                // Perguntar ao usuário se deseja iniciar uma nova conexão ou encerrar o programa
                escolha = JOptionPane.showInputDialog("Didite qualquer digito para iniciar uma nova conexão ou '0' para Finalizar Conexão:");

            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Servidor não Encontrado "); break;
            }
        } while (!escolha.equals("0"));
    }

}
