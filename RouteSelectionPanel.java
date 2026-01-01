package trafficoptimizer.ui;

import trafficoptimizer.utils.NetworkClient;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.Random;

public class RouteSelectionPanel extends JPanel {
    private final MainFrame frame;
    private final NetworkClient client;
    private JComboBox<String> sourceBox;
    private JComboBox<String> destinationBox;
    private JButton computeButton;
    
    private float glowPhase = 0f;
    private float[] particles = new float[120];
    private float roadAnimation = 0f;
    private Random rand = new Random();
    private javax.swing.Timer animTimer;
    private boolean isComputing = false;
    private float computeProgress = 0f;

    // ⚠️ MUST MATCH C++ LocationData order
    private final String[] locations = {
            "Air University (E-9)",
            "Air University (H-11)",
            "F-8",
            "Blue Area",
            "Centaurus Mall",
            "F-9 Park",
            "G-8",
            "Bahria Town"
    };

    public RouteSelectionPanel(MainFrame frame, NetworkClient client) {
        this.frame = frame;
        this.client = client;
        setLayout(null);
        setBackground(new Color(10, 15, 30));

        for (int i = 0; i < particles.length; i++) {
            particles[i] = rand.nextFloat();
        }

        // Main title with icon
        JLabel titleIcon = new JLabel("🗺️", SwingConstants.CENTER);
        titleIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        add(titleIcon);

        JLabel title = new JLabel("Plan Your Route", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 42));
        title.setForeground(Color.WHITE);
        add(title);

        JLabel subtitle = new JLabel("Select source and destination for intelligent route optimization", SwingConstants.CENTER);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        subtitle.setForeground(new Color(180, 200, 255));
        add(subtitle);

        // Source section with decorative panel
        JPanel sourcePanel = createGlassPanel();
        add(sourcePanel);

        JLabel srcIcon = new JLabel("📍", SwingConstants.CENTER);
        srcIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        srcIcon.setBounds(20, 15, 50, 50);
        sourcePanel.add(srcIcon);

        JLabel srcLabel = new JLabel("Source Location");
        srcLabel.setForeground(new Color(180, 200, 255));
        srcLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        srcLabel.setBounds(80, 20, 200, 25);
        sourcePanel.add(srcLabel);

        sourceBox = createStyledComboBox();
        sourceBox.setBounds(80, 50, 480, 50);
        sourcePanel.add(sourceBox);

        // Destination section with decorative panel
        JPanel destPanel = createGlassPanel();
        add(destPanel);

        JLabel destIcon = new JLabel("🎯", SwingConstants.CENTER);
        destIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        destIcon.setBounds(20, 15, 50, 50);
        destPanel.add(destIcon);

        JLabel destLabel = new JLabel("Destination Location");
        destLabel.setForeground(new Color(180, 200, 255));
        destLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        destLabel.setBounds(80, 20, 200, 25);
        destPanel.add(destLabel);

        destinationBox = createStyledComboBox();
        destinationBox.setBounds(80, 50, 480, 50);
        destPanel.add(destinationBox);

        // Compute button with animation
        computeButton = new JButton("🚀 Compute Optimal Route") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                Color btnColor;
                if (isComputing) {
                    btnColor = new Color(255, 150, 50);
                } else if (getModel().isPressed()) {
                    btnColor = new Color(70, 130, 235);
                } else if (getModel().isRollover()) {
                    btnColor = new Color(110, 170, 255);
                } else {
                    btnColor = new Color(90, 150, 255);
                }
                
                // Gradient button
                GradientPaint gradient = new GradientPaint(
                    0, 0, btnColor,
                    0, getHeight(), btnColor.darker()
                );
                g2.setPaint(gradient);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                
                // Glow effect
                if (!isComputing) {
                    int glowSize = getWidth() + (int)(Math.sin(glowPhase) * 15);
                    g2.setColor(new Color(90, 150, 255, 40));
                    g2.fillRoundRect((getWidth() - glowSize) / 2, -8, glowSize, getHeight() + 16, 20, 20);
                }
                
                // Computing progress bar
                if (isComputing) {
                    g2.setColor(new Color(255, 255, 255, 60));
                    g2.fillRoundRect(10, getHeight() - 8, getWidth() - 20, 4, 2, 2);
                    
                    g2.setColor(Color.WHITE);
                    int progWidth = (int)((getWidth() - 20) * computeProgress);
                    g2.fillRoundRect(10, getHeight() - 8, progWidth, 4, 2, 2);
                }
                
                // Text with shadow
                g2.setColor(new Color(0, 0, 0, 80));
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), x + 2, y + 2);
                
                g2.setColor(Color.WHITE);
                g2.drawString(getText(), x, y);
            }
        };
        computeButton.setFont(new Font("Segoe UI", Font.BOLD, 18));
        computeButton.setForeground(Color.WHITE);
        computeButton.setFocusPainted(false);
        computeButton.setBorderPainted(false);
        computeButton.setContentAreaFilled(false);
        computeButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        computeButton.addActionListener(e -> computeRoute());
        add(computeButton);

        // Info panel at bottom
        JPanel infoPanel = createInfoPanel();
        add(infoPanel);

        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                positionComponents();
            }
        });

        animTimer = new javax.swing.Timer(30, e -> {
            glowPhase += 0.06f;
            roadAnimation += 0.015f;
            if (roadAnimation > 1f) roadAnimation = 0f;
            
            for (int i = 0; i < particles.length; i++) {
                particles[i] += 0.003f;
                if (particles[i] > 1f) particles[i] = 0f;
            }
            
            if (isComputing) {
                computeProgress += 0.02f;
                if (computeProgress > 1f) computeProgress = 0f;
            }
            
            repaint();
        });
        animTimer.start();
    }

    private JPanel createGlassPanel() {
        JPanel panel = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2.setColor(new Color(20, 35, 65, 200));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);
                
                g2.setColor(new Color(90, 150, 255, 80));
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 24, 24);
                
                g2.setColor(new Color(90, 150, 255, 20));
                g2.fillRoundRect(5, 5, getWidth() - 10, getHeight() / 2, 20, 20);
            }
        };
        panel.setOpaque(false);
        return panel;
    }

    private JPanel createInfoPanel() {
        JPanel panel = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2.setColor(new Color(20, 35, 65, 180));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                
                g2.setColor(new Color(90, 150, 255, 60));
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 20, 20);
            }
        };
        panel.setOpaque(false);
        
        String[] infos = {
            "⚡ Powered by Dijkstra's Algorithm",
            "🎯 Real-time Traffic Analysis",
            "🔄 Connected to C++ Backend"
        };
        
        int yPos = 15;
        for (String info : infos) {
            JLabel label = new JLabel(info);
            label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            label.setForeground(new Color(180, 200, 255));
            label.setBounds(30, yPos, 500, 25);
            panel.add(label);
            yPos += 30;
        }
        
        return panel;
    }

    private JComboBox<String> createStyledComboBox() {
        JComboBox<String> box = new JComboBox<>(locations);
        box.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        box.setBackground(new Color(15, 25, 50));
        box.setForeground(Color.WHITE);
        box.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(90, 150, 255, 120), 2),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        return box;
    }

    private void positionComponents() {
        int w = getWidth();
        int h = getHeight();
        
        int contentW = 640;
        int startX = (w - contentW) / 2;
        
        getComponent(0).setBounds(startX + 250, 50, 60, 60);  // Icon
        getComponent(1).setBounds(startX, 120, contentW, 50);  // Title
        getComponent(2).setBounds(startX, 175, contentW, 25);  // Subtitle
        
        ((JPanel)getComponent(3)).setBounds(startX, 230, contentW, 120);  // Source panel
        ((JPanel)getComponent(4)).setBounds(startX, 370, contentW, 120);  // Dest panel
        
        computeButton.setBounds((w - 420) / 2, 520, 420, 65);
        
        ((JPanel)getComponent(6)).setBounds(startX, h - 160, contentW, 120);  // Info panel
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // Animated particles
        g2.setColor(new Color(90, 150, 255, 50));
        for (int i = 0; i < particles.length; i++) {
            int x = (int) ((i * 22) % w);
            int y = (int) (particles[i] * h);
            int size = 2 + (i % 4);
            g2.fillOval(x, y, size, size);
        }

        // Animated road lines (decorative)
        g2.setColor(new Color(90, 150, 255, 30));
        g2.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                                     0, new float[]{15, 15}, roadAnimation * 30));
        
        // Curved connecting lines
        int cx = w / 2;
        Path2D path = new Path2D.Float();
        path.moveTo(cx - 200, 290);
        path.curveTo(cx - 100, 350, cx + 100, 350, cx + 200, 290);
        g2.draw(path);
        
        Path2D path2 = new Path2D.Float();
        path2.moveTo(cx - 200, 430);
        path2.curveTo(cx - 100, 370, cx + 100, 370, cx + 200, 430);
        g2.draw(path2);

        // Central glow
        int glowSize = 400 + (int)(Math.sin(glowPhase * 0.7f) * 50);
        RadialGradientPaint glow = new RadialGradientPaint(
            cx, 360, glowSize / 2,
            new float[]{0f, 1f},
            new Color[]{new Color(90, 150, 255, 30), new Color(90, 150, 255, 0)}
        );
        g2.setPaint(glow);
        g2.fillOval(cx - glowSize / 2, 360 - glowSize / 2, glowSize, glowSize);
    }

    private void computeRoute() {
        String src = sourceBox.getSelectedItem().toString();
        String dst = destinationBox.getSelectedItem().toString();

        if (src.equals(dst)) {
            JOptionPane.showMessageDialog(this, 
                "⚠️ Source and destination cannot be the same!\nPlease select different locations.", 
                "Invalid Selection", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        isComputing = true;
        computeProgress = 0f;
        computeButton.setText("⏳ Computing...");
        computeButton.setEnabled(false);

        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            String response;
            
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    if (client == null) {
                        throw new Exception("Backend not connected");
                    }
                    
                    // Send request to C++ backend
                    response = client.send("ROUTE:" + src + ":" + dst);
                    
                    // Simulate processing time for smooth animation
                    Thread.sleep(800);
                    
                } catch (Exception e) {
                    response = null;
                }
                return null;
            }
            
            @Override
            protected void done() {
                isComputing = false;
                computeButton.setText("🚀 Compute Optimal Route");
                computeButton.setEnabled(true);
                
                try {
                    if (response == null || response.contains("ERROR")) {
                        JOptionPane.showMessageDialog(RouteSelectionPanel.this, 
                            "❌ Backend not responding!\n\nPlease ensure:\n• C++ backend is running\n• Port 8080 is available\n• Network connection is active",
                            "Connection Error", 
                            JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    // Parse response (format: DISTANCE:X\nPATH:Y\nEND\n)
                    int time = -1;
                    String path = "No route";
                    
                    String[] lines = response.split("\n");
                    for (String line : lines) {
                        line = line.trim();
                        if (line.startsWith("DISTANCE:")) {
                            String distStr = line.substring(9).trim();
                            time = Integer.parseInt(distStr);
                        } else if (line.startsWith("PATH:")) {
                            path = line.substring(5).trim();
                        }
                    }

                    if (time == -1) {
                        JOptionPane.showMessageDialog(RouteSelectionPanel.this,
                            "❌ No route found!\n\nNo valid path exists between:\n• " + src + "\n• " + dst,
                            "No Route",
                            JOptionPane.WARNING_MESSAGE);
                        frame.showRouteResults(-1, "No route");
                        return;
                    }

                    // Show success animation before switching
                    JOptionPane.showMessageDialog(RouteSelectionPanel.this,
                        "✅ Route calculated successfully!\n\n" +
                        "📍 From: " + src + "\n" +
                        "🎯 To: " + dst + "\n" +
                        "⏱️ Time: " + time + " minutes\n\n" +
                        "Click OK to view detailed analysis.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    frame.showRouteResults(time, path);
                    
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(RouteSelectionPanel.this, 
                        "⚠️ Error processing route data:\n" + ex.getMessage(), 
                        "Computation Error", 
                        JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        };
        
        worker.execute();
    }
}