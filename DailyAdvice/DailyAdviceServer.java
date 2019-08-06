import java.io.*;
import java.net.*;

/**
 * DailyAdviceServer
 */
public class DailyAdviceServer {
    String[] adviceList = {"Take smaller bites",
            "Go for the tight jeans. No they do NOT make you look fat.", "One word: inappropriate",
            "Just for today, be honest. Tell your boss what you *really* think",
            "You might want to rethink that haircut."};

    public void go(int port) {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            while (true) {
                Socket socket = serverSocket.accept();
                PrintWriter writer = new PrintWriter(socket.getOutputStream());
                String advice = getAdvice();
                writer.println(advice);
                writer.close();
                System.out.println(advice);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private String getAdvice() {
        int random = (int) (Math.random() * adviceList.length);
        return adviceList[random];
    }

    public static void main(String[] args) {
        int port = 4242;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }
        
        DailyAdviceServer server = new DailyAdviceServer();
        server.go(port);
    }
}
