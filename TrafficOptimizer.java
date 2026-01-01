package trafficoptimizer;

import javax.swing.SwingUtilities;
import trafficoptimizer.ui.LoginScreen;

public class TrafficOptimizer {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginScreen().setVisible(true);
        });
    }
}
