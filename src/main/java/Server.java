import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

public class Server {
    protected static List<Connect> connects = new LinkedList<>();

    public static void main(String[] args) throws IOException {
        Properties props = new Properties();
        props.load(new FileInputStream("setting.txt"));
        int port = Integer.parseInt(props.getProperty("PORT"));
        System.out.println("Server started");

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            try {
                while (true) {
                    Socket socket = serverSocket.accept();
                    try {
                        connects.add(new Connect(socket));
                    } catch (IOException e) {
                        socket.close();
                    }
                }
            } finally {
                serverSocket.close();
            }
        }
    }
}
