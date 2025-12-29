package trafficoptimizer.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;

public class DashBoardPanel extends JPanel {

    private float t = 0f;
    private float zoom = 0f;

    private float[] particlesX = new float[100];
    private float[] particlesY = new float[100];

    private Point[] intersections = {
            new Point(320, 320),
            new Point(560, 450),
            new Point(820, 280),
            new Point(1040, 380)
    };

    private String hoveredNode = null;
    private Random rand = new Random();
    private Timer timer;
    private float pulsePhase = 0f;

    public DashBoardPanel() {
        setBackground(new Color(8, 12, 25));

        for (int i = 0; i < particlesX.length; i++) {
            particlesX[i] = rand.nextInt(1400);
            particlesY[i] = rand.nextInt(900);
        }

        timer = new Timer(25, e -> {
            t += 0.008f;
            if (t > 1f) t = 0f;

            zoom += 0.003f;
            if (zoom > 1f) zoom = 0f;

            pulsePhase += 0.05f;

            for (int i = 0; i < particlesY.length; i++) {
                particlesY[i] += 0.6f;
                if (particlesY[i] > getHeight()) {
                    particlesY[i] = 0;
                    particlesX[i] = rand.nextInt(Math.max(1, getWidth()));
                }
            }
            repaint();
        });
        timer.start();

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                String prevHovered = hoveredNode;
                hoveredNode = null;
                
                for (int i = 0; i < intersections.length; i++) {
                    if (e.getPoint().distance(intersections[i]) < 20) {
                        hoveredNode = "Intersection " + (char)('A' + i) + " | Status: "
                                + (i % 2 == 0 ? "High Traffic" : "Medium Traffic");
                        break;
                    }
                }
                
                if ((hoveredNode == null) != (prevHovered == null)) {
                    repaint();
                }
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                hoveredNode = null;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 32));
        g2.drawString("Live Traffic Dashboard", 40, 50);

        g2.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        g2.setColor(new Color(180, 200, 255));
        g2.drawString("Real-time city network • Interactive heatmap • Dynamic traffic flow", 40, 78);

        int gx = 40;
        int gy = 110;
        int gw = w - 80;
        int gh = h - 170;

        g2.setColor(new Color(18, 28, 50, 230));
        g2.fillRoundRect(gx, gy, gw, gh, 32, 32);
        g2.setColor(new Color(90, 150, 255, 60));
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(gx, gy, gw, gh, 32, 32);

        g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
        g2.setColor(new Color(170, 190, 230));
        g2.drawString("City Network Visualization", gx + 25, gy + 35);

        g2.setColor(new Color(255, 255, 255, 18));
        int gridStep = 52 + (int) (Math.sin(zoom * Math.PI) * 8);
        for (int x = gx + 40; x < gx + gw - 40; x += gridStep)
            g2.drawLine(x, gy + 45, x, gy + gh - 25);
        for (int y = gy + 70; y < gy + gh - 25; y += gridStep)
            g2.drawLine(gx + 25, y, gx + gw - 25, y);

        g2.setColor(new Color(255, 255, 255, 60));
        for (int i = 0; i < particlesX.length; i++)
            g2.fillOval((int) particlesX[i], (int) particlesY[i], 2, 2);

        int x1 = gx + 100;
        int y1 = gy + gh - 100;
        int x2 = gx + gw - 100;
        int y2 = gy + 110;

        g2.setStroke(new BasicStroke(5f));
        g2.setColor(new Color(90, 150, 255, 180));
        g2.drawLine(x1, y1, x2, y2);

        GradientPaint heat = new GradientPaint(x1, y1, new Color(120, 255, 160),
                x2, y2, new Color(255, 90, 90));
        g2.setStroke(new BasicStroke(12f));
        g2.setPaint(heat);
        g2.drawLine(x1, y1, x2, y2);

        drawCar(g2, x1, y1, x2, y2, t, new Color(120, 200, 255));
        drawCar(g2, x1, y1, x2, y2, (t + 0.30f) % 1f, new Color(255, 180, 120));
        drawCar(g2, x1, y1, x2, y2, (t + 0.60f) % 1f, new Color(180, 255, 180));

        for (int i = 0; i < intersections.length; i++) {
            int basePulse = 8;
            int pulse = basePulse + (int) (Math.abs(Math.sin(pulsePhase + i * 0.5f)) * 8);
            Color c = (i % 2 == 0) ? new Color(255, 80, 80) : new Color(255, 210, 80);

            g2.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 180));
            g2.fillOval(intersections[i].x - pulse, intersections[i].y - pulse, pulse * 2, pulse * 2);

            g2.setColor(new Color(255, 255, 255, 140));
            g2.setStroke(new BasicStroke(2.5f));
            g2.drawOval(intersections[i].x - pulse, intersections[i].y - pulse, pulse * 2, pulse * 2);

            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
            String label = String.valueOf((char)('A' + i));
            g2.drawString(label, intersections[i].x - 5, intersections[i].y + 5);
        }

        if (hoveredNode != null) {
            int tooltipW = 420;
            int tooltipH = 50;
            int tooltipX = 70;
            int tooltipY = h - 90;
            
            g2.setColor(new Color(20, 30, 55, 240));
            g2.fillRoundRect(tooltipX, tooltipY, tooltipW, tooltipH, 14, 14);
            g2.setColor(new Color(90, 150, 255, 120));
            g2.setStroke(new BasicStroke(2));
            g2.drawRoundRect(tooltipX, tooltipY, tooltipW, tooltipH, 14, 14);

            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 15));
            g2.drawString(hoveredNode, tooltipX + 20, tooltipY + 32);
        }
    }

    private void drawCar(Graphics2D g2, int x1, int y1, int x2, int y2, float progress, Color color) {
        int x = (int) (x1 + (x2 - x1) * progress);
        int y = (int) (y1 + (y2 - y1) * progress);

        g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 60));
        g2.fillOval(x - 32, y - 22, 64, 64);

        g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 220));
        g2.fillRoundRect(x - 20, y - 12, 40, 22, 12, 12);

        g2.setColor(Color.BLACK);
        g2.fillOval(x - 14, y + 8, 10, 10);
        g2.fillOval(x + 4, y + 8, 10, 10);
    }
}