package org.example.thread.create;

/**
 * creation - by creating Thread class object directly
 * by implicitly extending/implementing Runnable class and passing it to above
 */
public class Creation {
    public static void creation() throws InterruptedException {
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

    public static void debugging() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                throw new RuntimeException("Intentional Exp.");
            }
        });

        thread.setName("Misbehavior");
        thread.setUncaughtExceptionHandler(
                new Thread.UncaughtExceptionHandler() {
                    @Override
                    public void uncaughtException(Thread t, Throwable e) {
                        System.out.println("Error in thread " + t
                                + ". Exception: " + e);
                    }
                }
        );

        thread.start();
    }
}
