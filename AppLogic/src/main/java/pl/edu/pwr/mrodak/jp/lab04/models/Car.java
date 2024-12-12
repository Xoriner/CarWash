package pl.edu.pwr.mrodak.jp.lab04.models;

import java.util.concurrent.BlockingQueue;

public class Car implements Runnable {
    private final int id;
    private final BlockingQueue<Car> queue1;
    private final BlockingQueue<Car> queue2;

    public Car(int id, BlockingQueue<Car> queue1, BlockingQueue<Car> queue2) {
        this.id = id;
        this.queue1 = queue1;
        this.queue2 = queue2;
    }

    public int getId() {
        return id;
    }

    @Override
    public void run() {
        try {
            System.out.println("Car " + id + " arrives at the car wash.");

            // Selecting the shortest queue
            BlockingQueue<Car> selectedQueue;
            if (queue1.size() >= queue2.size()) {
                selectedQueue = queue2;
            } else {
                selectedQueue = queue1;
            }
            selectedQueue.put(this);

            System.out.println("Car " + id + " joined queue at entrance " + (selectedQueue == queue1 ? 1 : 2));

            // Waiting for the Controller to let into the Station
            synchronized (this) {
                wait();
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
