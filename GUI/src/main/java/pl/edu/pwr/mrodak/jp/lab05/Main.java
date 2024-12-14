package pl.edu.pwr.mrodak.jp.lab05;

import pl.edu.pwr.mrodak.jp.lab05.logic.CarWashSimulation;

public class Main {
    public static void main(String[] args) {
        ConfigWindow configWindow = new ConfigWindow();
        configWindow.createAndShowConfigWindow();

        //Parameters of simulations from user
        int numStations = configWindow.getNumStations();
        int numberOfCars = configWindow.getNumberOfCars();

        CarWashSimulation simulation = new CarWashSimulation(numStations,numberOfCars);

        // Step 1: Initialize the simulation setup
        simulation.runSimulationSetup();

        // Step 2: Start the Display thread
        Display display = new Display(simulation);
        Thread displayThread = new Thread(display);
        displayThread.start();

        // Step 3: Start the Simulation logic in a separate thread
        Thread simulationThread = new Thread(simulation::runSimulationLogic);
        simulationThread.start();

        // Wait for the simulation to finish
        try {
            simulationThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Clean up resources
        display.stop();
        try {
            displayThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
