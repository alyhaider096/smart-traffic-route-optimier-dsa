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

        enterBtn = new JButton("Live Traffic View Dashboard") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2.setColor(new Color(70, 130, 235));
                } else if (getModel().isRollover()) {
                    g2.setColor(new Color(100, 160, 255));
                } else {
                    g2.setColor(new Color(90, 150, 255));
                }
                
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                
                g2.setColor(Color.WHITE);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), x, y);
            }
        };
        enterBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        enterBtn.setForeground(Color.WHITE);
        enterBtn.setFocusPainted(false);
        enterBtn.setBorderPainted(false);
        enterBtn.setContentAreaFilled(false);
        enterBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        enterBtn.addActionListener(e -> frame.showPage(MainFrame.DASHBOARD));
        add(enterBtn);

        themeToggle = new JToggleButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(new Color(30, 45, 80, 220));
                g2.fillOval(0, 0, getWidth(), getHeight());

                if (nightMode) drawMoonIcon(g2, getWidth(), getHeight());
                else drawSunIcon(g2, getWidth(), getHeight());
            }
            @Override protected void paintBorder(Graphics g) {}
        };
        themeToggle.setContentAreaFilled(false);
        themeToggle.setFocusPainted(false);
        themeToggle.setOpaque(false);
        themeToggle.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        themeLabel = new JLabel("Night Mode", SwingConstants.CENTER);
        themeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));

        themeToggle.addActionListener(e -> {
            nightMode = !nightMode;
            themeLabel.setText(nightMode ? "Night Mode" : "Day Mode");
            repaint();
        });

        add(themeToggle);
        add(themeLabel);

        addComponentListener(new ComponentAdapter() {
            @Override public void componentResized(ComponentEvent e) {
                positionControls();
            }
        });

        new Timer(30, e -> {
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
        }).start();
    }

    private void positionControls() {
        int w = getWidth();
        int h = getHeight();

        int btnW = 360;
        int btnH = 52;
        enterBtn.setBounds((w - btnW) / 2, (int) (h * 0.58), btnW, btnH);

        int x = w - 75;
        themeToggle.setBounds(x, 20, 48, 48);
        themeLabel.setBounds(x - 10, 70, 68, 16);
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

        g2.setColor(nightMode ? new Color(8, 12, 25) : new Color(236, 241, 250));
        g2.fillRect(0, 0, w, h);

        g2.setColor(new Color(255, 255, 255, nightMode ? 40 : 25));
        for (int i = 0; i < dotsX.length; i++) {
            g2.fillOval(dotsX[i], dotsY[i], 2, 2);
        }

        int glow = (int) (560 + Math.sin(glowPhase) * 70);
        g2.setColor(new Color(90, 150, 255, nightMode ? 45 : 30));
        g2.fillOval(w / 2 - glow / 2, (int)(h * 0.28) - glow / 2, glow, glow);

        g2.setFont(new Font("Segoe UI", Font.BOLD, 48));
        g2.setColor(nightMode ? Color.WHITE : new Color(20, 25, 35));
        String t = fullTitle.substring(0, typingIndex);
        g2.drawString(t, w / 2 - g2.getFontMetrics().stringWidth(t) / 2, (int)(h * 0.18));

        drawFeatureList(g2, w / 2, (int)(h * 0.30));
        drawCredit(g2, w / 2, (int)(h * 0.70));
        drawRoadAndCar(g2, w, h);
    }

    private void drawFeatureList(Graphics2D g2, int cx, int y) {
        String[] features = {
                "Graph (Adjacency List)",
                "Dijkstra's Algorithm",
                "Priority Queue (Min-Heap)",
                "AVL Tree",
                "Hashing (HashMap)"
        };

        g2.setFont(new Font("Segoe UI", Font.BOLD, 20));
        g2.setColor(nightMode ? new Color(180, 200, 255) : new Color(60, 80, 120));
        
        for (int i = 0; i < features.length; i++) {
            int rowY = y + i * 50;
            float alpha = Math.min(1f, (typingIndex / (float)fullTitle.length()) * 1.5f);
            g2.setColor(new Color(nightMode ? 180 : 60, nightMode ? 200 : 80, nightMode ? 255 : 120, 
                                  (int)(alpha * 255)));
            g2.drawString("• " + features[i], cx - 180, rowY);
        }
    }

    private void drawCredit(Graphics2D g2, int cx, int y) {
        g2.setFont(new Font("Segoe UI", Font.BOLD, 19));
        int alpha = (int)(creditAlpha * (nightMode ? 220 : 180));
        g2.setColor(new Color(nightMode ? 255 : 40, nightMode ? 255 : 50, nightMode ? 255 : 70, alpha));
        String txt = credits[creditPhase];
        g2.drawString(txt, cx - g2.getFontMetrics().stringWidth(txt) / 2, y);
    }

    private void drawRoadAndCar(Graphics2D g2, int w, int h) {
        int roadH = 150;
        int roadY = h - roadH;

        g2.setColor(nightMode ? new Color(25, 35, 55) : new Color(100, 110, 130));
        g2.fillRect(0, roadY, w, roadH);

        g2.setStroke(new BasicStroke(5f, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_ROUND, 0, new float[]{20, 20}, 0));
        g2.setColor(new Color(255, 255, 255, nightMode ? 70 : 90));
        g2.drawLine(40, roadY + roadH / 2, w - 40, roadY + roadH / 2);

        int carX = (int) (40 + (w - 180) * carT);
        int carY = roadY + 45 + (int) carBounce;

        drawCar(g2, carX, carY);
    }

    private void drawCar(Graphics2D g2, int x, int y) {
        // Shadow
        g2.setColor(new Color(0, 0, 0, 50));
        g2.fillOval(x - 8, y + 52, 130, 26);

        // Car body
        g2.setColor(new Color(90, 150, 255));
        g2.fillRoundRect(x, y + 18, 115, 38, 18, 18);

        // Car roof
        g2.setColor(new Color(120, 180, 255));
        g2.fillRoundRect(x + 26, y, 56, 28, 16, 16);

        // Headlights (blinking in night mode)
        if (nightMode) {
            float blinkIntensity = (float) Math.abs(Math.sin(headlightBlink));
            int alpha = (int) (150 + blinkIntensity * 105);
            
            g2.setColor(new Color(255, 255, 200, alpha));
            g2.fillOval(x + 108, y + 24, 8, 8);
            g2.fillOval(x + 108, y + 40, 8, 8);
            
            // Headlight glow
            g2.setColor(new Color(255, 255, 150, (int)(alpha * 0.4f)));
            g2.fillOval(x + 106, y + 22, 12, 12);
            g2.fillOval(x + 106, y + 38, 12, 12);
        }

        // Wheels
        g2.setColor(Color.BLACK);
        g2.fillOval(x + 18, y + 46, 20, 20);
        g2.fillOval(x + 80, y + 46, 20, 20);
        
        g2.setColor(new Color(60, 60, 60));
        g2.fillOval(x + 21, y + 49, 14, 14);
        g2.fillOval(x + 83, y + 49, 14, 14);
    }

    private void drawSunIcon(Graphics2D g2, int w, int h) {
        int cx = w / 2, cy = h / 2;
        g2.setColor(new Color(255, 200, 80));
        g2.fillOval(cx - 8, cy - 8, 16, 16);
        
        for (int i = 0; i < 8; i++) {
            double angle = i * Math.PI / 4;
            int x1 = cx + (int)(12 * Math.cos(angle));
            int y1 = cy + (int)(12 * Math.sin(angle));
            int x2 = cx + (int)(18 * Math.cos(angle));
            int y2 = cy + (int)(18 * Math.sin(angle));
            g2.setStroke(new BasicStroke(2f));
            g2.drawLine(x1, y1, x2, y2);
        }
    }

    private void drawMoonIcon(Graphics2D g2, int w, int h) {
        int cx = w / 2, cy = h / 2;
        g2.setColor(new Color(220, 230, 255));
        g2.fillOval(cx - 8, cy - 8, 16, 16);
        g2.setColor(new Color(30, 45, 80, 220));
        g2.fillOval(cx - 4, cy - 8, 14, 14);
    }
}