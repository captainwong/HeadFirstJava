import java.io.*;
import java.net.*;
import java.util.*;

/**
 * Server
 */
public class Server {

    ArrayList<ObjectOutputStream> clientOutputStreams;

    public static void main(String[] args) {
        int port = 4242;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }
        new Server().go(port);
    }

    public class ClientHandler implements Runnable {
        ObjectInputStream in;
        Socket socket;

        public ClientHandler(Socket socket) {
            try {
                this.socket = socket;
                in = new ObjectInputStream(this.socket.getInputStream());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        @Override
        public void run() {
            Object o1 = null;
            Object o2 = null;
            try {
                while ((o1 = in.readObject()) != null) {
                    o2 = in.readObject();
                    System.out.println("read two objects");
                    tellEveryOne(o1, o2);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void go(int port) {
        clientOutputStreams = new ArrayList<>();
        try {
            ServerSocket serverSocket = new ServerSocket(port);

            while (true) {
                Socket socket = serverSocket.accept();
                ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                clientOutputStreams.add(outputStream);

                Thread t = new Thread(new ClientHandler(socket));
                t.start();

                System.out.println("Got a connection");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void tellEveryOne(Object o1, Object o2) {
        Iterator it = clientOutputStreams.iterator();
        while (it.hasNext()) {
            try {
                ObjectOutputStream out = (ObjectOutputStream) it.next();
                out.writeObject(o1);
                out.writeObject(o2);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
