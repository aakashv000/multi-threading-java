package org.example.thread.concurrencyChallenges;

public class CriticalSection {
    public static void main(String[] args) throws InterruptedException {
        InventoryCounter inventoryCounter = new InventoryCounter();
        IncrementingThread incrementingThread = new IncrementingThread(inventoryCounter);
        DecrementingThread decrementingThread = new DecrementingThread(inventoryCounter);
        IncDecThread incDecThread = new IncDecThread(inventoryCounter);

        // run all threads parallelly
        incrementingThread.start();
        decrementingThread.start();
        incDecThread.start();

        incrementingThread.join();
        decrementingThread.join();
        incDecThread.join();

        System.out.println("\nParallel - InventoryCounter.itemsCount = " + inventoryCounter.getItemsCount());
        // ^ its value is always = 0
        // only when, both non-atomic operations, itemsCount++ and itemsCount--
        // are in SAME synchronized monitor/lock
    }


    private static class IncrementingThread extends Thread {
        private InventoryCounter inventoryCounter;
        IncrementingThread(InventoryCounter inventoryCounter) {
            this.inventoryCounter = inventoryCounter;
            setName("|");
        }

        public void run() {
            for (int i = 0; i < 10000; i++) {
                inventoryCounter.increment();
            }
        }
    }

    private static class IncDecThread extends Thread {
        private InventoryCounter inventoryCounter;
        IncDecThread(InventoryCounter inventoryCounter) {
            this.inventoryCounter = inventoryCounter;
            setName("z");
        }

        public void run() {
            for (int i = 0; i < 10000; i++) {
                inventoryCounter.incDec();
            }
        }
    }

    private static class DecrementingThread extends Thread {
        private InventoryCounter inventoryCounter;
        DecrementingThread(InventoryCounter inventoryCounter) {
            this.inventoryCounter = inventoryCounter;
            setName("-");
        }

        public void run() {
            for (int i = 0; i < 10000; i++) {
                inventoryCounter.decrement();
            }
        }
    }


    private static class InventoryCounter {
        private int itemsCount = 0;
//        private Object lock = new Object();
        private Object lock_1 = new Object();
        private Object lock_2 = new Object();

//        public synchronized void increment() {
        public void increment() {
//            synchronized (lock) {
            synchronized (this.lock_1) {
                // critical section - start
                itemsCount++;
                System.out.printf("^"+Thread.currentThread().getName()+"\t");
                // critical section - end
            }
        }

        public void incDec() {
            synchronized (this.lock_1) {
                increment();

                // when thread in here, with lock 1, goes into decrement() and gets lock 2,
                // does it release lock 1 - no
                // > check if increment() is called by any other thread b/w this thread is inside incDec()
                //      - no, only decrement() by Dec. thread is called in middle of IncDec() of IncDec thread
                decrement();
            }
        }

//        public synchronized void decrement() {
        public void decrement() {
//            synchronized (lock) {
            synchronized (this.lock_2) {
                // critical section - start
                itemsCount--;
                System.out.printf("v"+Thread.currentThread().getName()+"\t");
                // critical section - end
            }
        }

        public int getItemsCount() { return itemsCount; }
    }
}
