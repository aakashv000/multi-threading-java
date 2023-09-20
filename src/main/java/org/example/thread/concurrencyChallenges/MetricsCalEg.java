package org.example.thread.concurrencyChallenges;

import java.util.Random;

public class MetricsCalEg {
    public static void main(String[] args) {
        Metrics metrics = new Metrics();

        BusinessLogic businessLogic1 = new BusinessLogic(metrics);
        BusinessLogic businessLogic2 = new BusinessLogic(metrics);

        MetricsPrinter metricsPrinter = new MetricsPrinter(metrics);

        businessLogic1.start();
        businessLogic2.start();
        metricsPrinter.start();
    }


    private static class BusinessLogic extends Thread {
        private Metrics metrics;
//        private Random random = new Random();
        BusinessLogic (Metrics metrics) { this.metrics = metrics; }

        public void run() {
            while (true) {
//                long start = System.currentTimeMillis();
//
//                try {
//                    Thread.sleep(random.nextInt(10));
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                }
//
//                long end = System.currentTimeMillis();
//
//                metrics.addSample(end - start);
                metrics.addSample(10);
            }
        }
    }


    private static class Metrics {
        private long count = 0;
        private volatile double average = 0.0;
//        private double average = 0.0;

        // vv due to synchronized - average is always 10
//        public synchronized void addSample (long sample) {
        // vv w/o it, starts with approx. 9.998 and tends towards 10
        public void addSample (long sample) {
            double oldAverage = average * count;

            count++;
            average = (oldAverage + sample) / count;
        }

        public double getAverage() {
            return this.average;
        }
    }


    private static class MetricsPrinter extends Thread {
        private Metrics metrics;
        MetricsPrinter(Metrics metrics) { this.metrics = metrics; }

        public void run() {
            while (true) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    break;
                }

                double average = metrics.getAverage();
                System.out.println("average = " + average);
            }
        }
    }
}
