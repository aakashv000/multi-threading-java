package org.example;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("Hello world!");

        Thread t = new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("In thread: " + Thread.currentThread());
                        System.out.println("Current thread priority = " + Thread.currentThread().getPriority());
                    }
                }
        );

        t.setName("My thread first");
        t.setPriority(Thread.MAX_PRIORITY);

        System.out.println("Before starting custom thread. Thread = " + Thread.currentThread());
        t.start();
        System.out.println("After starting custom thread. Thread = " + Thread.currentThread());

        Thread.sleep(5*1000);
    }
}