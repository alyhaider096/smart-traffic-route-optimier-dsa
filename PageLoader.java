package trafficoptimizer.components;

import trafficoptimizer.utils.ColorPalette;

import javax.swing.*;
import java.awt.*;

public class PageLoader extends JPanel {

    private int angle = 0;
    private final Timer timer;

    public PageLoader() {
        setOpaque(false);

        timer = new Timer(16, e -> {
            angle = (angle + 6) % 360;
            repaint();
        });
    }

    public void showLoader() {
        timer.start();
        setVisible(true);
    }

    public void hideLoader() {
        timer.stop();
        setVisible(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        // semi-transparent backdrop
        g2.setColor(new Color(0, 0, 0, 140));
        g2.fillRect(0, 0, getWidth(), getHeight());

        int size = 70;
        int x = getWidth() / 2 - size / 2;
        int y = getHeight() / 2 - size / 2;

        g2.setStroke(new BasicStroke(6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.setColor(ColorPalette.ACCENT_BLUE);
        g2.drawArc(x, y, size, size, angle, 270);

        g2.dispose();
    }
}
