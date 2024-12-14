package pl.edu.pwr.mrodak.jp.lab05;

import pl.edu.pwr.mrodak.jp.lab05.logic.CarWashSimulation;
import pl.edu.pwr.mrodak.jp.lab05.models.Car;
import pl.edu.pwr.mrodak.jp.lab05.models.Hose;
import pl.edu.pwr.mrodak.jp.lab05.models.Station;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.BlockingQueue;

public class Display extends JFrame implements Runnable {
    private final BlockingQueue<Car> queue1;
    private final BlockingQueue<Car> queue2;
    private final Station[] stations;
    private volatile boolean running = true; // Flag to stop the display thread from running

    private JTextArea queue1Area;
    private JTextArea queue2Area;
    private JPanel stationPanel;

    public Display(CarWashSimulation simulation) {
        this.queue1 = simulation.getEntranceQueue1();
        this.queue2 = simulation.getEntranceQueue2();
        this.stations = simulation.getStations();

        // Setup GUI
        setTitle("Car Wash Simulation");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Create and add components
        queue1Area = new JTextArea();
        queue2Area = new JTextArea();
        stationPanel = new JPanel();
        stationPanel.setLayout(new GridLayout(0, 1));

        add(createQueuePanel(), BorderLayout.WEST);
        add(new JScrollPane(stationPanel), BorderLayout.CENTER);

        setLocationRelativeTo(null); // This centers the window

        setVisible(true);
    }

    private JPanel createQueuePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2, 1));

        panel.add(createQueueSubPanel("Queue 1", queue1Area));
        panel.add(createQueueSubPanel("Queue 2", queue2Area));

        return panel;
    }

    private JPanel createQueueSubPanel(String title, JTextArea textArea) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JLabel label = new JLabel(title);
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);

        panel.add(label, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void updateQueueArea(JTextArea area, BlockingQueue<Car> queue) {
        StringBuilder content = new StringBuilder();
        if (queue.isEmpty()) {
            content.append("(empty)");
        } else {
            for (Car car : queue) {
                content.append("Car ").append(car.getId()).append("\n");
            }
        }
        area.setText(content.toString());
    }

    private void updateStationPanel() {
        stationPanel.removeAll();

        for (Station station : stations) {
            JPanel stationSubPanel = new JPanel();
            stationSubPanel.setLayout(new BorderLayout());

            JLabel stationLabel = new JLabel("Station " + station.getId());
            stationSubPanel.add(stationLabel, BorderLayout.NORTH);

            JTextArea stationArea = new JTextArea();
            stationArea.setEditable(false);
            stationArea.setText(getStationInfo(station));
            stationSubPanel.add(stationArea, BorderLayout.CENTER);

            stationPanel.add(stationSubPanel);
        }

        stationPanel.revalidate();
        stationPanel.repaint();
    }

    private String getStationInfo(Station station) {
        StringBuilder info = new StringBuilder();

        info.append("Current Car: ");
        Car currentCar = station.getCurrentCar();
        if (currentCar != null) {
            info.append("Car ").append(currentCar.getId()).append("\n");
        } else {
            info.append("(none)\n");
        }

        info.append("Water Hoses:\n");
        appendHoseInfo(info, station.getWaterHoses());

        info.append("\nSoap Hoses:\n");
        appendHoseInfo(info, station.getSoapHoses());

        return info.toString();
    }

    private void appendHoseInfo(StringBuilder info, Hose[] hoses) {
        for (Hose hose : hoses) {
            String status;
            Car currentCar = hose.getCurrentCar();
            if (currentCar != null) {
                status = "(in use by Car " + currentCar.getId() + ")";
            } else {
                status = "(free)";
            }
            info.append(hose.getType()).append(" ").append(status).append("\n");
        }
    }

    public void stop() {
        running = false;
    }

    @Override
    public void run() {
        while (running) {
            try {
                // Update GUI components
                updateQueueArea(queue1Area, queue1);
                updateQueueArea(queue2Area, queue2);
                updateStationPanel();
                // Wait 100  milliseconds before updating
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
