package trafficoptimizer.components;

import trafficoptimizer.utils.ColorPalette;

import javax.swing.*;
import java.awt.*;

public class GlassCard extends JPanel {

    public GlassCard() {
        setOpaque(false);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // Glass gradient
        GradientPaint gp = new GradientPaint(
                0, 0, new Color(50, 60, 85, 160),
                0, h, new Color(30, 40, 65, 200)
        );
        g2.setPaint(gp);
        g2.fillRoundRect(0, 0, w, h, 26, 26);

        // Border
        g2.setColor(new Color(255, 255, 255, 32));
        g2.drawRoundRect(0, 0, w - 1, h - 1, 26, 26);

        g2.dispose();
    }
}
