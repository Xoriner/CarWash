package pl.edu.pwr.mrodak.jp.lab05;

import javax.swing.*;
import java.awt.*;

public class ConfigWindow extends JFrame {
    private int numStations;
    private int numberOfCars;

    public int getNumberOfCars() {
        return numberOfCars;
    }

    public int getNumStations() {
        return numStations;
    }

    public void createAndShowConfigWindow() {
        // Create the frame
        JFrame frame = new JFrame("Car Wash Simulation Configuration");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLayout(new GridLayout(3, 1));

        // Create components
        JPanel carPanel = createSelectionPanel("Number of Cars:", 1, 10);
        JPanel stationPanel = createSelectionPanel("Number of Stations:", 1, 10);

        JButton startButton = new JButton("Start Simulation");
        startButton.addActionListener(e -> {
            numberOfCars = getSelectedValue(carPanel);
            numStations = getSelectedValue(stationPanel);

            // Close the configuration window
            frame.dispose();
        });

        // Add components to the frame
        frame.add(carPanel);
        frame.add(stationPanel);
        frame.add(startButton);

        // Make the frame modal-like (blocking the main thread)
        frame.setModalExclusionType(Dialog.ModalExclusionType.APPLICATION_EXCLUDE); // Only for Java 9 and above

        // Display the frame
        frame.setVisible(true);

        // Wait for the user to finish the configuration
        while (frame.isVisible()) {
            try {
                // Sleep for a short period to keep the event loop active
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private static JPanel createSelectionPanel(String label, int min, int max) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel(label);
        JList<Integer> list = new JList<>();
        DefaultListModel<Integer> model = new DefaultListModel<>();
        for (int i = min; i <= max; i++) {
            model.addElement(i);
        }
        list.setModel(model);
        list.setSelectedIndex(0); // Default selection
        JScrollPane scrollPane = new JScrollPane(list);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Store the list component in the panel for later retrieval
        panel.putClientProperty("list", list);

        return panel;
    }

    private static int getSelectedValue(JPanel panel) {
        @SuppressWarnings("unchecked")
        JList<Integer> list = (JList<Integer>) panel.getClientProperty("list");
        return list.getSelectedValue();
    }
}
