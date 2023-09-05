package org.example.thread.create;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CreationByInheritanceExample {
    private int MAX_PASSWORD = 9999;


    /**
     * start robbery and police countdown
     */
    public void exec() {
        // init vault, with random pass
        Random random = new Random();
        Vault vault = new Vault(random.nextInt(MAX_PASSWORD));

        // init all actors (hackers, police)
        List<Thread> threads = new ArrayList<>();
        threads.add(new AscHackerThread(vault));
        threads.add(new DescHackerThread(vault));
        threads.add(new PoliceThread());

        // start all
        for (Thread thread: threads) {
            thread.start();
        }
    }


    private class Vault {
        private int password;

        Vault (int password) {
            this.password = password;
        }

//        public boolean isPasswordCorrect(int guess) throws InterruptedException {
        public boolean isPasswordCorrect(int guess) {
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            return this.password == guess;
        }
    }


    private abstract class HackerThread extends Thread {
        protected Vault vault;

        HackerThread(Vault vault) {
            this.vault = vault;

            this.setName(this.getClass().getSimpleName());
            this.setPriority(Thread.MAX_PRIORITY);
        }

        @Override
        public void start() {
            System.out.println("Starting thread - " + this);
            super.start();
        }
    }


    private class AscHackerThread extends HackerThread {
        AscHackerThread (Vault vault) {
            super(vault);
        }

        @Override
        public void run() {
            for (int guess = 0; guess < MAX_PASSWORD; guess++) {
                if (vault.isPasswordCorrect(guess)) {
                    System.out.println("AscHackerThread.run - " + this + " guessed password " + guess);
                    System.exit(0);
                }
            }
        }
    }


    private class DescHackerThread extends HackerThread {
        DescHackerThread(Vault vault) {
            super(vault);
        }

        @Override
        public void run() {
            for (int guess = MAX_PASSWORD; guess > 0; guess--) {
                if (vault.isPasswordCorrect(guess)) {
                    System.out.println("DescHackerThread.run - " + this + " guessed password " + guess);
                    System.exit(0);
                }
            }
        }
    }


    private class PoliceThread extends Thread {
        @Override
        public void run() {
            for (int i = 10; i > 0; i--) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println(i);
            }

            System.out.println("PoliceThread.run - Caught the hackers");
            System.exit(0);
        }
    }
}
