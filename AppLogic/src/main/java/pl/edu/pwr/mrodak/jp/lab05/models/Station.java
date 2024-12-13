package pl.edu.pwr.mrodak.jp.lab05.models;

import java.util.concurrent.Semaphore;

public class Station {
    private final Semaphore semaphore; //blocking mechanism
    private int id;
    private Hose[] waterHoses;
    private Hose[] soapHoses;

    public Station() {
        this.semaphore = new Semaphore(1, true);
    }

    public Semaphore getSemaphore() {
        return semaphore;
    }
}