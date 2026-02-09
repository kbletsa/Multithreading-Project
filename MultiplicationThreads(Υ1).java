
import java.util.Random;
import java.util.concurrent.*;

class MatrixVectorMultiplication {
    private static final int N = 200; // αριθμός γραμμών του πίνακα
    private static final int M = 100; // αριθμός στηλών του πίνακα
    private static final int MAX_VALUE = 10; // μέγιστη τυχαία τιμή

    private static int[][] A = new int[N][M];
    private static int[] v = new int[M];
    private static int[] result = new int[N];

    public static void main(String[] args) {
        Random rand = new Random();

        // Γέμισμα πίνακα και διανύσματος με τυχαίες τιμές
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
                A[i][j] = rand.nextInt(MAX_VALUE + 1) ; // τιμές 0-10
            }
        }

        for (int j = 0; j < M; j++) {
            v[j] = rand.nextInt(MAX_VALUE + 1) ;  // τιμές 0-10
        }

        // Εκτέλεση για διαφορετικό πλήθος νημάτων
        int[] threadCounts = {1, 2, 4, 8};
        for (int numThreads : threadCounts) {
            System.out.println("\n--- Υπολογισμός με " + numThreads + " νήματα ---");
            long startTime = System.nanoTime(); // πιο ακριβής μέτρηση σε nanoseconds
            multiplyWithExecutor(numThreads);
            long endTime = System.nanoTime();

            // Εκτύπωση αποτελεσμάτων χρόνου και μετατροπή σε milliseconds
            printResult();
            System.out.println("Χρόνος εκτέλεσης: " + ((endTime - startTime) / 1_000_000.0) + " ms");
        }
    }



    private static void multiplyWithExecutor(int numThreads) {
        int rowsPerThread = (int) Math.ceil((double) N / numThreads);

        // Δημιουργία thread pool
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        for (int t = 0; t < numThreads; t++) {
            int start = t * rowsPerThread;
            int end = Math.min(start + rowsPerThread, N);

            // Υποβολή εργασίας στο executor για τον πολλαπλασιασμό
            executor.submit(() -> {
                for (int i = start; i < end; i++) {
                    int sum = 0;
                    for (int j = 0; j < M; j++) {
                        sum += A[i][j] * v[j];
                    }
                    result[i] = sum;
                }
            });
        }

        // Τερματισμός και αναμονή να τελειώσουν όλα τα threads
        executor.shutdown();
        try {
            executor.awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Εκτύπωση αποτελεσμάτων πίνακα
    private static void printResult() {
        System.out.print("Αποτέλεσμα: [ ");
        for (int i = 0; i < N; i++) {
            System.out.print(result[i] + " ");
        }
        System.out.println("]");
    }
}
