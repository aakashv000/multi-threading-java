package org.example.thread.coordinate;

import org.example.util.Format;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Join {
    public void exec() throws InterruptedException {
        Format.printSeparator();

//        List<Long> inputNums = Arrays.asList(0L, 3434L, 12345L, 1414L, 22L, 123L);
        List<Long> inputNums = Arrays.asList(0L, 1000000L, 3434L, 12345L, 1414L, 22L, 123L);    // edge case - 1 very large input

        // Initialize threads for each input number
        List<FactorialThread> threads = new ArrayList<>();
        for (long inputNum: inputNums) {
            threads.add(new FactorialThread(inputNum));
        }

        // start all threads
        for (Thread thread: threads) {
            thread.setDaemon(true); // to not let even bypassed time-consuming jobs keep the whole program from ending
            thread.start();
        }

        /*
         * Race condition present b/w thread.start() and factorialThread.isFinished().
         * fix - use join()
         */
        for (Thread thread: threads) {
//            thread.join();
            thread.join(2000);  // use timeout in join() to bypass very time-consuming threads/jobs
        }

        // get calculated values from each thread if finished
        for (int i = 0; i < inputNums.size(); i++) {
            FactorialThread factorialThread = threads.get(i);
            long input = inputNums.get(i);

            if (factorialThread.isFinished()){
                System.out.println(input+"! = "+factorialThread.getResult());
            }
            else {
                System.out.println(input+"! = Calculation in progress ...");
            }
        }
    }

    private class FactorialThread extends Thread {
        private long inputNumber;
        private BigInteger result;
        private boolean isFinished = false;

        FactorialThread(long inputNumber) {
            this.inputNumber = inputNumber;
        }

        public void run() {
            this.result = factorial(inputNumber);
            this.isFinished = true;
        }

        private BigInteger factorial (long n) {
            BigInteger tempResult = BigInteger.ONE;
            for (long i = n; i > 0; i--) {
                tempResult = tempResult.multiply(new BigInteger(Long.toString(i)));
            }

            return tempResult;
        }

        public boolean isFinished(){
            return isFinished;
        }

        public BigInteger getResult() {
            return result;
        }
    }
}
