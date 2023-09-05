package org.example.thread.coordinate;

import java.math.BigInteger;

public class TerminationByCheck_Daemon {
    public void exec() throws InterruptedException {
        Thread t = new Thread(
                new LongComputationTask(3L, 500000L)
        );

        t.setDaemon(true);
        t.start();

        Thread.sleep(1000); // ensuring to let it enter run() but not finish its computation

        // exit immediately, as it is daemon thread;
        // even though no interrupt checking is present
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
            System.out.println("LongComputationTask.run - " + Thread.currentThread());
            System.out.println(base+"^"+power+" = "+pow(base, power));
        }

        private BigInteger pow(long base, long power) {
            BigInteger result = BigInteger.ONE;

            for (Long i = 0L; i.compareTo(power) != 0; i = i+1) {
                // removed checking for interrupt signal here

                result = result.multiply(BigInteger.valueOf(base));
            }
            return result;
        }
    }
}
