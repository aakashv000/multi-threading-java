package org.example.thread.concurrencyChallenges;

public class DataRace {
    public static void main(String[] args) {
        int ITER = 1000000;

        SharedClass sharedClass = new SharedClass();

        Thread incThread = new Thread(() -> {
            for (int i = 0; i < ITER; i++) {
                sharedClass.increment();
            }
        });

        Thread checkThread = new Thread(() -> {
            for (int i = 0; i < ITER; i++) {
                sharedClass.checkForDataRace();
            }
        });

        incThread.start();
        checkThread.start();
    }


    private static class SharedClass {
        // making a variable volatile, is equivalent to memory fence/barrier
        // and thus, is a solution for our data race here
        private int x;
        private int y;
//        private volatile int x;
//        private volatile int y;

        // 2nd solution - make increment() method synchronized
        private void increment() {
            x++;
            y++;
        }

        private void checkForDataRace() {
            if (y > x) {
                System.out.println("y > x - Data Race");
            }
            // vv ! surprisingly - if we enable the conditionals below,
            // the chances of data race coming for same no. of iterations decreases
//            else {
//                if (y < x) { System.out.print("<\t"); }
//                else { System.out.print("."); }
//            }
        }
    }
}
