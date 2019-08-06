import java.io.*;
import java.net.*;

/**
 * DailyAdviceClient
 */
public class DailyAdviceClient {
    public void go() {
        try {
            Socket socket = new Socket("127.0.0.1", 4242);
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
        DailyAdviceClient client = new DailyAdviceClient();
        client.go();
    }
}
