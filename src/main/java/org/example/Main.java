package org.example;

import org.example.thread.Creation;
import org.example.thread.CreationByInheritanceConcept;
import org.example.thread.CreationByInheritanceExample;

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
        // by inheritance - example
        (new CreationByInheritanceExample()).exec();
    }
}