package smartrafficroute.optimizer.gui;

import javax.swing.*;

public class TrafficPrediction extends JFrame {
    public TrafficPrediction() {
        setTitle("Traffic Prediction");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel label = new JLabel("Traffic Prediction Coming Soon...", SwingConstants.CENTER);
        add(label);

        setVisible(true);
    }
}

