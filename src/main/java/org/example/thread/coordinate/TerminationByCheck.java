package org.example.thread.coordinate;

import java.math.BigInteger;

public class TerminationByCheck {
    public void exec() {
        Thread t = new Thread(
                new LongComputationTask(2L, 500000L)
        );

        t.start();

        // to exit immediately if interrupt is checked vs no effect
        t.interrupt();
    }

    private class LongComputationTask implements Runnable {
        private long base;
        private long power;

        LongComputationTask(long base, long power) {
            this.base = base;
            this.power = power;
        }

        @Override
        public void run() {
            System.out.println(base+"^"+power+" = "+pow(base, power));
        }

        private BigInteger pow(long base, long power) {
            BigInteger result = BigInteger.ONE;

            for (Long i = 0L; i.compareTo(power) != 0; i = i+1) {
                // toggle - checking whether i(thread) am interrupted, by some other thread
                if (Thread.currentThread().isInterrupted()) {
                    System.out.println("Prematurely interrupted computation");
                    return BigInteger.ZERO;
                }

                result = result.multiply(BigInteger.valueOf(base));
            }
            return result;
        }
    }
}
