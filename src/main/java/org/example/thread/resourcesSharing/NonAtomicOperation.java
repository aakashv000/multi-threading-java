package org.example.thread.resourcesSharing;

public class NonAtomicOperation {
    public static void main(String[] args) throws InterruptedException {
        InventoryCounter inventoryCounter = new InventoryCounter();
        IncrementingThread incrementingThread = new IncrementingThread(inventoryCounter);
        DecrementingThread decrementingThread = new DecrementingThread(inventoryCounter);
        
        // run both threads sequentially
        incrementingThread.start();
        incrementingThread.join();
        
        decrementingThread.start();
        decrementingThread.join();

        System.out.println("Sequential - InventoryCounter.itemsCount = " + inventoryCounter.getItemsCount());

        // run both threads parallelly
        incrementingThread = new IncrementingThread(inventoryCounter);
        decrementingThread = new DecrementingThread(inventoryCounter);

        incrementingThread.start();
        decrementingThread.start();

        incrementingThread.join();
        decrementingThread.join();

        System.out.println("Parallel - InventoryCounter.itemsCount = " + inventoryCounter.getItemsCount());
        // ^ its value varies in every run
        // because both operations, itemsCount++ and itemsCount--, are non-atomic
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

        public void increment() {
            itemsCount++;
        }
        public void decrement() {
            itemsCount--;
        }

        public int getItemsCount() { return itemsCount; }
    }
}
