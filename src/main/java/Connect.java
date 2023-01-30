import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Connect extends Thread {

    private static final String COMMAND_CLOSE_CHAT = "\\exit";

    private final Socket clientSocket;
    private final BufferedReader in;
    private final BufferedWriter out;
    private String clientName;

    public Connect(Socket clientSocket) throws IOException {
        this.clientSocket = clientSocket;
        this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        this.out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
        start();
    }

    @Override
    public void run() {
        try {
            this.clientName = in.readLine();
            System.out.printf("New connection accepted: %s, %s%n", this.clientName, clientSocket);
            sendAllClients(String.format("(" + getDateWrite() + ") " + "В чат добавился %s.", this.clientName
                    + ". Добро пожаловать!"));
            /*try {
                out.write("Hello, " + this.clientName + "!\n");
                out.flush();
            } catch (IOException ignored) {
            }*/
            try {
                while (true) {
                    String text = in.readLine();
                    if (COMMAND_CLOSE_CHAT.equals(text)) {
                        this.downService();

                        sendAllClients("(" + getDateWrite() + ") " + clientName + "`s out.");
                        break;
                    }
                    System.out.println("Echoing: " + text);
                    sendAllClients(text);
                }
            } catch (NullPointerException ignored) {
            }
        } catch (IOException e) {
            this.downService();
        }
    }

    private void sendAllClients(String msg) {
        for (Connect connect : Server.connects) {
            /*if (this.clientSocket.equals(connect.clientSocket))// для неотправки сообщения самому себе
                continue;*/
            connect.send(msg);
        }
    }

    private void send(String msg) {
        try {
            out.write(String.format("%s\n", msg));
            out.flush();
        } catch (IOException ignored) {
        }
    }

    private void downService() {
        try {
            if (!clientSocket.isClosed()) {
                clientSocket.close();
                in.close();
                out.close();
                for (Connect connect : Server.connects) {
                    if (connect.equals(this))
                        connect.interrupt();
                    Server.connects.remove(this);
                }
                System.out.printf("Connection closed: %s, %s%n", clientName, clientSocket);
            }
        } catch (IOException ignored) {
        }
    }

    private String getDateWrite() {
        Date time = new Date();
        SimpleDateFormat dt1 = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        return dt1.format(time);
    }
}