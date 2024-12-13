package pl.edu.pwr.mrodak.jp.lab05.models;

import java.util.concurrent.Semaphore;

public class Station {
    private final Semaphore semaphore; //blocking mechanism
    private int id;
    private Hose[] waterHoses;
    private Hose[] soapHoses;

    public Station(int id, Hose[] waterHoses, Hose[] soapHoses){
        this.id = id;
        this.semaphore = new Semaphore(1, true);
        this.waterHoses = waterHoses;
        this.soapHoses = soapHoses;
    }

    public Semaphore getSemaphore() {
        return semaphore;
    }
}