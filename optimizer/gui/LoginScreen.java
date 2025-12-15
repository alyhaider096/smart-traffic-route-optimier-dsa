package smartrafficroute.optimizer.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LoginScreen extends JFrame {
    
    // UI Components
    private JPanel mainPanel;
    private JPanel headerPanel;
    private JPanel formPanel;
    private JLabel titleLabel;
    private JLabel subtitleLabel;
    private JLabel userIconLabel;
    private JLabel passIconLabel;
    private JLabel userLabel;
    private JLabel passLabel;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton clearButton;
    private JLabel statusLabel;
    private JLabel footerLabel;
    
    // Credentials
    private static final String VALID_USERNAME = "admin";
    private static final String VALID_PASSWORD = "1234";
    
    public LoginScreen() {
        initComponents();
        setupUI();
    }
    
    private void initComponents() {
        setTitle("Smart Traffic Route Optimizer - Login");
        setSize(500, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // Main Panel with gradient background
        mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                // Gradient background
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(41, 128, 185),
                    0, getHeight(), new Color(109, 213, 250)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(null);
        
        add(mainPanel);
    }
    
    private void setupUI() {
        // ========== HEADER SECTION ==========
        headerPanel = new JPanel();
        headerPanel.setBounds(0, 0, 500, 150);
        headerPanel.setOpaque(false);
        headerPanel.setLayout(null);
        
        // App Icon/Logo
        JLabel logoLabel = new JLabel("🚦", SwingConstants.CENTER);
        logoLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 60));
        logoLabel.setBounds(200, 20, 100, 70);
        headerPanel.add(logoLabel);
        
        // Title
        titleLabel = new JLabel("Traffic Optimizer", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(0, 100, 500, 35);
        headerPanel.add(titleLabel);
        
        mainPanel.add(headerPanel);
        
        // ========== FORM PANEL (White Card) ==========
        formPanel = new JPanel();
        formPanel.setBounds(50, 170, 400, 320);
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(20, 30, 20, 30)
        ));
        formPanel.setLayout(null);
        
        // Subtitle
        subtitleLabel = new JLabel("Please login to continue", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(100, 100, 100));
        subtitleLabel.setBounds(0, 10, 340, 25);
        formPanel.add(subtitleLabel);
        
        // Username Section
        userLabel = new JLabel("Username");
        userLabel.setFont(new Font("Arial", Font.BOLD, 14));
        userLabel.setForeground(new Color(52, 73, 94));
        userLabel.setBounds(20, 60, 100, 25);
        formPanel.add(userLabel);
        
        usernameField = new JTextField();
        usernameField.setBounds(20, 90, 300, 35);
        usernameField.setFont(new Font("Arial", Font.PLAIN, 14));
        usernameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 2),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        formPanel.add(usernameField);
        
        // Password Section
        passLabel = new JLabel("Password");
        passLabel.setFont(new Font("Arial", Font.BOLD, 14));
        passLabel.setForeground(new Color(52, 73, 94));
        passLabel.setBounds(20, 140, 100, 25);
        formPanel.add(passLabel);
        
        passwordField = new JPasswordField();
        passwordField.setBounds(20, 170, 300, 35);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 2),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        formPanel.add(passwordField);
        
        // Login Button
        loginButton = new JButton("LOGIN");
        loginButton.setBounds(20, 230, 140, 40);
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.setBackground(new Color(46, 204, 113));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setBorderPainted(false);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginButton.addActionListener(e -> handleLogin());
        
        // Hover effect
        loginButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                loginButton.setBackground(new Color(39, 174, 96));
            }
            public void mouseExited(MouseEvent e) {
                loginButton.setBackground(new Color(46, 204, 113));
            }
        });
        formPanel.add(loginButton);
        
        // Clear Button
        clearButton = new JButton("CLEAR");
        clearButton.setBounds(180, 230, 140, 40);
        clearButton.setFont(new Font("Arial", Font.BOLD, 14));
        clearButton.setBackground(new Color(149, 165, 166));
        clearButton.setForeground(Color.WHITE);
        clearButton.setFocusPainted(false);
        clearButton.setBorderPainted(false);
        clearButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        clearButton.addActionListener(e -> {
            usernameField.setText("");
            passwordField.setText("");
            statusLabel.setText("");
        });
        
        clearButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                clearButton.setBackground(new Color(127, 140, 141));
            }
            public void mouseExited(MouseEvent e) {
                clearButton.setBackground(new Color(149, 165, 166));
            }
        });
        formPanel.add(clearButton);
        
        mainPanel.add(formPanel);
        
        // ========== STATUS MESSAGE ==========
        statusLabel = new JLabel("", SwingConstants.CENTER);
        statusLabel.setBounds(50, 500, 400, 25);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 13));
        mainPanel.add(statusLabel);
        
        // ========== FOOTER ==========
        footerLabel = new JLabel("Developed by Your Team © 2024", SwingConstants.CENTER);
        footerLabel.setBounds(0, 540, 500, 25);
        footerLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        footerLabel.setForeground(Color.WHITE);
        mainPanel.add(footerLabel);
        
        setVisible(true);
    }
    
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setForeground(new Color(231, 76, 60));
            statusLabel.setText("⚠ Please fill all fields!");
            return;
        }
        
        if (username.equals(VALID_USERNAME) && password.equals(VALID_PASSWORD)) {
            statusLabel.setForeground(new Color(46, 204, 113));
            statusLabel.setText("✓ Login Successful! Opening Dashboard...");
            
            loginButton.setEnabled(false);
            
            Timer timer = new Timer(1500, e -> {
                new Dashboard();
                dispose();
            });
            timer.setRepeats(false);
            timer.start();
            
        } else {
            statusLabel.setForeground(new Color(231, 76, 60));
            statusLabel.setText("✗ Invalid username or password!");
            
            // Shake animation
            Point originalLocation = getLocation();
            Timer shakeTimer = new Timer(50, null);
            final int[] shakeCount = {0};
            
            shakeTimer.addActionListener(e -> {
                if (shakeCount[0]++ < 6) {
                    setLocation(originalLocation.x + (shakeCount[0] % 2 == 0 ? 10 : -10), originalLocation.y);
                } else {
                    setLocation(originalLocation);
                    ((Timer)e.getSource()).stop();
                }
            });
            shakeTimer.start();
        }
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> new LoginScreen());
    }
}
