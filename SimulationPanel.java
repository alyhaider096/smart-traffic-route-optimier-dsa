package trafficoptimizer.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.Random;

public class SimulationPanel extends JPanel {
    private float simProgress = 0f;
    private Random rand = new Random();
    private Timer simTimer;
    private boolean isRunning = false;
    private JButton controlBtn;
    private ArrayList<Vehicle> vehicles = new ArrayList<>();
    private float glowPhase = 0f;
    private float[] sparkles = new float[60];
    private int[][] roadNetwork = {
        {100, 200, 300, 200},
        {300, 200, 500, 300},
        {500, 300, 700, 200},
        {700, 200, 900, 300}
    };

    // Statistics
    private int totalVehicles = 0;
    private int averageSpeed = 0;
    private String congestionLevel = "Low";

    class Vehicle {
        float x, y;
        float vx, vy;
        Color color;
        int pathIndex;
        float progress;

        Vehicle(int pathIndex) {
            this.pathIndex = pathIndex;
            this.progress = rand.nextFloat();
            this.color = new Color(
                100 + rand.nextInt(155),
                150 + rand.nextInt(105),
                200 + rand.nextInt(55)
            );
            updatePosition();
        }

        void updatePosition() {
            int[] path = roadNetwork[pathIndex % roadNetwork.length];
            x = path[0] + (path[2] - path[0]) * progress;
            y = path[1] + (path[3] - path[1]) * progress;
        }

        void move() {
            progress += 0.003f + rand.nextFloat() * 0.002f;
            if (progress > 1f) {
                progress = 0f;
                pathIndex = rand.nextInt(roadNetwork.length);
            }
            updatePosition();
        }

        void draw(Graphics2D g2) {
            g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 100));
            g2.fillOval((int)x - 12, (int)y - 12, 24, 24);

            g2.setColor(color);
            g2.fillRoundRect((int)x - 8, (int)y - 6, 16, 12, 6, 6);

            g2.setColor(Color.WHITE);
            g2.fillOval((int)x + 6, (int)y - 2, 3, 3);
        }
    }

    public SimulationPanel() {
        setLayout(null);
        setBackground(new Color(10, 15, 30));

        for (int i = 0; i < sparkles.length; i++) {
            sparkles[i] = rand.nextFloat();
        }

        // Title with icon
        JLabel iconLabel = new JLabel("🎮", SwingConstants.CENTER);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 52));
        add(iconLabel);

        JLabel title = new JLabel("Traffic Flow Simulation");
        title.setFont(new Font("Segoe UI", Font.BOLD, 40));
        title.setForeground(Color.WHITE);
        add(title);

        JLabel subtitle = new JLabel("Real-time vehicle movement and congestion analysis powered by graph algorithms");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(new Color(180, 200, 255));
        add(subtitle);

        // Control button
        controlBtn = new JButton("▶️ Start Simulation") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                Color btnColor = isRunning ? new Color(255, 100, 100) : new Color(90, 150, 255);
                if (getModel().isPressed()) {
                    btnColor = btnColor.darker();
                } else if (getModel().isRollover()) {
                    btnColor = btnColor.brighter();
                }
                
                GradientPaint gradient = new GradientPaint(
                    0, 0, btnColor,
                    0, getHeight(), btnColor.darker()
                );
                g2.setPaint(gradient);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                
                g2.setColor(new Color(255, 255, 255, 40));
                g2.fillRoundRect(0, 0, getWidth(), getHeight() / 2, 16, 16);
                
                g2.setColor(Color.WHITE);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), x, y);
            }
        };
        controlBtn.setFont(new Font("Segoe UI", Font.BOLD, 17));
        controlBtn.setFocusPainted(false);
        controlBtn.setBorderPainted(false);
        controlBtn.setContentAreaFilled(false);
        controlBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        controlBtn.addActionListener(e -> toggleSimulation());
        add(controlBtn);

        // Stats panel
        createStatsPanel();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                positionComponents();
            }
        });

        simTimer = new Timer(30, e -> {
            if (isRunning) {
                simProgress += 0.005f;
                if (simProgress > 1f) simProgress = 0f;

                for (Vehicle v : vehicles) {
                    v.move();
                }

                totalVehicles = vehicles.size();
                averageSpeed = 35 + rand.nextInt(20);
                
                if (totalVehicles > 15) {
                    congestionLevel = "High";
                } else if (totalVehicles > 8) {
                    congestionLevel = "Medium";
                } else {
                    congestionLevel = "Low";
                }
            }

            glowPhase += 0.05f;
            
            for (int i = 0; i < sparkles.length; i++) {
                sparkles[i] += 0.003f;
                if (sparkles[i] > 1f) sparkles[i] = 0f;
            }
            
            repaint();
        });
        simTimer.start();
    }

    private void createStatsPanel() {
        // Vehicle count panel
        JPanel vehiclePanel = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2.setColor(new Color(20, 35, 65, 220));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                
                g2.setColor(new Color(90, 150, 255, 100));
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 20, 20);
            }
        };
        vehiclePanel.setOpaque(false);
        
        JLabel vIcon = new JLabel("🚗", SwingConstants.CENTER);
        vIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));
        vIcon.setBounds(0, 10, 180, 40);
        vehiclePanel.add(vIcon);
        
        JLabel vTitle = new JLabel("Active Vehicles", SwingConstants.CENTER);
        vTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        vTitle.setForeground(new Color(180, 200, 255));
        vTitle.setBounds(0, 55, 180, 20);
        vehiclePanel.add(vTitle);
        
        JLabel vCount = new JLabel("0", SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                setText(String.valueOf(totalVehicles));
                super.paintComponent(g);
            }
        };
        vCount.setFont(new Font("Segoe UI", Font.BOLD, 32));
        vCount.setForeground(Color.WHITE);
        vCount.setBounds(0, 75, 180, 40);
        vehiclePanel.add(vCount);
        
        add(vehiclePanel);

        // Speed panel
        JPanel speedPanel = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2.setColor(new Color(20, 35, 65, 220));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                
                g2.setColor(new Color(90, 150, 255, 100));
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 20, 20);
            }
        };
        speedPanel.setOpaque(false);
        
        JLabel sIcon = new JLabel("⚡", SwingConstants.CENTER);
        sIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));
        sIcon.setBounds(0, 10, 180, 40);
        speedPanel.add(sIcon);
        
        JLabel sTitle = new JLabel("Avg Speed", SwingConstants.CENTER);
        sTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        sTitle.setForeground(new Color(180, 200, 255));
        sTitle.setBounds(0, 55, 180, 20);
        speedPanel.add(sTitle);
        
        JLabel sValue = new JLabel("0 km/h", SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                setText(averageSpeed + " km/h");
                super.paintComponent(g);
            }
        };
        sValue.setFont(new Font("Segoe UI", Font.BOLD, 28));
        sValue.setForeground(Color.WHITE);
        sValue.setBounds(0, 75, 180, 40);
        speedPanel.add(sValue);
        
        add(speedPanel);

        // Congestion panel
        JPanel congestionPanel = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                Color bgColor;
                if (congestionLevel.equals("High")) {
                    bgColor = new Color(60, 30, 30, 220);
                } else if (congestionLevel.equals("Medium")) {
                    bgColor = new Color(60, 50, 30, 220);
                } else {
                    bgColor = new Color(30, 60, 40, 220);
                }
                
                g2.setColor(bgColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                
                g2.setColor(new Color(90, 150, 255, 100));
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 20, 20);
            }
        };
        congestionPanel.setOpaque(false);
        
        JLabel cIcon = new JLabel("📊", SwingConstants.CENTER);
        cIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));
        cIcon.setBounds(0, 10, 180, 40);
        congestionPanel.add(cIcon);
        
        JLabel cTitle = new JLabel("Congestion", SwingConstants.CENTER);
        cTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        cTitle.setForeground(new Color(180, 200, 255));
        cTitle.setBounds(0, 55, 180, 20);
        congestionPanel.add(cTitle);
        
        JLabel cValue = new JLabel("Low", SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                setText(congestionLevel);
                super.paintComponent(g);
            }
        };
        cValue.setFont(new Font("Segoe UI", Font.BOLD, 28));
        cValue.setForeground(Color.WHITE);
        cValue.setBounds(0, 75, 180, 40);
        congestionPanel.add(cValue);
        
        add(congestionPanel);
    }

    private void toggleSimulation() {
        isRunning = !isRunning;
        
        if (isRunning) {
            controlBtn.setText("⏸️ Pause Simulation");
            simProgress = 0f;
            vehicles.clear();
            for (int i = 0; i < 12; i++) {
                vehicles.add(new Vehicle(rand.nextInt(roadNetwork.length)));
            }
        } else {
            controlBtn.setText("▶️ Start Simulation");
        }
    }

    private void positionComponents() {
        int w = getWidth();
        
        getComponent(0).setBounds(40, 30, 60, 60);  // Icon
        getComponent(1).setBounds(110, 40, 600, 45);  // Title
        getComponent(2).setBounds(110, 90, 800, 25);  // Subtitle
        
        controlBtn.setBounds((w - 280) / 2, 140, 280, 60);
        
        // Stats panels
        getComponent(4).setBounds(50, 220, 180, 130);
        getComponent(5).setBounds((w - 180) / 2, 220, 180, 130);
        getComponent(6).setBounds(w - 230, 220, 180, 130);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // Sparkles
        g2.setColor(new Color(90, 150, 255, 60));
        for (int i = 0; i < sparkles.length; i++) {
            int x = (int) ((i * 30) % w);
            int y = (int) (sparkles[i] * h);
            g2.fillOval(x, y, 2, 2);
        }

        // Simulation area
        int simX = 50;
        int simY = 370;
        int simW = w - 100;
        int simH = h - 420;

        g2.setColor(new Color(20, 35, 65, 230));
        g2.fillRoundRect(simX, simY, simW, simH, 32, 32);
        
        g2.setColor(new Color(90, 150, 255, 100));
        g2.setStroke(new BasicStroke(3));
        g2.drawRoundRect(simX, simY, simW, simH, 32, 32);

        if (!isRunning && vehicles.isEmpty()) {
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 18));
            g2.setColor(new Color(150, 170, 200));
            String msg = "Press 'Start Simulation' to begin traffic flow analysis";
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(msg, simX + (simW - fm.stringWidth(msg)) / 2, simY + simH / 2);
        } else {
            // Draw road network
            g2.setColor(new Color(40, 50, 70));
            for (int[] road : roadNetwork) {
                g2.setStroke(new BasicStroke(20f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawLine(simX + road[0], simY + road[1], simX + road[2], simY + road[3]);
            }

            // Road lines
            g2.setColor(new Color(255, 255, 255, 80));
            g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND,
                                         0, new float[]{10, 10}, simProgress * 20));
            for (int[] road : roadNetwork) {
                g2.drawLine(simX + road[0], simY + road[1], simX + road[2], simY + road[3]);
            }

            // Draw vehicles
            for (Vehicle v : vehicles) {
                v.draw(g2);
            }

            // Add/remove vehicles dynamically
            if (isRunning && rand.nextFloat() < 0.02f && vehicles.size() < 20) {
                vehicles.add(new Vehicle(rand.nextInt(roadNetwork.length)));
            }
            if (isRunning && rand.nextFloat() < 0.01f && vehicles.size() > 5) {
                vehicles.remove(0);
            }
        }

        // Glow effect
        int glowSize = 400 + (int)(Math.sin(glowPhase) * 50);
        RadialGradientPaint glow = new RadialGradientPaint(
            w / 2, simY + simH / 2, glowSize / 2,
            new float[]{0f, 1f},
            new Color[]{new Color(90, 150, 255, 30), new Color(90, 150, 255, 0)}
        );
        g2.setPaint(glow);
        g2.fillOval(w / 2 - glowSize / 2, simY + simH / 2 - glowSize / 2, glowSize, glowSize);
    }
}