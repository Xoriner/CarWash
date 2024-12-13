package pl.edu.pwr.mrodak.jp.lab04.models;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class Controller implements Runnable {
    private final BlockingQueue<Car> queue1;
    private final BlockingQueue<Car> queue2;
    private final Semaphore[] stations;
    private final AtomicBoolean running = new AtomicBoolean(true);
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private boolean toggle = true; // To alternate between queues

    public Controller(BlockingQueue<Car> queue1, BlockingQueue<Car> queue2, Semaphore[] stations) {
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
                Car car = null;
                if (toggle) {
                    car = queue1.poll();
                    toggle = false;
                } else {
                    car = queue2.poll();
                    toggle = true;
                }

                if (car != null) {
                    for (int i = 0; i < stations.length; i++) {
                        if (stations[i].tryAcquire()) {
                            synchronized (car) {
                                car.notify();
                            }
                            System.out.println("Controller let car " + car.getId() + " into station " + (i + 1));

                            // Schedule the release of the station after some time
                            final int stationIndex = i;
                            scheduler.schedule(() -> {
                                stations[stationIndex].release();
                                System.out.println("Station " + (stationIndex + 1) + " is now free.");
                            }, 2, TimeUnit.SECONDS);
                            break;
                        }
                    }
                }

                // Sleep to simulate time taken to process each car
                Thread.sleep(10000);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}