import java.io.*;
import java.net.*;
import java.util.concurrent.*; // Για thread pool

public class Server_CP {
    private int producerPort;
    private int consumerPort;
    private int storage;
    private final Object lock = new Object(); // Για συγχρονισμό
    private ExecutorService executor = Executors.newCachedThreadPool();

    public Server_CP(int producerPort, int consumerPort) {
        this.producerPort = producerPort;
        this.consumerPort = consumerPort;
        this.storage = (int) (Math.random() * 1000) + 1; // Τυχαία αρχική τιμή
    }

    public void start() {
        System.out.println("Server_CP ξεκίνησε. Αρχικό απόθεμα: " + storage);
        new Thread(() -> handleProducers()).start();
        new Thread(() -> handleConsumers()).start();
    }

    // Διαχείριση producers
    private void handleProducers() {
        try (ServerSocket serverSocket = new ServerSocket(producerPort)) {
            System.out.println("Ακρόαση producers στη θύρα: " + producerPort);
            while (true) {
                Socket socket = serverSocket.accept();
                executor.execute(new ProducerHandler(socket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Διαχείριση consumers
    private void handleConsumers() {
        try (ServerSocket serverSocket = new ServerSocket(consumerPort)) {
            System.out.println("Ακρόαση consumers στη θύρα: " + consumerPort);
            while (true) {
                Socket socket = serverSocket.accept();
                executor.execute(new ConsumerHandler(socket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Εσωτερική κλάση για producers
    private class ProducerHandler implements Runnable {
        private Socket socket;

        public ProducerHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

                int addValue = Integer.parseInt(in.readLine());
                synchronized (lock) {
                    if (storage + addValue > 1000) {
                        out.println("Απόθεμα υπερέβη το όριο! Δεν ενημερώνεται.");
                        System.out.println("⚠ Server_CP(" + producerPort + "): Απόθεμα > 1000, προσθήκη απορρίφθηκε.");
                    } else {
                        storage += addValue;
                        out.println("Ενημέρωση επιτυχής. Νέο απόθεμα: " + storage);
                        System.out.println("Producer ➕ " + addValue + " -> νέο απόθεμα: " + storage);
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Εσωτερική κλάση για consumers
    private class ConsumerHandler implements Runnable {
        private Socket socket;

        public ConsumerHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

                int removeValue = Integer.parseInt(in.readLine());
                synchronized (lock) {
                    if (storage - removeValue < 1) {
                        out.println("Απόθεμα πολύ χαμηλό! Δεν ενημερώνεται.");
                        System.out.println("⚠ Server_CP(" + consumerPort + "): Απόθεμα < 1, αφαίρεση απορρίφθηκε.");
                    } else {
                        storage -= removeValue;
                        out.println("Ενημέρωση επιτυχής. Νέο απόθεμα: " + storage);
                        System.out.println("Consumer ➖ " + removeValue + " -> νέο απόθεμα: " + storage);
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Κύρια μέθοδος για εκτέλεση server
    public static void main(String[] args) {
        // Π.χ. δημιουργούμε 3 servers
        int[][] ports = { {8881, 9991}, {8882, 9992}, {8883, 9993} };

        for (int[] pair : ports) {
            new Thread(() -> new Server_CP(pair[0], pair[1]).start()).start();
        }
    }
}

