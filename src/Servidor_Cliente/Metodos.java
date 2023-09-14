package Servidor_Cliente;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Hashtable;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.InitialDirContext;
import javax.swing.JOptionPane;

public class Metodos {
    public void comandoPrincipal(String serverIp, int serverPort) throws IOException {
        try(Socket mainSocket = new Socket(serverIp, serverPort)) {
                    
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
                enviarArquivo(mainSocket, clientName, filePath);   
            }

            // Fechando a conexão
            System.out.println("#Conexão encerrada!");

        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Servidor não Encontrado "); //break;
        }
    }

    public void enviarArquivo(Socket socket, String clientName, String filePath) throws IOException {
        
        File arquivo = new File(filePath); // Usando a Função File.

        if (!arquivo.exists()) {           //Caso o Arquivo não exista ou não encontrado
            JOptionPane.showMessageDialog(null, "Arquivo não encontrado.");
        }

        else{

            // Enviar o nome do cservidor.
            OutputStream os = socket.getOutputStream();
            
            // Enviar o nome do cliente antes do nome do arquivo e dos dados do arquivo
            os.write(clientName.getBytes("UTF-8"));
            os.write("\n".getBytes());

            //Copia o nome do arquivo para a string fileName para enviar ao servidor.
            String fileName = arquivo.getName();
            os.write(fileName.getBytes("UTF-8"));
            os.write("\n".getBytes());

            try (// Reseve o Arquivo para preparar o envio 
            FileInputStream fis = new FileInputStream(arquivo)) {
                byte[] buffer = new byte[4096]; //buffer usado para armazenar temporariamente os bytes lidos do arquivo antes de serem enviados
                
                //Quarda a quantidade de bytes lidos
                int bytesRead;

                //Faz a leiturua dos byte do arquivo e envia ao servido e finaliza quando chegar em '-1'
                while ((bytesRead = fis.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }

                os.flush();
            } 

            JOptionPane.showMessageDialog(null, "Arquivo enviado com sucesso.");
        }
    }

    public static String getServerIpFromDns() {
        String servidorDns = "192.168.1.18:52";
        String nomeHost = "servidorsdjorge.balanceador";
        
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
