package org.example;

import org.example.thread.Creation;
import org.example.thread.CreationByInheritanceConcept;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("Hello world!");

        // # thread - creation:

        // init
        Creation.creation();
        // Exception handling
        Creation.debugging();

        // by inheritance
        CreationByInheritanceConcept.parent();
    }
}