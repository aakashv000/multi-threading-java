package org.example.thread.concurrencyChallenges;

import java.util.Random;

/**
 * Deadlock
 * Eg. - 2 intersecting rail tracks,
 * to ensure that only 1 train should pass the intersection at a time.
 */
public class Deadlock {
    public static void main(String[] args) throws InterruptedException {
        Intersection intersection = new Intersection();
        Thread trainA = new Thread(new TrainA(intersection), "Train-A");
        Thread trainB = new Thread(new TrainB(intersection), "Train-B");

        trainA.start();
//        Thread.sleep(50);
        trainB.start();
    }
}


class Intersection {
    private Object roadA = new Object();
    private Object roadB = new Object();

    void passIntersectionByRoadA() {
        synchronized (roadA) {
            System.out.println("Road A is initially locked by " + Thread.currentThread().getName());

            synchronized (roadB) {
                System.out.println("Road B is then locked by " + Thread.currentThread().getName());

                System.out.println("Train is passing through intersection via road A - " + Thread.currentThread().getName());
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    void passIntersectionByRoadB() {
        synchronized (roadB) {
            System.out.println("Road B is initially locked by " + Thread.currentThread().getName());

            synchronized (roadA) {
                System.out.println("Road A is then locked by " + Thread.currentThread().getName());

                System.out.println("Train is passing through intersection via road B - " + Thread.currentThread().getName());
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}



class TrainA implements Runnable {
    private Intersection intersection;
    private Random random = new Random();
    TrainA (Intersection intersection) { this.intersection = intersection; }

    public void run() {
        while (true) {
//            int sleepingTime = random.nextInt(10);
            int sleepingTime = 1;

            try {
                Thread.sleep(sleepingTime*100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            intersection.passIntersectionByRoadA();
        }
    }
}


class TrainB implements Runnable {
    private Intersection intersection;
    private Random random = new Random();
    TrainB (Intersection intersection) { this.intersection = intersection; }

    public void run() {
        while (true) {
//            int sleepingTime = random.nextInt(10);
            int sleepingTime = 3;

            try {
                Thread.sleep(sleepingTime*100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            intersection.passIntersectionByRoadB();
        }
    }
}