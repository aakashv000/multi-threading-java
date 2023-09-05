package org.example.thread.coordinate;

public class Termination {
    public void exec() {
        Thread t = new Thread(new BlockingTask());
        t.start();

        // toggle b/w app's exit waiting for blocking thread & interrupting its sleep
        t.interrupt();
    }

    private class BlockingTask implements Runnable {
        @Override
        public void run() {
            try {
                Thread.sleep(60*1000);
            } catch (Exception e) {
                System.out.println("BlockingTask.run - exiting blocking thread - " + e);
            }
        }
    }
}
