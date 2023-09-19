package org.example.thread.concurrencyChallenges;

public class CriticalSection {
    public static void main(String[] args) throws InterruptedException {
        InventoryCounter inventoryCounter = new InventoryCounter();
        IncrementingThread incrementingThread = new IncrementingThread(inventoryCounter);
        DecrementingThread decrementingThread = new DecrementingThread(inventoryCounter);
        
        // run both threads parallelly
        incrementingThread = new IncrementingThread(inventoryCounter);
        decrementingThread = new DecrementingThread(inventoryCounter);

        incrementingThread.start();
        decrementingThread.start();

        incrementingThread.join();
        decrementingThread.join();

        System.out.println("Parallel - InventoryCounter.itemsCount = " + inventoryCounter.getItemsCount());
        // ^ its value is always = 0
        // because both operations, itemsCount++ and itemsCount--, are in same synchronized monitor/lock
    }


    private static class IncrementingThread extends Thread {
        private InventoryCounter inventoryCounter;
        IncrementingThread(InventoryCounter inventoryCounter) { this.inventoryCounter = inventoryCounter; }

        public void run() {
            for (int i = 0; i < 10000; i++) {
                inventoryCounter.increment();
            }
        }
    }

    private static class DecrementingThread extends Thread {
        private InventoryCounter inventoryCounter;
        DecrementingThread(InventoryCounter inventoryCounter) { this.inventoryCounter = inventoryCounter; }

        public void run() {
            for (int i = 0; i < 10000; i++) {
                inventoryCounter.decrement();
            }
        }
    }


    private static class InventoryCounter {
        private int itemsCount = 0;
        private Object lock = new Object();

//        public synchronized void increment() {
        public void increment() {
            synchronized (lock) {
                // critical section - start
                itemsCount++;
                // critical section - end
            }
        }

//        public synchronized void decrement() {
        public void decrement() {
            synchronized (lock) {
                // critical section - start
                itemsCount--;
                // critical section - end
            }
        }

        public int getItemsCount() { return itemsCount; }
    }
}
