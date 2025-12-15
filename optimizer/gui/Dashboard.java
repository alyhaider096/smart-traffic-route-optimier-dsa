package smartrafficroute.optimizer.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Dashboard extends JFrame {
    
    private JPanel mainPanel;
    private JPanel headerPanel;
    private JPanel menuPanel;
    private JLabel titleLabel;
    private JLabel welcomeLabel;
    
    // Menu Cards
    private JPanel routeCard;
    private JPanel simulationCard;
    private JPanel predictionCard;
    
    public Dashboard() {
        initComponents();
        setupUI();
    }
    
    private void initComponents() {
        setTitle("Dashboard - Traffic Optimizer");
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        
        mainPanel = new JPanel();
        mainPanel.setLayout(null);
        mainPanel.setBackground(new Color(236, 240, 241));
        
        add(mainPanel);
    }
    
    private void setupUI() {
        // ========== HEADER ==========
        headerPanel = new JPanel();
        headerPanel.setBounds(0, 0, 900, 100);
        headerPanel.setBackground(new Color(41, 128, 185));
        headerPanel.setLayout(null);
        
        // App Icon
        JLabel iconLabel = new JLabel("🚦", SwingConstants.LEFT);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
        iconLabel.setBounds(30, 25, 60, 50);
        headerPanel.add(iconLabel);
        
        // Title
        titleLabel = new JLabel("Smart Traffic Route Optimizer");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(100, 30, 500, 40);
        headerPanel.add(titleLabel);
        
        // Welcome message
        welcomeLabel = new JLabel("Welcome, Admin!");
        welcomeLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        welcomeLabel.setForeground(new Color(236, 240, 241));
        welcomeLabel.setBounds(650, 40, 200, 25);
        headerPanel.add(welcomeLabel);
        
        // Logout button
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setBounds(780, 35, 90, 30);
        logoutBtn.setFont(new Font("Arial", Font.BOLD, 12));
        logoutBtn.setBackground(new Color(231, 76, 60));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setBorderPainted(false);
        logoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutBtn.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to logout?", 
                "Logout", 
                JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                new LoginScreen();
                dispose();
            }
        });
        headerPanel.add(logoutBtn);
        
        mainPanel.add(headerPanel);
        
        // ========== INSTRUCTION LABEL ==========
        JLabel instructionLabel = new JLabel("Select a module to get started:");
        instructionLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        instructionLabel.setForeground(new Color(52, 73, 94));
        instructionLabel.setBounds(50, 130, 400, 30);
        mainPanel.add(instructionLabel);
        
        // ========== MENU CARDS ==========
        
        // Card 1: Route Finder
        routeCard = createCard(
            "🗺️ Route Finder",
            "Find the shortest path between two locations",
            new Color(52, 152, 219),
            70, 180
        );
        routeCard.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                openRouteFinder();
            }
            public void mouseEntered(MouseEvent e) {
                routeCard.setBackground(new Color(41, 128, 185));
            }
            public void mouseExited(MouseEvent e) {
                routeCard.setBackground(new Color(52, 152, 219));
            }
        });
        mainPanel.add(routeCard);
        
        // Card 2: Traffic Simulation
        simulationCard = createCard(
            "🚦 Traffic Simulation",
            "View real-time traffic conditions on roads",
            new Color(46, 204, 113),
            340, 180
        );
        simulationCard.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                openTrafficSimulation();
            }
            public void mouseEntered(MouseEvent e) {
                simulationCard.setBackground(new Color(39, 174, 96));
            }
            public void mouseExited(MouseEvent e) {
                simulationCard.setBackground(new Color(46, 204, 113));
            }
        });
        mainPanel.add(simulationCard);
        
        // Card 3: Traffic Prediction
        predictionCard = createCard(
            "📊 Traffic Prediction",
            "Predict future traffic patterns and congestion",
            new Color(155, 89, 182),
            610, 180
        );
        predictionCard.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                openTrafficPrediction();
            }
            public void mouseEntered(MouseEvent e) {
                predictionCard.setBackground(new Color(142, 68, 173));
            }
            public void mouseExited(MouseEvent e) {
                predictionCard.setBackground(new Color(155, 89, 182));
            }
        });
        mainPanel.add(predictionCard);
        
        // ========== INFO PANEL ==========
        JPanel infoPanel = new JPanel();
        infoPanel.setBounds(70, 450, 760, 120);
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199), 2));
        infoPanel.setLayout(null);
        
        JLabel infoTitle = new JLabel("ℹ️ Quick Info");
        infoTitle.setFont(new Font("Arial", Font.BOLD, 16));
        infoTitle.setForeground(new Color(52, 73, 94));
        infoTitle.setBounds(20, 10, 200, 25);
        infoPanel.add(infoTitle);
        
        JTextArea infoText = new JTextArea(
            "• Route Finder uses Graph BFS algorithm to find shortest paths\n" +
            "• Traffic Simulation displays real-time road congestion using Hash Tables\n" +
            "• Traffic Prediction uses AVL Tree to analyze historical data"
        );
        infoText.setBounds(20, 40, 720, 70);
        infoText.setFont(new Font("Arial", Font.PLAIN, 13));
        infoText.setForeground(new Color(127, 140, 141));
        infoText.setBackground(Color.WHITE);
        infoText.setEditable(false);
        infoText.setLineWrap(true);
        infoText.setWrapStyleWord(true);
        infoPanel.add(infoText);
        
        mainPanel.add(infoPanel);
        
        setVisible(true);
    }
    
    private JPanel createCard(String title, String description, Color bgColor, int x, int y) {
        JPanel card = new JPanel();
        card.setBounds(x, y, 240, 240);
        card.setBackground(bgColor);
        card.setLayout(null);
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        card.setBorder(BorderFactory.createLineBorder(bgColor.darker(), 2));
        
        // Title with emoji
        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(10, 60, 220, 40);
        card.add(titleLabel);
        
        // Description
        JTextArea descLabel = new JTextArea(description);
        descLabel.setBounds(20, 120, 200, 80);
        descLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        descLabel.setForeground(new Color(236, 240, 241));
        descLabel.setBackground(bgColor);
        descLabel.setLineWrap(true);
        descLabel.setWrapStyleWord(true);
        descLabel.setEditable(false);
        descLabel.setFocusable(false);
        card.add(descLabel);
        
        return card;
    }
    
    private void openRouteFinder() {
        new RouteFinder();
        this.setVisible(false);
    }
    
    private void openTrafficSimulation() {
        new TrafficSimulation();
        this.setVisible(false);
    }
    
    private void openTrafficPrediction() {
        new TrafficPrediction();
        this.setVisible(false);
    }
}