package org.example.thread.create;

public class CreationByInheritanceConcept {
    public static void parent() {
        Thread thread = new SubThread();

        thread.start();
    }

    private static class SubThread extends Thread {
        public void run(){
            System.out.println("SubThread.run - " + this);
        }
    }
}
