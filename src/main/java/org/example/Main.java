package org.example;

import org.example.thread.coordinate.Join;
import org.example.thread.coordinate.Termination;
import org.example.thread.coordinate.TerminationByCheck;
import org.example.thread.coordinate.TerminationByCheck_Daemon;
import org.example.thread.create.Creation;
import org.example.thread.create.CreationByInheritanceConcept;
import org.example.thread.create.CreationByInheritanceExample;

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
        // by inheritance - example (disabled, as consumes time)
//        (new CreationByInheritanceExample()).exec();

        // # termination:
        new Termination().exec();
        // by explicitly checking for interrupt
        new TerminationByCheck().exec();
        // daemon
        new TerminationByCheck_Daemon().exec();

        // # join
        new Join().exec();
    }
}