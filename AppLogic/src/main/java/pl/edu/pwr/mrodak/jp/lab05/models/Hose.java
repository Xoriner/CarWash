package pl.edu.pwr.mrodak.jp.lab05.models;

import java.util.concurrent.Semaphore;

public class Hose {
    private final Semaphore semaphore;
    private final String type; // e.g., "water" or "soap"
    private Car currentCar; // Tracks the car using the hose

    public Hose(String type) {
        this.type = type;
        this.semaphore = new Semaphore(1, true);
    }

    public Semaphore getSemaphore() {
        return semaphore;
    }

    public String getType() {
        return type;
    }

    public synchronized Car getCurrentCar() {
        return currentCar;
    }

    public synchronized void setCurrentCar(Car currentCar) {
        this.currentCar = currentCar;
    }
}
