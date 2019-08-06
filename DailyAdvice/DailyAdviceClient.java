import java.io.*;
import java.net.*;

/**
 * DailyAdviceClient
 */
public class DailyAdviceClient {
    public void go(String ip, int port) {
        try {
            Socket socket = new Socket(ip, port);
            InputStreamReader streamReader = new InputStreamReader(socket.getInputStream());
            BufferedReader reader = new BufferedReader(streamReader);
            String advice = reader.readLine();
            System.out.println("Today you sould: " + advice);
            reader.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String ip = "127.0.0.1";
        int port = 4242;
        if (args.length > 1) {
            ip = args[0];
            port = Integer.parseInt(args[1]);
        }
        
        DailyAdviceClient client = new DailyAdviceClient();
        client.go(ip, port);
    }
}
