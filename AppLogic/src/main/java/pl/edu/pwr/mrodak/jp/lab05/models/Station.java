package pl.edu.pwr.mrodak.jp.lab05.models;

import java.util.concurrent.Semaphore;

public class Station {
    private final Semaphore semaphore;
    private int id;
    private Hose[] waterHoses;
    private Hose[] soapHoses;
    private Car currentCar; // Add a field to track the current car

    public Station(int id, Hose[] waterHoses, Hose[] soapHoses){
        this.id = id;
        this.semaphore = new Semaphore(1, true);
        this.waterHoses = waterHoses;
        this.soapHoses = soapHoses;
    }

    public Semaphore getSemaphore() {
        return semaphore;
    }

    public Hose[] getSoapHoses() {
        return soapHoses;
    }
    public Hose[] getWaterHoses() {
        return waterHoses;
    }

    public int getId() {
        return id;
    }

    public Car getCurrentCar() {
        return currentCar;
    }

    public void setCurrentCar(Car currentCar) {
        this.currentCar = currentCar;
    }
}
