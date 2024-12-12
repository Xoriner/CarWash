package pl.edu.pwr.mrodak.jp.lab04.models;

import java.util.concurrent.BlockingQueue;

public class Car implements Runnable {
    private final int id;
    private final BlockingQueue<Car> entranceQueue;

    public Car(int id, BlockingQueue<Car> entranceQueue) {
        this.id = id;
        this.entranceQueue = entranceQueue;
    }

    public int getId() {
        return id;
    }

    @Override
    public void run() {
        try {
            System.out.println("Car " + id + " arrives at the car wash.");

            // Adding car to the queue
            entranceQueue.put(this);

            System.out.println("Car " + id + " joined the queue.");

            // Waiting for the Controller to let into the Station
            synchronized (this) {
                wait();
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}