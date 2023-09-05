package Servidor_Cliente;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import javax.swing.JOptionPane;

public class Metodos {
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
