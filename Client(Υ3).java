import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {

    public Client(String host, int port) {
        try (Socket socket = new Socket(host, port);
             DataOutputStream out = new DataOutputStream(socket.getOutputStream());
             DataInputStream in = new DataInputStream(socket.getInputStream());
             Scanner scanner = new Scanner(System.in)) {

            System.out.println("Συνδέθηκε με τον server " + host + ":" + port);

            while (true) {
                System.out.println("Δώσε εντολή (A B C). A=0(exit),1(insert),2(delete),3(search):");
                int A = scanner.nextInt();
                int B = 0, C = 0;

                if (A != 0) {
                    System.out.print("B (key): ");
                    B = scanner.nextInt();
                    if (A == 1) { // insert χρειάζεται και τιμή
                        System.out.print("C (value): ");
                        C = scanner.nextInt();
                    }
                }

                // Στέλνουμε την τριάδα (A,B,C)
                out.writeInt(A);
                out.writeInt(B);
                out.writeInt(C);

                if (A == 0) {
                    System.out.println("Τερματισμός client...");
                    break;
                }

                // Λήψη απάντησης
                int response = in.readInt();
                System.out.println("Απάντηση server: " + response);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Χρήση: java Client <host> <port>");
            return;
        }
        String host = args[0];
        int port = Integer.parseInt(args[1]);
        new Client(host, port);
    }
}

