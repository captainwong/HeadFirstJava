import java.io.*;
import java.net.*;
import java.util.*;

/**
 * ChatServer
 */
public class ChatServer {
    ArrayList clientOutputStreams;

    public class ClientHandler implements Runnable {
        BufferedReader reader;
        Socket socket;

        public ClientHandler(Socket clientSocket) {
            try {
                socket = clientSocket;
                InputStreamReader iReader = new InputStreamReader(socket.getInputStream());
                reader = new BufferedReader(iReader);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        @Override
        public void run() {
            String message;
            try {
                while ((message = reader.readLine()) != null) {
                    System.out.println("read: " + message);
                    tellEveryone(message);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        int port = 4242;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }
        new ChatServer().go(port);
    }

    public void go(int port) {
        clientOutputStreams = new ArrayList<>();
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            while (true) {
                Socket socket = serverSocket.accept();
                PrintWriter writer = new PrintWriter(socket.getOutputStream());
                clientOutputStreams.add(writer);

                Thread thread = new Thread(new ClientHandler(socket));
                thread.start();
                System.out.println("Got a connection");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (serverSocket) {
                serverSocket.close();
            }
        }
    }

    public void tellEveryone(String message) {
        Iterator it = clientOutputStreams.iterator();
        while (it.hasNext()) {
            try {
                PrintWriter writer = (PrintWriter) it.next();
                writer.println(message);
                writer.flush();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
