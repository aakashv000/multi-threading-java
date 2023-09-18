package org.example.thread.resourcesSharing;

/**
 * Observing Stack Frames, corresponding to methods,
 * in a single thread's Stack
 *
 * (Run in debug mode and check Frames section in Debugger)
 */
public class Stack {
    public static void main(String[] args) {
        // 1
        int x = 1;
        int y = 2;

        int result = sum(x, y);

        // 2
        String s1 = "hey";
    }

    private static int sum (int a, int b) {
        int s = a + b;
        return s;
    }
}
