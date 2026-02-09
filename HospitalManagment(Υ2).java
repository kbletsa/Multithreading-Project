import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Προσομοίωση διαχείρισης νοσοκομείου με χρήση νημάτων (Threads).
 * Περιλαμβάνει ένα νήμα που παράγει νέα κρούσματα (DiseaseThread)
 * και ένα νήμα που θεραπεύει ασθενείς (HospitalThread).
 * Το πρόγραμμα σταματά μετά από έναν καθορισμένο αριθμό επαναλήψεων
 * που ορίζονται παραμετρικά
 */

public class HospitalManagment {

    static final int ITERATIONS = 30;               // Πόσους κύκλους θα τρέξει η προσομοίωση
    static final int ICU_CAPACITY = 30;             // e: συνολικός αριθμός κλινών στη Μ.Ε.Θ.
    static final int K = 8;                         // μέγιστος αριθμός νέων κρουσμάτων ανά s
    static final int H = 5;                         // μέγιστος αριθμός που θεραπεύονται ανά s (h < k)

    static final int DISEASE_INTERVAL_MS = 2000;    // χρόνος σε δευτερόλεπτα που ερχονται τα κρούσματα
    static final int HOSPITAL_INTERVAL_MS = 4000;   // χρονος σε δευτερόλεπτα που θεραπεύονται

    // Συναρτηση main δημιουργεί νήματα
    public static void main(String[] args) throws InterruptedException {
        Hospital hospital = new Hospital(ICU_CAPACITY);
        SimulationControl control = new SimulationControl();

        Thread diseaseThread = new Thread(new DiseaseThread(hospital, ITERATIONS, K, DISEASE_INTERVAL_MS, control), "DISEASE");
        Thread hospitalThread = new Thread(new HospitalThread(hospital, H, HOSPITAL_INTERVAL_MS, control), "HOSPITAL");

        diseaseThread.start();
        hospitalThread.start();

        diseaseThread.join();
        hospitalThread.join();

        // αποτελέσματα τελικής αναφοράς ποσοι ανάρρωσαν, πόσοι δεν βρήκαν θέση και πόσες κλίνες χρησιμοποιούνται
        System.out.println("\n=== Τελική Αναφορά ===");
        System.out.println("Συνολικές αναρρώσεις: " + hospital.getTotalRecovered());
        System.out.println("Συνολικοί που δεν βρήκαν θέση (turned away): " + hospital.getTotalTurnedAway());
        System.out.println("Κλίνες σε χρήση στο τέλος: " + hospital.getBedsOccupied() + " / " + ICU_CAPACITY);
    }
}

// Κοινό αντικείμενο ελέγχου για τον τερματισμό της προσομοίωσης
class SimulationControl {
    volatile boolean running = true;
}

/**
 * Αντικείμενο που αντιπροσωπεύει το νοσοκομείο και την κατάσταση των κλινών.
 */
class Hospital {
    private final int capacity;
    private int bedsOccupied = 0;

    private final AtomicInteger totalRecovered = new AtomicInteger(0);
    private final AtomicInteger totalTurnedAway = new AtomicInteger(0);

    public Hospital(int capacity) {
        this.capacity = capacity;
    }

    public int admitNewCases(int n) {
        synchronized (this) {
            int free = capacity - bedsOccupied;
            int toAdmit = Math.min(free, n);
            bedsOccupied += toAdmit;
            int turnedAway = n - toAdmit;
            if (turnedAway > 0) {
                totalTurnedAway.addAndGet(turnedAway);
            }
            System.out.printf("[ADMIT] Νέα κρούσματα: %d, Εισήχθησαν: %d, Απέτυχαν εισαγωγή: %d, Κλίνες σε χρήση: %d/%d\n",
                    n, toAdmit, turnedAway, bedsOccupied, capacity);
            return toAdmit;
        }
    }

    public int releasePatients(int n) {
        synchronized (this) {
            int toRelease = Math.min(n, bedsOccupied);
            bedsOccupied -= toRelease;
            totalRecovered.addAndGet(toRelease);
            System.out.printf("[RELEASE] Θεραπεύθηκαν: %d, Κλίνες σε χρήση: %d/%d\n",
                    toRelease, bedsOccupied, capacity);
            return toRelease;
        }
    }

    public int getBedsOccupied() { synchronized (this) { return bedsOccupied; } }
    public int getTotalRecovered() { return totalRecovered.get(); }
    public int getTotalTurnedAway() { return totalTurnedAway.get(); }
}

/**
 * Νήμα που παράγει νέα κρούσματα κάθε DISEASE_INTERVAL_MS.
 */
class DiseaseThread implements Runnable {
    private final Hospital hospital;
    private final int iterations;
    private final int k;
    private final int intervalMs;
    private final SimulationControl control;
    private final Random rnd = new Random();

    public DiseaseThread(Hospital hospital, int iterations, int k, int intervalMs, SimulationControl control) {
        this.hospital = hospital;
        this.iterations = iterations;
        this.k = k;
        this.intervalMs = intervalMs;
        this.control = control;
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < iterations; i++) {
                int newCases = rnd.nextInt(k + 1);
                hospital.admitNewCases(newCases);
                Thread.sleep(intervalMs);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("DiseaseThread διακόπηκε");
        } finally {
            control.running = false;
        }
    }
}

/**
 * Νήμα που θεραπεύει ασθενείς κάθε HOSPITAL_INTERVAL_MS.
 */
class HospitalThread implements Runnable {
    private final Hospital hospital;
    private final int h;
    private final int intervalMs;
    private final SimulationControl control;
    private final Random rnd = new Random();

    public HospitalThread(Hospital hospital, int h, int intervalMs, SimulationControl control) {
        this.hospital = hospital;
        this.h = h;
        this.intervalMs = intervalMs;
        this.control = control;
    }

    @Override
    public void run() {
        try {
            while (control.running) {
                int healed = rnd.nextInt(h + 1);
                hospital.releasePatients(healed);
                Thread.sleep(intervalMs);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("HospitalThread διακόπηκε");
        }
    }
}
