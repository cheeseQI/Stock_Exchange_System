package testing;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Client implements Runnable {
    private static final String SERVER_IP = "127.0.0.1";
    private static final int SERVER_PORT = 12345;
    private String xmlFilePath;

    public Client(String xmlFilePath) {
        this.xmlFilePath = xmlFilePath;
    }

    public static void main(String[] args) {
        String xmlFilePath = "src/test/resources/load_test1.txt";
        int numberOfClients = 10;
        for (int i = 0; i < numberOfClients; i++) {
            Client client = new Client(xmlFilePath);
            Thread clientThread = new Thread(client);
            clientThread.start();
        }
    }

    @Override
    public void run() {
        try {
            Socket socket = new Socket(SERVER_IP, SERVER_PORT);

            // 从本地文件读取XML字符串
            String xml = readXMLFromFile(xmlFilePath);

            byte[] xmlBytes = xml.getBytes(StandardCharsets.UTF_8);
            int xmlLength = xmlBytes.length;
            long startTime = System.currentTimeMillis();
            OutputStream outputStream = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(outputStream, true);
            writer.println(xmlLength);
            outputStream.write(xmlBytes);
            outputStream.flush();
            System.out.println("send successfully");
            InputStream inputStream = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            String line;
            StringBuilder response = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                response.append(line);
                response.append(System.lineSeparator());
            }
            //String response = reader.readLine();
            long endTime = System.currentTimeMillis();
            long elapsedTime = endTime - startTime;
                        System.out.println("Elapsed time: " + elapsedTime + " milliseconds");
            System.out.println("Response from the server: " + response.toString());
            socket.close();
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private String readXMLFromFile(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        byte[] bytes = Files.readAllBytes(path);
        return new String(bytes, StandardCharsets.UTF_8);
    }
}
