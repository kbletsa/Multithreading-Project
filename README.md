# Big Data Analysis Technologies - Multithreading

This repository contains the implementation for the 1st Assignment of the "Big Data Analysis Technologies" course . The project focuses on **Java Multithreading**, **Process Synchronization**, and **Socket Programming** .

## Project Structure

### 1. Matrix-Vector Multiplication (Multithreading)
* **Goal:** Compare execution time of matrix-vector multiplication using different thread counts (1, 2, 4, 8) to analyze concurrency benefits .
* **Implementation:**
  * Uses `ExecutorService` to manage a pool of threads .
  * Splits matrix rows among threads for parallel processing .
  * Measures execution time in milliseconds using `System.nanoTime()` .

### 2. Hospital Management (Synchronization)
* **Goal:** Simulate a hospital system handling patient admissions and discharges while preventing race conditions .
* **Implementation:**
  * **Threads:** Separate `DiseaseThread` (generates cases) and `HospitalThread` (releases patients) .
  * **Safety:** Uses `synchronized` blocks/methods and `AtomicInteger` to protect critical sections (bed capacity, statistics) .

### 3. Client-Server Hashtable (Sockets)
* **Goal:** Implement a Client-Server architecture to manage a `Hashtable<Integer, Integer>` remotely .
* **Implementation:**
  * **Server:** Listens on port `8888`. Handles Insert (1), Delete (2), and Search (3) commands .
  * **Client:** Connects via TCP Sockets and sends user commands via `DataOutputStream` .

### 4. Producer-Consumer System (IPC)
* **Goal:** Create a multi-process system with multiple Producers and Consumers accessing a shared inventory .
* **Implementation:**
  * **Server:** Uses a `CachedThreadPool` to handle multiple concurrent connections on ports 8881-8883 (Producers) and 9991-9993 (Consumers) .
  * **Logic:** Producers add to storage (limit 1000); Consumers remove from storage (limit 0). Shared storage is thread-safe using locks .

---

## How to Run

### Task 3 (Client-Server)
1.  **Server:** Run `Server.java` with program argument: `8888` .
2.  **Client:** Run `Client.java` with program arguments: `localhost 8888` .

### Task 4 (Producer-Consumer)
1.  Run `Server_CP` to start listening for connections .
2.  Run `Producer` and `Consumer` instances to simulate traffic .
