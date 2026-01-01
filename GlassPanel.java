package trafficoptimizer.components;

import trafficoptimizer.utils.ColorPalette;

import javax.swing.*;
import java.awt.*;

public class GlassPanel extends JPanel {

    public GlassPanel() {
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // Glass fill
        g2.setColor(ColorPalette.GLASS_MEDIUM);
        g2.fillRoundRect(0, 0, w, h, 26, 26);

        // Border
        g2.setColor(ColorPalette.BORDER_SOFT);
        g2.drawRoundRect(0, 0, w - 1, h - 1, 26, 26);

        g2.dispose();
    }
}
