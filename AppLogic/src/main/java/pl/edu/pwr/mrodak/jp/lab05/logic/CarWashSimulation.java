package pl.edu.pwr.mrodak.jp.lab05.logic;

import pl.edu.pwr.mrodak.jp.lab05.models.Car;
import pl.edu.pwr.mrodak.jp.lab05.models.Controller;
import pl.edu.pwr.mrodak.jp.lab05.models.Hose;
import pl.edu.pwr.mrodak.jp.lab05.models.Station;

import java.util.concurrent.*;

public class CarWashSimulation {
    private BlockingQueue<Car> entranceQueue1;
    private BlockingQueue<Car> entranceQueue2;

    public BlockingQueue<Car> getShortestQueue() {
        if (entranceQueue1.size() > entranceQueue2.size()) {
            return entranceQueue2;
        } else {
            return entranceQueue1;
        }
    }

    public BlockingQueue<Car> getEntranceQueue1() {
        return entranceQueue1;
    }

    public void runSimulation() {
        System.out.println("Simulation is running...");

        // Parameters of Simulation
        int numberOfCars = 6;
        int numStations = 3;

        // Queues to the Entrance
        entranceQueue1 = new LinkedBlockingQueue<>();
        entranceQueue2 = new LinkedBlockingQueue<>();

        Hose[] waterHoses = new Hose[numStations-1];
        Hose[] soapHoses = new Hose[numStations-1];

        for (int i=0; i<numStations-1;i++){
            waterHoses[i] = new Hose("water");
            soapHoses[i] = new Hose("soap");
        }

        // Creating Washing Stations
        Station[] stations = new Station[numStations];
        for (int i = 0; i < numStations; i++) {
            stations[i] = new Station();
        }


        // Create and start the Controller
        Controller controller = new Controller(entranceQueue1, entranceQueue2, stations);
        Thread controllerThread = new Thread(controller);
        controllerThread.start();

        // Create a thread pool for car threads
        ExecutorService carThreadPool = Executors.newFixedThreadPool(numberOfCars);

        // Create and start Car threads
        for (int i = 0; i < numberOfCars; i++) {
            BlockingQueue<Car> shortestQueue = getShortestQueue();
            Car car = new Car(i, shortestQueue);
            carThreadPool.submit(car);
            try {
                // Sleep for 1 second plus a random amount of time (0 to 1000 milliseconds)
                Thread.sleep(1000 + ThreadLocalRandom.current().nextInt(1000));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // Shutdown the thread pool after finishing
        carThreadPool.shutdown();
        try {
            if (!carThreadPool.awaitTermination(60, TimeUnit.SECONDS)) {
                carThreadPool.shutdownNow();
            }
        } catch (InterruptedException e) {
            carThreadPool.shutdownNow();
            Thread.currentThread().interrupt();
        }

        // Stop the controller thread
        controller.stop();
        controllerThread.interrupt();

        // Set references to null to ensure they are eligible for garbage collection
        entranceQueue1 = null;
        entranceQueue2 = null;
        stations = null;
        controller = null;
    }
}