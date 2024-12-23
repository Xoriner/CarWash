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
        while (!Thread.currentThread().isInterrupted()) { // Keep looping until interrupted
            try {
                System.out.println("Car " + id + " arrives at the car wash.");
                entranceQueue.put(this); // Join the entrance queue

                System.out.println("Car " + id + " joined queue at entrance.");

                // Wait for the controller to notify
                synchronized (this) {
                    wait();
                    System.out.println("Car " + id + " is entering station " + assignedStationId);
                }

                // Notify the station about the current car
                assignedStation.setCurrentCar(this);

                // Sequentially use hoses in the station
                useHoseSequentially("water");
                useHoseSequentially("soap");
                useHoseSequentially("water (final rinse)");

                // Clear the current car from the station
                assignedStation.setCurrentCar(null);

                // Release the station
                assignedStation.getSemaphore().release();
                System.out.println("Car " + id + " finished washing at station " + assignedStationId);
                System.out.println("Station " + assignedStationId + " is now free.");

                // Simulate time before rejoining the queue
                Thread.sleep(10000); // Wait for 5 seconds before rejoining the queue
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void useHoseSequentially(String type) throws InterruptedException {
        Hose[] hoses = type.contains("water") ? assignedStation.getWaterHoses() : assignedStation.getSoapHoses();
        Hose acquiredHose = null;

        while (acquiredHose == null) {
            for (Hose hose : hoses) {
                if (hose.getSemaphore().tryAcquire()) {
                    acquiredHose = hose;
                    break;
                }
            }

            if (acquiredHose == null) {
                // No hoses available, wait and retry
                System.out.println("Car " + id + " is waiting for a free " + type + " hose.");
                Thread.sleep(500); // Wait before retrying
            }
        }

        // Notify the hose that it is in use by this car
        acquiredHose.setCurrentCar(this);

        // Use the hose
        System.out.println("Car " + id + " is using " + type + " hose.");
        Thread.sleep(2500); // Simulate time to use the hose

        // Release the hose
        acquiredHose.setCurrentCar(null); // Clear the hose's current car
        acquiredHose.getSemaphore().release();
        System.out.println("Car " + id + " has released " + type + " hose.");
    }

}

