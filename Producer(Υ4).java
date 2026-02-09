import java.io.*;
import java.net.*;
import java.util.*;

public class Producer {
    private static final int[] PORTS = {8881, 8882, 8883};
    private static final String HOST = "localhost";

    public static void main(String[] args) throws Exception {
        Random random = new Random();

        while (true) {
            int port = PORTS[random.nextInt(PORTS.length)];
            int value = random.nextInt(91) + 10; // 10–100
            System.out.println("Producer -> Σύνδεση στον server στη θύρα " + port + " για προσθήκη " + value);

            try (Socket socket = new Socket(HOST, port);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                out.println(value);
                System.out.println("Απάντηση Server: " + in.readLine());

            } catch (IOException e) {
                System.out.println("Σφάλμα σύνδεσης με server στη θύρα " + port);
            }

            int sleep = random.nextInt(10) + 1;
            Thread.sleep(sleep * 1000);
        }
    }
}

