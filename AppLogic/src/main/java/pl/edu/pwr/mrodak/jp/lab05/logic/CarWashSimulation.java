package pl.edu.pwr.mrodak.jp.lab05.logic;

import pl.edu.pwr.mrodak.jp.lab05.models.Car;
import pl.edu.pwr.mrodak.jp.lab05.models.Controller;
import pl.edu.pwr.mrodak.jp.lab05.models.Hose;
import pl.edu.pwr.mrodak.jp.lab05.models.Station;

import java.util.concurrent.*;

public class CarWashSimulation {
    private BlockingQueue<Car> entranceQueue1;
    private BlockingQueue<Car> entranceQueue2;
    private Station[] stations;

    public BlockingQueue<Car> getEntranceQueue1() {
        return entranceQueue1;
    }

    public BlockingQueue<Car> getEntranceQueue2() {
        return entranceQueue2;
    }

    public Station[] getStations() {
        return stations;
    }

    // Setup phase
    public void runSimulationSetup() {
        System.out.println("Setting up simulation...");

        // Parameters of Simulation
        int numStations = 3;

        // Initialize Queues
        entranceQueue1 = new LinkedBlockingQueue<>();
        entranceQueue2 = new LinkedBlockingQueue<>();

        Hose[] waterHoses = new Hose[numStations - 1];
        Hose[] soapHoses = new Hose[numStations - 1];

        for (int i = 0; i < numStations - 1; i++) {
            waterHoses[i] = new Hose("water");
            soapHoses[i] = new Hose("soap");
        }

        // Create Washing Stations
        stations = new Station[numStations];
        for (int i = 0; i < numStations; i++) {
            int hoseCount = i == 0 || i == numStations - 1 ? 1 : 2;
            Hose[] _waterHoses = new Hose[hoseCount];
            Hose[] _soapHoses = new Hose[hoseCount];
            if (i == 0) {
                _waterHoses[0] = waterHoses[0];
                _soapHoses[0] = soapHoses[0];
            } else if (i == numStations - 1) {
                _waterHoses[0] = waterHoses[numStations - 2];
                _soapHoses[0] = soapHoses[numStations - 2];
            } else {
                _waterHoses[0] = waterHoses[i - 1];
                _waterHoses[1] = waterHoses[i];
                _soapHoses[0] = soapHoses[i - 1];
                _soapHoses[1] = soapHoses[i];
            }
            stations[i] = new Station(i, _waterHoses, _soapHoses);
        }
    }

    // Simulation logic phase
    public void runSimulationLogic() {
        System.out.println("Starting simulation logic...");

        // Parameters of Simulation
        int numberOfCars = 5;

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
                Thread.sleep(1000 + ThreadLocalRandom.current().nextInt(1000));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // Shutdown the thread pool after finishing
        // Shutdown the thread pool after finishing
        carThreadPool.shutdown();
        try {
            // Wait indefinitely for car threads to finish
            carThreadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            carThreadPool.shutdownNow();
            Thread.currentThread().interrupt();
        }

        // Stop the controller thread
        controller.stop();
        controllerThread.interrupt();
    }

    private BlockingQueue<Car> getShortestQueue() {
        if (entranceQueue1.size() > entranceQueue2.size()) {
            return entranceQueue2;
        } else {
            return entranceQueue1;
        }
    }
}
