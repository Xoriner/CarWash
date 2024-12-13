package pl.edu.pwr.mrodak.jp.lab05.models;

import java.util.concurrent.BlockingQueue;

public class Car implements Runnable {
    private final int id;
    private final BlockingQueue<Car> entranceQueue;
    private int assignedStationId;
    private Station assignedStation;

    public Car(int id, BlockingQueue<Car> entranceQueue) {
        this.id = id;
        this.entranceQueue = entranceQueue;
    }


    public void setAssignedStationId(int assignedStationId) {
        this.assignedStationId = assignedStationId;
    }

    public int getAssignedStationId() {
        return assignedStationId;
    }

    public void setStation(Station station) {
        this.assignedStation = station;
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

            System.out.println("Car " + id + " joined queue at entrance " + entranceQueue);

            // Waiting for the Controller to let into the Station
            synchronized (this) {
                wait();
                System.out.println("Car " + id + " is being washed.");
            }

            // Washing the car
            Thread.sleep(5000);
            assignedStation.getSemaphore().release();
            System.out.println("Station " + assignedStationId + " is now free.");


        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}