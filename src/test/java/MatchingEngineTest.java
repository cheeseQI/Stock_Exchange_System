import org.junit.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.*;

public class MatchingEngineTest {
    private static final String SERVER_IP = "127.0.0.1";
    private static final int SERVER_PORT = 12345;
    @Test
    public void testCommunication() throws IOException {
        Socket socket = new Socket(SERVER_IP, SERVER_PORT);
        System.out.println("Connected to the server");
        // 这里是要发送的XML数据
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "<create>\n" +
                " <account id=\"123457\" balance=\"1000\"/>\n" +
                " <account id=\"123456\" balance=\"1000\"/>\n" +
                " <symbol sym=\"USD\">\n" +
                " <account id=\"123456\">100</account>\n" +
                " <account id=\"123457\">101</account>\n" +
                " </symbol>\n" +
                " <symbol sym=\"CYN\">\n" +
                " <account id=\"123456\">100</account>\n" +
                " </symbol>\n" +
                "</create>";
        byte[] xmlBytes = xml.getBytes(StandardCharsets.UTF_8);
        int xmlLength = xmlBytes.length;

        OutputStream outputStream = socket.getOutputStream();
        PrintWriter writer = new PrintWriter(outputStream, true);
        // 发送XML长度
        writer.println(xmlLength);

        // 发送XML数据
        outputStream.write(xmlBytes);
        outputStream.flush();

        System.out.println("Message sent to the server");

        socket.close();
    }
}