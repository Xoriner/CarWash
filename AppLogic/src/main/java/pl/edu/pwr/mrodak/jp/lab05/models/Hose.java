package pl.edu.pwr.mrodak.jp.lab05.models;

import java.util.concurrent.Semaphore;

public class Hose {
    private int idOfStationUsingHose = -1; // Station number
    private final String type; // "Soap" or "Water"
    private final Semaphore semaphore;

    public Hose(String type) {
        this.type = type;
        this.semaphore = new Semaphore(1); // Only one thread can use this hose at a time
    }

    public int getIdOfStationUsingHose() {
        return idOfStationUsingHose;
    }

    public void setIdOfStationUsingHose(int idOfStationUsingHose) {
        this.idOfStationUsingHose = idOfStationUsingHose;
    }

    public Semaphore getSemaphore() {
        return semaphore;
    }

    public String getType() {
        return type;
    }
}
