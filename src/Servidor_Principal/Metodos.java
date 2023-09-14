package Servidor_Principal;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.InitialDirContext;

public class Metodos {
    private static Map<String, String> clientFolders = new HashMap<>(); // Mapear o nome do cliente para a pasta

    public void receberArquivo(Socket clientServeSocket, String nomeServe) throws IOException {
        
        InputStream is = clientServeSocket.getInputStream();

        // Receber o nome do cliente
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String clientName = new String(br.readLine().getBytes(), "UTF-8");


        // Verificar se a pasta do cliente já foi criada
        String saveDir = clientFolders.get(clientName);
        if (saveDir == null) {
            // Se a pasta ainda não existe, criar uma nova pasta
            String currentDir = System.getProperty("user.dir");
            saveDir = currentDir + File.separator + nomeServe + File.separator + clientName + File.separator;
            
            File directory = new File(saveDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }
            // Adicionar a pasta ao mapa para reutilização futura
            clientFolders.put(clientName, saveDir);
        }

        // Receber o nome do arquivo
        String fileName = new String(br.readLine().getBytes(), "UTF-8");


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
        clientServeSocket.close(); //depois testar sem.
        System.out.println("Cliente: "+clientName+" Saiu!!\n");
    }

    public static String getServerIpFromDns() {
        String servidorDns = "192.168.1.18:52";
        String nomeHost = "servidorsdjorge.dns";
        
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
