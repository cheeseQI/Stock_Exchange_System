import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MatchingEngine {
    private static final int PORT = 12345;

    private static class ClientHandler implements Runnable {
        private Socket socket;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                System.out.println("Client connected");

                // Read the XML data from the client
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                StringBuilder xmlData = new StringBuilder();
                String line;
                while (!(line = reader.readLine()).isEmpty()) {
                    xmlData.append(line);
                }

                System.out.println("Received XML data:\n" + xmlData.toString());

                // Process the XML data and generate a response
                String xmlResponse = "<response><status>success</status></response>";

                // Send the XML response to the client
                OutputStream outputStream = socket.getOutputStream();
                PrintWriter writer = new PrintWriter(outputStream, true);
                writer.println(xmlResponse);
                writer.flush(); // Ensure the response is sent

                reader.close();
                writer.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is listening on port " + PORT);

            // Create a fixed-size thread pool
            ExecutorService executorService = Executors.newFixedThreadPool(10);

            while (true) {
                Socket socket = serverSocket.accept();

                // Create a new task for each connected client
                ClientHandler clientHandler = new ClientHandler(socket);

                // Submit the task to the thread pool
                executorService.submit(clientHandler);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
