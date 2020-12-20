package com.oleksa.snapshot;

import com.oleksa.snapshot.entity.RandomResponse;
import com.oleksa.snapshot.entity.Room;
import org.springframework.util.StopWatch;

public class AsyncTask implements Runnable {

    private Room room;
    private RandomCaller randomCaller;

    public AsyncTask(Room room, RandomCaller randomCaller) {
        this.room = room;
        this.randomCaller = randomCaller;
    }

    RandomResponse executeCall() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        RandomResponse randomResponse = randomCaller.doNew();
        stopWatch.stop();
        System.out.println("Took " + stopWatch.getLastTaskTimeMillis() + " ms");
        return randomResponse;
    }

    @Override
    public void run() {
        RandomResponse randomResponse = executeCall();
        room.setLocation(randomResponse.getJsonrpc());
    }
}
