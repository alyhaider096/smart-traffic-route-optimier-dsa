package trafficoptimizer.ui;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class LoginScreen extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private TrafficLight trafficLight;
    private AnimatedRoad animatedRoad;
    private float[] stars = new float[80];
    private Random rand = new Random();
    private Timer mainTimer;

    public LoginScreen() {

        setTitle("Login – Smart Traffic Route Optimizer");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setUndecorated(true);

        GradientPanel root = new GradientPanel();
        root.setLayout(null);
        add(root);

        // Initialize star positions
        for (int i = 0; i < stars.length; i++) {
            stars[i] = rand.nextFloat();
        }

        // Animated road component
        animatedRoad = new AnimatedRoad();
        root.add(animatedRoad);

        // Traffic light
        trafficLight = new TrafficLight();
        root.add(trafficLight);

        // Main login card
        JPanel card = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Glassmorphism effect
                g2.setColor(new Color(20, 30, 55, 240));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 32, 32);
                
                // Glow border
                g2.setColor(new Color(90, 150, 255, 100));
                g2.setStroke(new BasicStroke(3));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 32, 32);
                
                // Inner glow
                g2.setColor(new Color(90, 150, 255, 30));
                g2.fillRoundRect(8, 8, getWidth() - 16, getHeight() - 16, 28, 28);
            }
        };
        card.setOpaque(false);
        root.add(card);

        // Logo/Icon
        JLabel logoLabel = new JLabel("🚦", SwingConstants.CENTER);
        logoLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 56));
        card.add(logoLabel);

        // Title
        JLabel title = new JLabel("Traffic Route Optimizer", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 32));
        title.setForeground(Color.WHITE);
        card.add(title);

        // Subtitle
        JLabel sub = new JLabel("Smart routing with AI-powered traffic analysis", SwingConstants.CENTER);
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        sub.setForeground(new Color(180, 200, 255));
        card.add(sub);

        // Username field
        usernameField = new JTextField();
        styleField(usernameField, "Username");
        card.add(usernameField);

        // Password field
        passwordField = new JPasswordField();
        styleField(passwordField, "Password");
        card.add(passwordField);

        // Login button
        JButton loginBtn = styleButton("Login to Dashboard");
        loginBtn.addActionListener(e -> handleLogin());
        card.add(loginBtn);

        // Signup link
        JButton signupBtn = new JButton("Don't have an account? Sign Up");
        signupBtn.setFocusPainted(false);
        signupBtn.setContentAreaFilled(false);
        signupBtn.setBorderPainted(false);
        signupBtn.setForeground(new Color(180, 200, 255));
        signupBtn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        signupBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        signupBtn.addActionListener(e -> {
            dispose();
            new SignupScreen().setVisible(true);
        });
        card.add(signupBtn);

        // Hint label
        JLabel hintLabel = new JLabel("Demo: admin / admin123", SwingConstants.CENTER);
        hintLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        hintLabel.setForeground(new Color(120, 140, 180));
        card.add(hintLabel);

        root.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                int w = root.getWidth();
                int h = root.getHeight();
                
                // Card positioning
                int cardW = 480;
                int cardH = 560;
                int cardX = (w - cardW) / 2;
                int cardY = (h - cardH) / 2;
                card.setBounds(cardX, cardY, cardW, cardH);
                
                // Components inside card
                logoLabel.setBounds(50, 30, cardW - 100, 70);
                title.setBounds(50, 110, cardW - 100, 40);
                sub.setBounds(50, 155, cardW - 100, 25);
                usernameField.setBounds(60, 210, cardW - 120, 52);
                passwordField.setBounds(60, 280, cardW - 120, 52);
                loginBtn.setBounds(60, 355, cardW - 120, 54);
                signupBtn.setBounds((cardW - 250) / 2, 430, 250, 35);
                hintLabel.setBounds(60, 475, cardW - 120, 25);
                
                // Traffic light
                trafficLight.setBounds(w - 130, 40, 90, 200);
                
                // Animated road
                animatedRoad.setBounds(0, h - 180, w, 180);
            }
        });

        // Main animation timer
        mainTimer = new Timer(30, e -> {
            for (int i = 0; i < stars.length; i++) {
                stars[i] += 0.002f;
                if (stars[i] > 1f) stars[i] = 0f;
            }
            root.repaint();
        });
        mainTimer.start();
    }

    private void handleLogin() {
        String user = usernameField.getText().trim();
        String pass = new String(passwordField.getPassword());

        if (user.equals("admin") && pass.equals("admin123")) {
            trafficLight.turnGreen();
            
            Timer delayTimer = new Timer(1800, e -> {
                dispose();
                SwingUtilities.invokeLater(() -> {
                    MainFrame frame = new MainFrame();
                    frame.setVisible(true);
                });
            });
            delayTimer.setRepeats(false);
            delayTimer.start();
            
        } else if (user.isEmpty() || pass.isEmpty()) {
            trafficLight.turnRed();
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter username and password",
                    "Login Failed",
                    JOptionPane.ERROR_MESSAGE
            );
        } else {
            trafficLight.turnRed();
            JOptionPane.showMessageDialog(
                    this,
                    "Invalid credentials!\nUse: admin / admin123",
                    "Login Failed",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void styleField(JTextField f, String title) {
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(90, 150, 255, 120), 2),
                BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));
        f.setBackground(new Color(15, 25, 50));
        f.setForeground(Color.WHITE);
        f.setCaretColor(new Color(90, 150, 255));
        f.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        
        // Placeholder effect
        if (f instanceof JPasswordField) {
            ((JPasswordField)f).setEchoChar('•');
        }
    }

    private JButton styleButton(String text) {
        JButton b = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2.setColor(new Color(70, 130, 235));
                } else if (getModel().isRollover()) {
                    g2.setColor(new Color(110, 170, 255));
                } else {
                    g2.setColor(new Color(90, 150, 255));
                }
                
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                
                // Shine effect
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
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setContentAreaFilled(false);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 16));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    // ==================== GRADIENT PANEL ====================
    class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            
            // Gradient background
            GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(8, 12, 25),
                    0, getHeight(), new Color(25, 40, 70)
            );
            g2.setPaint(gradient);
            g2.fillRect(0, 0, getWidth(), getHeight());
            
            // Animated stars
            g2.setColor(new Color(255, 255, 255, 50));
            for (int i = 0; i < stars.length; i++) {
                int x = (int) ((i * 27) % getWidth());
                int y = (int) (stars[i] * getHeight());
                int size = 2 + (i % 3);
                g2.fillOval(x, y, size, size);
            }
            
            // Glow effect at center
            int glowSize = 600;
            RadialGradientPaint glow = new RadialGradientPaint(
                getWidth() / 2, getHeight() / 2, glowSize / 2,
                new float[]{0f, 1f},
                new Color[]{new Color(90, 150, 255, 40), new Color(90, 150, 255, 0)}
            );
            g2.setPaint(glow);
            g2.fillOval(getWidth() / 2 - glowSize / 2, getHeight() / 2 - glowSize / 2, glowSize, glowSize);
        }
    }

    // ==================== TRAFFIC LIGHT ====================
    static class TrafficLight extends JPanel {
        private int currentLight = 2; // 0=red, 1=green, 2=yellow
        private Timer blinkTimer;
        private float glowPhase = 0f;
        private Timer glowTimer;

        public TrafficLight() {
            setOpaque(false);
            
            glowTimer = new Timer(50, e -> {
                glowPhase += 0.1f;
                repaint();
            });
            glowTimer.start();
        }

        public void turnGreen() {
            currentLight = 1;
            repaint();
        }

        public void turnRed() {
            currentLight = 0;
            repaint();
            
            if (blinkTimer != null) blinkTimer.stop();
            blinkTimer = new Timer(400, e -> {
                currentLight = (currentLight == 0) ? -1 : 0;
                repaint();
            });
            blinkTimer.start();
            
            Timer stopBlink = new Timer(2500, e -> {
                if (blinkTimer != null) blinkTimer.stop();
                currentLight = 2;
                repaint();
            });
            stopBlink.setRepeats(false);
            stopBlink.start();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            // Traffic light box with 3D effect
            g2.setColor(new Color(25, 35, 55));
            g2.fillRoundRect(12, 2, w - 24, h - 4, 24, 24);
            
            g2.setColor(new Color(30, 40, 60, 200));
            g2.fillRoundRect(10, 0, w - 20, h, 24, 24);
            
            g2.setColor(new Color(90, 150, 255, 120));
            g2.setStroke(new BasicStroke(3));
            g2.drawRoundRect(10, 0, w - 20, h, 24, 24);

            int cy1 = 35;
            int cy2 = 100;
            int cy3 = 165;
            int cx = w / 2;
            int lightSize = 20;
            int glowSize = 28;

            // Red light
            if (currentLight == 0) {
                float glow = (float) Math.abs(Math.sin(glowPhase));
                g2.setColor(new Color(255, 100, 100, (int)(120 + glow * 80)));
                g2.fillOval(cx - glowSize, cy1 - glowSize, glowSize * 2, glowSize * 2);
                
                g2.setColor(new Color(255, 50, 50));
                g2.fillOval(cx - lightSize, cy1 - lightSize, lightSize * 2, lightSize * 2);
                
                g2.setColor(new Color(255, 150, 150));
                g2.fillOval(cx - lightSize + 5, cy1 - lightSize + 5, lightSize, lightSize);
            } else {
                g2.setColor(new Color(80, 40, 40));
                g2.fillOval(cx - lightSize + 2, cy1 - lightSize + 2, lightSize * 2 - 4, lightSize * 2 - 4);
            }

            // Yellow light
            if (currentLight == 2) {
                float glow = (float) Math.abs(Math.sin(glowPhase * 0.7f));
                g2.setColor(new Color(255, 220, 100, (int)(100 + glow * 70)));
                g2.fillOval(cx - glowSize, cy2 - glowSize, glowSize * 2, glowSize * 2);
                
                g2.setColor(new Color(255, 220, 50));
                g2.fillOval(cx - lightSize, cy2 - lightSize, lightSize * 2, lightSize * 2);
                
                g2.setColor(new Color(255, 240, 150));
                g2.fillOval(cx - lightSize + 5, cy2 - lightSize + 5, lightSize, lightSize);
            } else {
                g2.setColor(new Color(80, 80, 40));
                g2.fillOval(cx - lightSize + 2, cy2 - lightSize + 2, lightSize * 2 - 4, lightSize * 2 - 4);
            }

            // Green light
            if (currentLight == 1) {
                float glow = (float) Math.abs(Math.sin(glowPhase * 0.9f));
                g2.setColor(new Color(100, 255, 100, (int)(120 + glow * 80)));
                g2.fillOval(cx - glowSize, cy3 - glowSize, glowSize * 2, glowSize * 2);
                
                g2.setColor(new Color(50, 255, 50));
                g2.fillOval(cx - lightSize, cy3 - lightSize, lightSize * 2, lightSize * 2);
                
                g2.setColor(new Color(150, 255, 150));
                g2.fillOval(cx - lightSize + 5, cy3 - lightSize + 5, lightSize, lightSize);
            } else {
                g2.setColor(new Color(40, 80, 40));
                g2.fillOval(cx - lightSize + 2, cy3 - lightSize + 2, lightSize * 2 - 4, lightSize * 2 - 4);
            }
        }
    }

    // ==================== ANIMATED ROAD ====================
    static class AnimatedRoad extends JPanel {
        private float carPosition = 0f;
        private float roadLineOffset = 0f;
        private Timer roadTimer;
        private Random rand = new Random();

        public AnimatedRoad() {
            setOpaque(false);
            
            roadTimer = new Timer(40, e -> {
                carPosition += 0.008f;
                if (carPosition > 1f) carPosition = 0f;
                
                roadLineOffset += 2f;
                if (roadLineOffset > 40) roadLineOffset = 0f;
                
                repaint();
            });
            roadTimer.start();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            // Road
            g2.setColor(new Color(30, 40, 60));
            g2.fillRect(0, 0, w, h);

            // Road lines
            g2.setStroke(new BasicStroke(5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 
                                         0, new float[]{25, 15}, roadLineOffset));
            g2.setColor(new Color(255, 255, 255, 100));
            g2.drawLine(0, h / 2, w, h / 2);

            // Side lines
            g2.setStroke(new BasicStroke(3f));
            g2.setColor(new Color(255, 200, 50, 150));
            g2.drawLine(0, 10, w, 10);
            g2.drawLine(0, h - 10, w, h - 10);

            // Moving car
            int carX = (int) (carPosition * (w + 100)) - 100;
            int carY = h / 2 - 15;
            drawCar(g2, carX, carY);

            // Buildings silhouette (background)
            g2.setColor(new Color(15, 20, 35, 150));
            for (int i = 0; i < 8; i++) {
                int bh = 40 + rand.nextInt(60);
                int bw = 60 + rand.nextInt(40);
                g2.fillRect(i * 180, h - bh - 20, bw, bh);
            }
        }

        private void drawCar(Graphics2D g2, int x, int y) {
            // Shadow
            g2.setColor(new Color(0, 0, 0, 60));
            g2.fillOval(x - 5, y + 35, 90, 15);

            // Car body
            g2.setColor(new Color(90, 150, 255));
            g2.fillRoundRect(x, y + 10, 80, 28, 12, 12);

            // Car top
            g2.setColor(new Color(120, 180, 255));
            g2.fillRoundRect(x + 18, y - 5, 40, 20, 10, 10);

            // Headlights
            g2.setColor(new Color(255, 255, 200, 200));
            g2.fillOval(x + 75, y + 15, 6, 6);
            g2.fillOval(x + 75, y + 27, 6, 6);

            // Wheels
            g2.setColor(Color.BLACK);
            g2.fillOval(x + 10, y + 32, 15, 15);
            g2.fillOval(x + 55, y + 32, 15, 15);
            
            g2.setColor(new Color(80, 80, 80));
            g2.fillOval(x + 13, y + 35, 9, 9);
            g2.fillOval(x + 58, y + 35, 9, 9);
        }
    }
}