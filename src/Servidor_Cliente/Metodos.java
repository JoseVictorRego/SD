package Servidor_Cliente;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JOptionPane;

public class Metodos {
    public void comandoPrincipal(String serveNome, String serverIp, int serverPort) throws IOException {
        //Conectar ao servidor de envio
        try(Socket mainSocket = new Socket(serverIp, serverPort)) {
                    
        // Conectando ao servidor
        System.out.println("Conectado ao servidor.");

        //enviar o seu nome ao servidor
        PrintWriter out = new PrintWriter(mainSocket.getOutputStream(), true);
        out.println(serveNome);

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

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Servidor não Encontrado ");
        }  
    }

    public void enviarArquivo(Socket socket, String clientName, String filePath) throws IOException {
        
        File arquivo = new File(filePath); // Usando a Função File.

        if (!arquivo.exists()) {           //Caso o Arquivo não exista ou não encontrado
            JOptionPane.showMessageDialog(null, "Arquivo não encontrado.");
        }

        else{
            // Enviar o nome do cliente antes do nome do arquivo e dos dados do arquivo
            OutputStream os = socket.getOutputStream();
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
}
