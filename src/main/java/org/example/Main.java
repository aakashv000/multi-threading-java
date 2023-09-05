package org.example;

import org.example.thread.Creation;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("Hello world!");

        // thread - creation - init
        Creation.creation();

        // Thread - creation - Exception handling
        Creation.debugging();
    }
}