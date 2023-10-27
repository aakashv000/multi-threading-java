package org.example.thread.lock;

import java.util.ArrayList;
import java.util.List;
import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ReentrantReadWriteLock {
    public static void main(String[] args) throws InterruptedException {
        final int MAX_PRICE = 1000;

        Random random = new Random();

        // init inventory database
        InventoryDatabase inventoryDatabase = new InventoryDatabase();
        for (int i = 0; i < 100000; i++) {
            inventoryDatabase.addItem(random.nextInt(MAX_PRICE));
        }

        // writer thread
        Thread writer = new Thread(() -> {
            while (true) {
                inventoryDatabase.addItem(random.nextInt(MAX_PRICE));
                inventoryDatabase.removeItem(random.nextInt(MAX_PRICE));

                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) { throw new RuntimeException(e); }
            }
        });
        writer.setDaemon(true);
        writer.start();

        // reader threads
        final int N_READER_THREADS = 7;

        List<Thread> readers = new ArrayList<>();
        for (int i : IntStream.range(0, 7).boxed().collect(Collectors.toList())) {
            Thread reader = new Thread(() -> {
                for (int j : IntStream.range(0, 100000).toArray()) {
                    int upperBound = random.nextInt(MAX_PRICE);
                    int lowerBound = upperBound > 0 ? random.nextInt(upperBound) : 0;

                    inventoryDatabase.getNumberOfItemsInPriceRange(lowerBound, upperBound);
                }
            });
            reader.setDaemon(true);
            readers.add(reader);
        }

        // performance measurement
        long startReadTime = System.currentTimeMillis();

        for (Thread t: readers)
            t.start();

        // wait for all threads to finish
        for (Thread t: readers)
            t.join();

        long endReadTime = System.currentTimeMillis();
        System.out.println("Total reading time = " + (endReadTime - startReadTime));
    }
}

/**
 * contains BST of nodes having (price, quantity) key-value pair
 */
class InventoryDatabase {
    private TreeMap<Integer, Integer> priceCountMap = new TreeMap<>();

    private ReentrantLock reentrantLock = new ReentrantLock();

    public int getNumberOfItemsInPriceRange (int lowerBound, int upperBound) {
        reentrantLock.lock();
        try {
            Integer fromKey = priceCountMap.ceilingKey(lowerBound);
            Integer toKey = priceCountMap.floorKey(upperBound);
            if (fromKey == null || toKey == null) return 0;

            NavigableMap<Integer, Integer> rangeMap = priceCountMap.subMap(fromKey, true, toKey, true);

            int sum = rangeMap.values().stream().reduce(0, (cumu, curr) -> cumu + curr);
            return sum;
        }
        finally {
            reentrantLock.unlock();
        }
    }

    public void addItem (int price) {
        reentrantLock.lock();
        try {
            Integer nItemsForPrice = priceCountMap.getOrDefault(price, 0);
            priceCountMap.put(price, nItemsForPrice + 1);
        }
        finally {
            reentrantLock.unlock();
        }
    }

    public void removeItem (int price) {
        reentrantLock.lock();
        try {
            Integer nItemsForPrice = priceCountMap.get(price);
            if (nItemsForPrice == null || nItemsForPrice == 1)
                priceCountMap.remove(price);
            else
                priceCountMap.put(price, nItemsForPrice - 1);
        }
        finally {
            reentrantLock.unlock();
        }
    }
}
