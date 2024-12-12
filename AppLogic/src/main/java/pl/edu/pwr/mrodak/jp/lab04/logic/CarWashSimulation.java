package pl.edu.pwr.mrodak.jp.lab04.logic;

import pl.edu.pwr.mrodak.jp.lab04.models.Car;
import pl.edu.pwr.mrodak.jp.lab04.models.Controller;

import java.util.concurrent.*;

public class CarWashSimulation {

    public void runSimulation() {
        System.out.println("Simulation is running...");

        // Parameters of Simulation
        int numberOfCars = 3;
        int numStations = 2;

        // Queues to the Entrance
        BlockingQueue<Car> entranceQueue1 = new LinkedBlockingQueue<>();
        BlockingQueue<Car> entranceQueue2 = new LinkedBlockingQueue<>();

        // Creating Washing Stations
        Semaphore[] stations = new Semaphore[numStations];
        for (int i = 0; i < numStations; i++) {
            stations[i] = new Semaphore(1, true);
        }

        // Create and start the Controller
        Controller controller = new Controller(entranceQueue1, entranceQueue2, stations);
        Thread controllerThread = new Thread(controller);
        controllerThread.start();

        // Create a thread pool for car threads
        ExecutorService carThreadPool = Executors.newFixedThreadPool(numberOfCars);

        // Create and start Car threads
        for (int i = 0; i < numberOfCars; i++) {
            Car car = new Car(i, entranceQueue1, entranceQueue2);
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