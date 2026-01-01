package trafficoptimizer.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Random;

public class DashBoardOverviewPanel extends JPanel {

    private final MainFrame frame;

    private int typingIndex = 0;
    private final String fullTitle = "Smart Traffic Route Optimizer";

    private boolean nightMode = true;

    private final int[] dotsX = new int[150];
    private final int[] dotsY = new int[150];
    private final Random rand = new Random();

    private float glowPhase = 0f;

    // ===== CAR ANIMATION =====
    private float carT = 0f;
    private float carSpeed = 0.007f;
    private float carBounce = 0f;
    private float headlightBlink = 0f;

    // ===== CREDIT CYCLE =====
    private float creditAlpha = 0f;
    private int creditPhase = 0;
    private int creditTimer = 0;

    private final String[] credits = {
            "Curated by Ali & Hadia",
            "Submitted to Sir Qaiser",
            "BSCS – 3rd Semester"
    };

    private JButton enterBtn;
    private JToggleButton themeToggle;
    private JLabel themeLabel;

    public DashBoardOverviewPanel(MainFrame frame) {
        this.frame = frame;
        setLayout(null);

        for (int i = 0; i < dotsX.length; i++) {
            dotsX[i] = rand.nextInt(1800);
            dotsY[i] = rand.nextInt(1000);
        }

        enterBtn = new JButton("🚀 Enter Live Traffic Dashboard") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Glowing effect
                if (getModel().isRollover() || getModel().isPressed()) {
                    g2.setColor(new Color(90, 150, 255, 80));
                    g2.fillRoundRect(-5, -5, getWidth() + 10, getHeight() + 10, 20, 20);
                }
                
                // Button gradient
                GradientPaint gradient;
                if (getModel().isPressed()) {
                    gradient = new GradientPaint(0, 0, new Color(70, 130, 235), 
                                                 0, getHeight(), new Color(50, 110, 215));
                } else if (getModel().isRollover()) {
                    gradient = new GradientPaint(0, 0, new Color(100, 160, 255), 
                                                 0, getHeight(), new Color(80, 140, 235));
                } else {
                    gradient = new GradientPaint(0, 0, new Color(90, 150, 255), 
                                                 0, getHeight(), new Color(70, 130, 235));
                }
                
                g2.setPaint(gradient);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                
                // Border shine
                g2.setColor(new Color(255, 255, 255, 100));
                g2.setStroke(new BasicStroke(2f));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 15, 15);
                
                // Text
                g2.setColor(Color.WHITE);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), x, y);
            }
        };
        enterBtn.setFont(new Font("Segoe UI", Font.BOLD, 18));
        enterBtn.setForeground(Color.WHITE);
        enterBtn.setFocusPainted(false);
        enterBtn.setBorderPainted(false);
        enterBtn.setContentAreaFilled(false);
        enterBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        enterBtn.addActionListener(e -> frame.showPage(MainFrame.LIVE_MAP));
        add(enterBtn);

        themeToggle = new JToggleButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Button background
                g2.setColor(new Color(30, 45, 80, 220));
                g2.fillOval(0, 0, getWidth(), getHeight());
                
                // Hover effect
                if (getModel().isRollover()) {
                    g2.setColor(new Color(90, 150, 255, 60));
                    g2.fillOval(0, 0, getWidth(), getHeight());
                }

                if (nightMode) drawMoonIcon(g2, getWidth(), getHeight());
                else drawSunIcon(g2, getWidth(), getHeight());
            }
            @Override 
            protected void paintBorder(Graphics g) {}
        };
        themeToggle.setContentAreaFilled(false);
        themeToggle.setFocusPainted(false);
        themeToggle.setOpaque(false);
        themeToggle.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        themeLabel = new JLabel("Night Mode", SwingConstants.CENTER);
        themeLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));

        themeToggle.addActionListener(e -> {
            nightMode = !nightMode;
            themeLabel.setText(nightMode ? "Night Mode" : "Day Mode");
            repaint();
        });

        add(themeToggle);
        add(themeLabel);

        addComponentListener(new ComponentAdapter() {
            @Override 
            public void componentResized(ComponentEvent e) {
                positionControls();
            }
        });

        Timer animTimer = new Timer(30, e -> {
            glowPhase += 0.03f;
            carT += carSpeed;
            if (carT > 1f) carT = 0f;

            carBounce = (float) Math.sin(carT * Math.PI * 2) * 3;
            headlightBlink += 0.08f;

            if (typingIndex < fullTitle.length()) typingIndex++;

            creditTimer++;
            if (creditTimer > 120) {
                creditTimer = 0;
                creditPhase = (creditPhase + 1) % credits.length;
                creditAlpha = 0f;
            }
            if (creditAlpha < 1f) creditAlpha += 0.015f;

            for (int i = 0; i < dotsY.length; i++) {
                dotsY[i] += 1;
                if (dotsY[i] > getHeight()) {
                    dotsY[i] = 0;
                    dotsX[i] = rand.nextInt(Math.max(1, getWidth()));
                }
            }

            positionControls();
            repaint();
        });
        animTimer.start();
    }

    private void positionControls() {
        int w = getWidth();
        int h = getHeight();

        int btnW = 400;
        int btnH = 60;
        enterBtn.setBounds((w - btnW) / 2, (int) (h * 0.58), btnW, btnH);

        int x = w - 80;
        themeToggle.setBounds(x, 25, 50, 50);
        themeLabel.setBounds(x - 12, 78, 74, 18);
        themeLabel.setForeground(nightMode ? new Color(180, 200, 255) : new Color(60, 80, 120));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // Background
        g2.setColor(nightMode ? new Color(8, 12, 25) : new Color(236, 241, 250));
        g2.fillRect(0, 0, w, h);

        // Animated stars/particles
        g2.setColor(new Color(255, 255, 255, nightMode ? 50 : 30));
        for (int i = 0; i < dotsX.length; i++) {
            int size = (i % 3) + 1;
            g2.fillOval(dotsX[i], dotsY[i], size, size);
        }

        // Central glow
        int glow = (int) (600 + Math.sin(glowPhase) * 80);
        g2.setColor(new Color(90, 150, 255, nightMode ? 50 : 35));
        g2.fillOval(w / 2 - glow / 2, (int)(h * 0.28) - glow / 2, glow, glow);

        // Title with typing effect
        g2.setFont(new Font("Segoe UI", Font.BOLD, 52));
        g2.setColor(nightMode ? Color.WHITE : new Color(20, 25, 35));
        String t = fullTitle.substring(0, typingIndex);
        int titleW = g2.getFontMetrics().stringWidth(t);
        g2.drawString(t, w / 2 - titleW / 2, (int)(h * 0.18));
        
        // Blinking cursor
        if (typingIndex < fullTitle.length() && System.currentTimeMillis() % 1000 < 500) {
            g2.fillRect(w / 2 - titleW / 2 + titleW + 5, (int)(h * 0.18) - 40, 3, 45);
        }

        drawFeatureList(g2, w / 2, (int)(h * 0.30));
        drawCredit(g2, w / 2, (int)(h * 0.72));
        drawRoadAndCar(g2, w, h);
    }

    private void drawFeatureList(Graphics2D g2, int cx, int y) {
        String[] features = {
                "✓ Graph (Adjacency List)",
                "✓ Dijkstra's Algorithm",
                "✓ Priority Queue (Min-Heap)",
                "✓ Hash Table (HashMap)"
        };

        g2.setFont(new Font("Segoe UI", Font.BOLD, 22));
        
        for (int i = 0; i < features.length; i++) {
            int rowY = y + i * 52;
            float alpha = Math.min(1f, (typingIndex / (float)fullTitle.length()) * 1.8f);
            
            if (nightMode) {
                g2.setColor(new Color(180, 200, 255, (int)(alpha * 255)));
            } else {
                g2.setColor(new Color(60, 80, 120, (int)(alpha * 255)));
            }
            
            String feature = features[i];
            int featW = g2.getFontMetrics().stringWidth(feature);
            g2.drawString(feature, cx - featW / 2, rowY);
        }
    }

    private void drawCredit(Graphics2D g2, int cx, int y) {
        g2.setFont(new Font("Segoe UI", Font.BOLD, 20));
        int alpha = (int)(creditAlpha * (nightMode ? 230 : 190));
        
        if (nightMode) {
            g2.setColor(new Color(255, 200, 100, alpha));
        } else {
            g2.setColor(new Color(90, 100, 120, alpha));
        }
        
        String txt = credits[creditPhase];
        int txtW = g2.getFontMetrics().stringWidth(txt);
        g2.drawString(txt, cx - txtW / 2, y);
    }

    private void drawRoadAndCar(Graphics2D g2, int w, int h) {
        int roadH = 160;
        int roadY = h - roadH;

        // Road
        g2.setColor(nightMode ? new Color(25, 35, 55) : new Color(100, 110, 130));
        g2.fillRect(0, roadY, w, roadH);

        // Road lines
        g2.setStroke(new BasicStroke(6f, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_ROUND, 0, new float[]{25, 25}, 0));
        g2.setColor(new Color(255, 255, 255, nightMode ? 80 : 100));
        g2.drawLine(40, roadY + roadH / 2, w - 40, roadY + roadH / 2);

        // Car position
        int carX = (int) (40 + (w - 180) * carT);
        int carY = roadY + 48 + (int) carBounce;

        drawCar(g2, carX, carY);
    }

    private void drawCar(Graphics2D g2, int x, int y) {
        // Shadow
        g2.setColor(new Color(0, 0, 0, 60));
        g2.fillOval(x - 8, y + 54, 135, 28);

        // Car body
        g2.setColor(new Color(90, 150, 255));
        g2.fillRoundRect(x, y + 20, 120, 40, 20, 20);

        // Car roof
        g2.setColor(new Color(120, 180, 255));
        g2.fillRoundRect(x + 28, y, 60, 30, 18, 18);

        // Windows
        g2.setColor(new Color(50, 70, 100, 150));
        g2.fillRoundRect(x + 32, y + 4, 24, 20, 8, 8);
        g2.fillRoundRect(x + 60, y + 4, 24, 20, 8, 8);

        // Headlights (animated in night mode)
        if (nightMode) {
            float blinkIntensity = (float) Math.abs(Math.sin(headlightBlink));
            int alpha = (int) (160 + blinkIntensity * 95);
            
            // Headlight glow
            g2.setColor(new Color(255, 255, 150, (int)(alpha * 0.5f)));
            g2.fillOval(x + 110, y + 24, 18, 18);
            g2.fillOval(x + 110, y + 42, 18, 18);
            
            // Bright headlights
            g2.setColor(new Color(255, 255, 220, alpha));
            g2.fillOval(x + 114, y + 26, 10, 10);
            g2.fillOval(x + 114, y + 44, 10, 10);
        } else {
            g2.setColor(new Color(255, 240, 200));
            g2.fillOval(x + 114, y + 26, 8, 8);
            g2.fillOval(x + 114, y + 44, 8, 8);
        }

        // Wheels
        g2.setColor(Color.BLACK);
        g2.fillOval(x + 20, y + 50, 22, 22);
        g2.fillOval(x + 85, y + 50, 22, 22);
        
        g2.setColor(new Color(70, 70, 70));
        g2.fillOval(x + 23, y + 53, 16, 16);
        g2.fillOval(x + 88, y + 53, 16, 16);
        
        // Wheel details
        g2.setColor(new Color(100, 100, 100));
        g2.fillOval(x + 27, y + 57, 8, 8);
        g2.fillOval(x + 92, y + 57, 8, 8);
    }

    private void drawSunIcon(Graphics2D g2, int w, int h) {
        int cx = w / 2, cy = h / 2;
        
        // Sun rays
        g2.setColor(new Color(255, 200, 80));
        for (int i = 0; i < 8; i++) {
            double angle = i * Math.PI / 4;
            int x1 = cx + (int)(14 * Math.cos(angle));
            int y1 = cy + (int)(14 * Math.sin(angle));
            int x2 = cx + (int)(20 * Math.cos(angle));
            int y2 = cy + (int)(20 * Math.sin(angle));
            g2.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawLine(x1, y1, x2, y2);
        }
        
        // Sun circle
        g2.fillOval(cx - 9, cy - 9, 18, 18);
    }

    private void drawMoonIcon(Graphics2D g2, int w, int h) {
        int cx = w / 2, cy = h / 2;
        
        // Moon
        g2.setColor(new Color(220, 230, 255));
        g2.fillOval(cx - 9, cy - 9, 18, 18);
        
        // Moon shadow (crescent)
        g2.setColor(new Color(30, 45, 80, 220));
        g2.fillOval(cx - 5, cy - 9, 16, 16);
    }
}