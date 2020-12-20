package com.oleksa.snapshot;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;

@Log4j2
public class ValueLatchTest {

    @Test
    public void testLat() throws InterruptedException {
        log.info("started main()");
        ValueLatch<String> stringValueLatch = new ValueLatch<>();

        Thread t1 = new Thread(() -> {
            try {
                log.info("try getting value...");
                String value = stringValueLatch.getValue();
                log.info("got value: " + value);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        Thread t2 = new Thread(() -> {
            log.info("start computing value...");
            try {
                Thread.sleep(1000);
                String value = "Piggybacked value";
                stringValueLatch.setValue(value);
                log.info("value is computed!");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        t1.start();
        t2.start();
        log.info("threads started...");

        t1.join();
        t2.join();

    }
}
