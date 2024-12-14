package pl.edu.pwr.mrodak.jp.lab05.models;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class Controller implements Runnable {
    private final BlockingQueue<Car> queue1;
    private final BlockingQueue<Car> queue2;
    private final Station[] stations;
    private final AtomicBoolean running = new AtomicBoolean(true); // to run until shutdown
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private boolean toggle = true; // To alternate between queues

    public Controller(BlockingQueue<Car> queue1, BlockingQueue<Car> queue2, Station[] stations) {
        this.queue1 = queue1;
        this.queue2 = queue2;
        this.stations = stations;
    }

    public void stop() {
        running.set(false);
        scheduler.shutdown();
    }

    @Override
    public void run() {
        try {
            while (running.get()) {
                Car car = getNextCarFromQueues(); // get the next car from the queues

                boolean stationFound = false;
                int foundStationId = 0;
                if (car != null) {
                    for (int i = 0; i < stations.length; i++) {
                        // we use tryAcquire() because Acquire() would block the thread until that specific Station is available
                        // other Stations might get released first and we want to check if the car can be assigned to any of them
                        if (stations[i].getSemaphore().tryAcquire()) {
                            stationFound = true;
                            foundStationId = i;
                            break;
                        }
                    }
                }

                if (stationFound) {
                    // remove the car from the queue
                    removeCarFromQueue();

                    // synchronize on the car object to notify the car
                    synchronized (car) {
                        System.out.println("Controller let car " + car.getId() + " into station " + foundStationId);
                        car.setAssignedStationId(foundStationId);
                        car.setStation(stations[foundStationId]);
                        car.notify();
                    }

                    // Schedule the release of the station
//                    int finalFoundStationId = foundStationId;
//                    scheduler.schedule(() -> {
//                        stations[finalFoundStationId].getSemaphore().release();
//                        System.out.println("Station " + finalFoundStationId + " is now free.");
//                    }, 10, TimeUnit.SECONDS);
                }

                // Sleep to simulate time taken to process each car
                Thread.sleep(5000);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private Car getNextCarFromQueues() {
        Car car = null;
        if (toggle) {
            car = queue1.peek();
            toggle = false;
        } else {
            car = queue2.peek();
            toggle = true;
        }
        return car;
    }

    private void removeCarFromQueue() {
        if (!toggle) {
            queue1.poll();
        } else {
            queue2.poll();
        }
    }

}