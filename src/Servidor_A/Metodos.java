package Servidor_A;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JOptionPane;

public class Metodos {
    private static Map<String, String> clientFolders = new HashMap<>(); // Mapear o nome do cliente para a pasta

    public void receberArquivo(Socket clientSSocket, String nomeServe) throws IOException {
        InputStream is = clientSSocket.getInputStream();
        
        try {
            // Receber o nome do cliente
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String clientName = new String(br.readLine().getBytes(), "UTF-8");


            // Verificar se a pasta do cliente já foi criada
            String saveDir = clientFolders.get(clientName);
            if (saveDir == null) {
                // Se a pasta ainda não existe, criar uma nova pasta
                String currentDir = System.getProperty("user.dir");
                saveDir = currentDir + "\\" + nomeServe + "\\" + clientName + "\\";
                
                File directory = new File(saveDir);
                if (!directory.exists()) {
                    directory.mkdirs();
                }
                // Adicionar a pasta ao mapa para reutilização futura
                clientFolders.put(clientName, saveDir);
            }

            // Receber o nome do arquivo
            String fileName = new String(br.readLine().getBytes(), "UTF-8");
            System.out.println(fileName);

            // Criar um novo arquivo com o nome recebido
            File file = new File(saveDir + fileName);
            
            try (FileOutputStream fos = new FileOutputStream(file)) {
                byte[] buffer = new byte[4096];
                
                int bytesRead;
                
                while ((bytesRead = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, bytesRead);
                }

                fos.flush();
            }

            System.out.println("Cliente: "+clientName+".\n ->Enviou um Arquivo e foi salvo com sucesso: " + fileName);

            // Fechando a conexão com o cliente
            clientSSocket.close(); //depois testar sem.
            System.out.println("Cliente: "+clientName+" Saiu!!\n");

            //-----------------------------------------------------------------------------------
            
            //envio dos arquivos para o servidor Principal
            servePrincipal(clientName, saveDir + fileName);
            System.out.println("\nEnviado dado para o Servidor Principal do cliente:"+clientName+"!!\n"
                                + "\n-- Conexão com o Serve Cliente finalizada. --");

        } catch (Exception e) {
            System.out.println(" #- Falha com o Clinte. Possiveis causas:\n -> Nome invalido;"
                                + "\n -> Arquvio invalido; \n -> Perda de conexão com o cliente."
                                + "\n-- Conexão com o Serve Cliente finalizada. --");
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

    public void servePrincipal(String clientName, String fileCaminho){
        int[] serverPrincialPort = {3000,5000};             // Porta do servidor Principal
        String serverPrincialIp = "127.0.0.1";      // IP do Servidor Principal
        
        try(Socket socketPrincipal = new Socket(serverPrincialIp, serverPrincialPort[0])) {
            enviarArquivo(socketPrincipal, clientName, fileCaminho);
            
        } catch (IOException e) { // Caso não haja conexão com o servidor
            JOptionPane.showMessageDialog(null, "Servidor Principal indisponível!");
            
            //iniciar conexão com o serve reserva.
            try(Socket socketReserva = new Socket(serverPrincialIp, serverPrincialPort[1])) {
                enviarArquivo(socketReserva,clientName, fileCaminho);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Servidor Reserva indisponível!");
            }
        }
    }
}
