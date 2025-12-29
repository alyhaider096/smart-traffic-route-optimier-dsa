package trafficoptimizer.models;

import trafficoptimizer.utils.ColorPalette;
import trafficoptimizer.utils.ResourceLoader;

import javax.swing.*;
import java.awt.*;

public class MapCanvas extends JPanel {

    private float t = 0f;
    private final Timer timer;

    public MapCanvas() {
        setOpaque(false);

        timer = new Timer(40, e -> {
            t += 0.01f;
            if (t >= 1) t = 0;
            repaint();
        });

        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // Glass background
        g2.setColor(ColorPalette.GLASS_STRONG);
        g2.fillRoundRect(0, 0, w, h, 26, 26);
        g2.setColor(ColorPalette.BORDER_SOFT);
        g2.drawRoundRect(0, 0, w - 1, h - 1, 26, 26);

        // Glow background
        Image glow = ResourceLoader.loadImage("city-glow.png");
        if (glow != null)
            g2.drawImage(glow, 0, 0, w, h / 2, null);

        // Grid
        g2.setColor(new Color(255, 255, 255, 20));
        int step = 40;
        for (int x = 20; x < w; x += step) g2.drawLine(x, 20, x, h - 20);
        for (int y = 20; y < h; y += step) g2.drawLine(20, y, w - 20, y);

        // Animated diagonal route
        int x1 = 40;
        int y1 = h - 60;
        int x2 = w - 40;
        int y2 = 60;

        g2.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.setPaint(new GradientPaint(
                x1, y1, ColorPalette.ACCENT_TEAL,
                x2, y2, ColorPalette.ACCENT_PURPLE
        ));
        g2.drawLine(x1, y1, x2, y2);

        // Moving node
        int nx = (int) (x1 + (x2 - x1) * t);
        int ny = (int) (y1 - (y1 - y2) * t);

        g2.setColor(ColorPalette.ACCENT_BLUE);
        g2.fillOval(nx - 14, ny - 14, 28, 28);
        g2.setColor(Color.WHITE);
        g2.fillOval(nx - 6, ny - 6, 12, 12);

        g2.dispose();
    }
}
