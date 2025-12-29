package trafficoptimizer.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;

public class LoadingOverlay extends JDialog {
    private float angle = 0;
    private float pulsePhase = 0;
    private float[] particles = new float[30];
    private final Timer timer;
    private String loadingText = "Computing Route...";

    public LoadingOverlay(JFrame parent) {
        super(parent, true);
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0));
        setSize(parent.getSize());
        setLocationRelativeTo(parent);
        
        // Initialize particles
        for (int i = 0; i < particles.length; i++) {
            particles[i] = (float) Math.random();
        }

        timer = new Timer(20, e -> {
            angle += 8;
            if (angle >= 360) angle = 0;
            
            pulsePhase += 0.08f;
            
            // Update particles
            for (int i = 0; i < particles.length; i++) {
                particles[i] += 0.01f;
                if (particles[i] > 1f) particles[i] = 0f;
            }
            
            repaint();
        });
    }

    public void setLoadingText(String text) {
        this.loadingText = text;
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // Semi-transparent background with blur effect
        g2.setColor(new Color(8, 12, 25, 220));
        g2.fillRect(0, 0, w, h);

        // Animated particles
        g2.setColor(new Color(90, 150, 255, 100));
        for (int i = 0; i < particles.length; i++) {
            int x = (int) ((i * 50) % w);
            int y = (int) (particles[i] * h);
            int size = 2 + (i % 3);
            g2.fillOval(x, y, size, size);
        }

        int centerX = w / 2;
        int centerY = h / 2;

        // Outer glow ring (pulsing)
        float pulse = (float) Math.abs(Math.sin(pulsePhase));
        int glowSize = 180 + (int)(pulse * 40);
        RadialGradientPaint outerGlow = new RadialGradientPaint(
            centerX, centerY, glowSize / 2,
            new float[]{0f, 1f},
            new Color[]{new Color(90, 150, 255, 80), new Color(90, 150, 255, 0)}
        );
        g2.setPaint(outerGlow);
        g2.fillOval(centerX - glowSize / 2, centerY - glowSize / 2, glowSize, glowSize);

        // Glass panel background
        g2.setColor(new Color(20, 35, 65, 230));
        g2.fillRoundRect(centerX - 160, centerY - 100, 320, 200, 32, 32);

        // Border
        g2.setColor(new Color(90, 150, 255, 120));
        g2.setStroke(new BasicStroke(3));
        g2.drawRoundRect(centerX - 160, centerY - 100, 320, 200, 32, 32);

        // Main spinner circle
        int spinnerSize = 80;
        int spinnerX = centerX - spinnerSize / 2;
        int spinnerY = centerY - spinnerSize / 2 - 20;

        // Background circle
        g2.setColor(new Color(40, 60, 100, 150));
        g2.setStroke(new BasicStroke(8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawOval(spinnerX, spinnerY, spinnerSize, spinnerSize);

        // Animated arc
        GradientPaint gradient = new GradientPaint(
            spinnerX, spinnerY, new Color(90, 150, 255),
            spinnerX + spinnerSize, spinnerY + spinnerSize, new Color(120, 180, 255)
        );
        g2.setPaint(gradient);
        g2.setStroke(new BasicStroke(8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawArc(spinnerX, spinnerY, spinnerSize, spinnerSize, (int)angle, 280);

        // Center glow
        RadialGradientPaint centerGlow = new RadialGradientPaint(
            centerX, centerY - 20, 30,
            new float[]{0f, 1f},
            new Color[]{new Color(90, 150, 255, 180), new Color(90, 150, 255, 0)}
        );
        g2.setPaint(centerGlow);
        g2.fillOval(centerX - 30, centerY - 50, 60, 60);

        // Loading text with shadow
        g2.setFont(new Font("Segoe UI", Font.BOLD, 18));
        g2.setColor(new Color(0, 0, 0, 100));
        FontMetrics fm = g2.getFontMetrics();
        int textWidth = fm.stringWidth(loadingText);
        g2.drawString(loadingText, centerX - textWidth / 2 + 2, centerY + 62);

        g2.setColor(Color.WHITE);
        g2.drawString(loadingText, centerX - textWidth / 2, centerY + 60);

        // Animated dots
        String dots = ".".repeat(((int)(angle / 90) % 4));
        g2.setFont(new Font("Segoe UI", Font.BOLD, 18));
        g2.setColor(new Color(180, 200, 255));
        g2.drawString(dots, centerX + textWidth / 2 + 10, centerY + 60);

        // Bottom info text
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        g2.setColor(new Color(180, 200, 255, 180));
        String info = "Applying Dijkstra's Algorithm";
        int infoWidth = g2.getFontMetrics().stringWidth(info);
        g2.drawString(info, centerX - infoWidth / 2, centerY + 85);
    }

    public void showOverlay() {
        timer.start();
        setVisible(true);
    }

    public void hideOverlay() {
        timer.stop();
        setVisible(false);
        dispose();
    }
}