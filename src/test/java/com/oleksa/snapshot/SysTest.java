package com.oleksa.snapshot;

import org.junit.jupiter.api.Test;

import java.util.concurrent.ForkJoinPool;

public class SysTest {

    @Test
    void hello() {
        ForkJoinPool.commonPool();
        Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run() {
                System.out.println(" well done. now exit");
            }
        });
        System.out.println("firsst line");

        Runtime.getRuntime().exit(-13);
    }
}
