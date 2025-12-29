package trafficoptimizer.ui;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class SignupScreen extends JFrame {

    private JTextField usernameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private float[] stars = new float[80];
    private Random rand = new Random();
    private Timer mainTimer;

    public SignupScreen() {
        setTitle("Sign Up – Smart Traffic Route Optimizer");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setUndecorated(true);

        // Initialize star positions
        for (int i = 0; i < stars.length; i++) {
            stars[i] = rand.nextFloat();
        }

        GradientPanel root = new GradientPanel();
        root.setLayout(null);
        add(root);

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
        logoLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        card.add(logoLabel);

        JLabel title = new JLabel("Create Account", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 30));
        title.setForeground(Color.WHITE);
        card.add(title);

        JLabel sub = new JLabel("Join the Traffic Optimizer Network", SwingConstants.CENTER);
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        sub.setForeground(new Color(180, 200, 255));
        card.add(sub);

        usernameField = new JTextField();
        styleField(usernameField, "Username");
        card.add(usernameField);

        emailField = new JTextField();
        styleField(emailField, "Email Address");
        card.add(emailField);

        passwordField = new JPasswordField();
        styleField(passwordField, "Password");
        card.add(passwordField);

        confirmPasswordField = new JPasswordField();
        styleField(confirmPasswordField, "Confirm Password");
        card.add(confirmPasswordField);

        JButton signupBtn = styleButton("Create Account");
        signupBtn.addActionListener(e -> handleSignup());
        card.add(signupBtn);

        JButton backBtn = new JButton("Already have an account? Login");
        backBtn.setFocusPainted(false);
        backBtn.setContentAreaFilled(false);
        backBtn.setBorderPainted(false);
        backBtn.setForeground(new Color(180, 200, 255));
        backBtn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        backBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backBtn.addActionListener(e -> {
            dispose();
            new LoginScreen().setVisible(true);
        });
        card.add(backBtn);

        root.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                int w = root.getWidth();
                int h = root.getHeight();
                
                int cardW = 480;
                int cardH = 620;
                int cardX = (w - cardW) / 2;
                int cardY = (h - cardH) / 2;
                card.setBounds(cardX, cardY, cardW, cardH);
                
                logoLabel.setBounds(50, 25, cardW - 100, 60);
                title.setBounds(50, 90, cardW - 100, 40);
                sub.setBounds(50, 135, cardW - 100, 25);
                usernameField.setBounds(60, 185, cardW - 120, 52);
                emailField.setBounds(60, 255, cardW - 120, 52);
                passwordField.setBounds(60, 325, cardW - 120, 52);
                confirmPasswordField.setBounds(60, 395, cardW - 120, 52);
                signupBtn.setBounds(60, 470, cardW - 120, 54);
                backBtn.setBounds((cardW - 280) / 2, 545, 280, 35);
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

    private void handleSignup() {
        String user = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String pass = new String(passwordField.getPassword());
        String confirmPass = new String(confirmPasswordField.getPassword());

        if (user.isEmpty() || email.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please fill all fields",
                    "Signup Failed",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!email.contains("@")) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a valid email address",
                    "Invalid Email",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!pass.equals(confirmPass)) {
            JOptionPane.showMessageDialog(this,
                    "Passwords do not match!",
                    "Signup Failed",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (pass.length() < 6) {
            JOptionPane.showMessageDialog(this,
                    "Password must be at least 6 characters",
                    "Weak Password",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(this,
                "Account created successfully!\n\nYou can now login with:\nUsername: admin\nPassword: admin123",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);

        dispose();
        new LoginScreen().setVisible(true);
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
}