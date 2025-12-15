package smartrafficroute.optimizer.gui;

import smartrafficroute.optimizer.integration.CppConnector;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.Timer;


public class RouteFinder extends JFrame {
    
    private JPanel mainPanel;
    private JPanel headerPanel;
    private JPanel inputPanel;
    private JPanel outputPanel;
    private JTextField startField;
    private JTextField endField;
    private JTextArea outputArea;
    private JButton findRouteBtn;
    private JButton clearBtn;
    private JButton backBtn;
    private JLabel statusLabel;
    
    public RouteFinder() {
        initComponents();
        setupUI();
    }
    
    private void initComponents() {
        setTitle("Route Finder - Traffic Optimizer");
        setSize(900, 700);
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
        headerPanel.setBounds(0, 0, 900, 80);
        headerPanel.setBackground(new Color(52, 152, 219));
        headerPanel.setLayout(null);
        
        JLabel iconLabel = new JLabel("🗺️", SwingConstants.LEFT);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
        iconLabel.setBounds(30, 20, 60, 40);
        headerPanel.add(iconLabel);
        
        JLabel titleLabel = new JLabel("Route Finder");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 26));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(100, 25, 300, 35);
        headerPanel.add(titleLabel);
        
        backBtn = new JButton("← Back");
        backBtn.setBounds(780, 25, 90, 30);
        backBtn.setFont(new Font("Arial", Font.BOLD, 13));
        backBtn.setBackground(new Color(189, 195, 199));
        backBtn.setForeground(new Color(44, 62, 80));
        backBtn.setFocusPainted(false);
        backBtn.setBorderPainted(false);
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backBtn.addActionListener(e -> backToDashboard());
        headerPanel.add(backBtn);
        
        mainPanel.add(headerPanel);
        
        // ========== INPUT PANEL ==========
        inputPanel = new JPanel();
        inputPanel.setBounds(50, 110, 800, 180);
        inputPanel.setBackground(Color.WHITE);
        inputPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(52, 152, 219), 2),
            " Enter Locations ",
            0,
            0,
            new Font("Arial", Font.BOLD, 14),
            new Color(52, 152, 219)
        ));
        inputPanel.setLayout(null);
        
        // Start Location
        JLabel startLabel = new JLabel("📍 Start Location:");
        startLabel.setFont(new Font("Arial", Font.BOLD, 15));
        startLabel.setForeground(new Color(52, 73, 94));
        startLabel.setBounds(40, 40, 150, 25);
        inputPanel.add(startLabel);
        
        startField = new JTextField("Islamabad");
        startField.setBounds(200, 35, 250, 35);
        startField.setFont(new Font("Arial", Font.PLAIN, 14));
        startField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 2),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        inputPanel.add(startField);
        
        // End Location
        JLabel endLabel = new JLabel("🎯 End Location:");
        endLabel.setFont(new Font("Arial", Font.BOLD, 15));
        endLabel.setForeground(new Color(52, 73, 94));
        endLabel.setBounds(40, 95, 150, 25);
        inputPanel.add(endLabel);
        
        endField = new JTextField("Lahore");
        endField.setBounds(200, 90, 250, 35);
        endField.setFont(new Font("Arial", Font.PLAIN, 14));
        endField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 2),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        inputPanel.add(endField);
        
        // Find Route Button
        findRouteBtn = new JButton("🔍 Find Route");
        findRouteBtn.setBounds(500, 40, 150, 40);
        findRouteBtn.setFont(new Font("Arial", Font.BOLD, 14));
        findRouteBtn.setBackground(new Color(46, 204, 113));
        findRouteBtn.setForeground(Color.WHITE);
        findRouteBtn.setFocusPainted(false);
        findRouteBtn.setBorderPainted(false);
        findRouteBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        findRouteBtn.addActionListener(e -> findRoute());
        inputPanel.add(findRouteBtn);
        
        // Clear Button
        clearBtn = new JButton("🗑️ Clear");
        clearBtn.setBounds(500, 90, 150, 40);
        clearBtn.setFont(new Font("Arial", Font.BOLD, 14));
        clearBtn.setBackground(new Color(149, 165, 166));
        clearBtn.setForeground(Color.WHITE);
        clearBtn.setFocusPainted(false);
        clearBtn.setBorderPainted(false);
        clearBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        clearBtn.addActionListener(e -> {
            startField.setText("");
            endField.setText("");
            outputArea.setText("");
            statusLabel.setText("");
        });
        inputPanel.add(clearBtn);
        
        mainPanel.add(inputPanel);
        
        // ========== OUTPUT PANEL ==========
        outputPanel = new JPanel();
        outputPanel.setBounds(50, 310, 800, 300);
        outputPanel.setBackground(Color.WHITE);
        outputPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(52, 152, 219), 2),
            " Route Result ",
            0,
            0,
            new Font("Arial", Font.BOLD, 14),
            new Color(52, 152, 219)
        ));
        outputPanel.setLayout(null);
        
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        outputArea.setForeground(new Color(44, 62, 80));
        outputArea.setBackground(new Color(250, 250, 250));
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);
        outputArea.setText("No route calculated yet. Enter locations and click 'Find Route'.");
        
        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setBounds(20, 30, 760, 250);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199), 1));
        outputPanel.add(scrollPane);
        
        mainPanel.add(outputPanel);
        
        // ========== STATUS LABEL ==========
        statusLabel = new JLabel("", SwingConstants.CENTER);
        statusLabel.setBounds(50, 620, 800, 25);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 13));
        mainPanel.add(statusLabel);
        
        setVisible(true);
    }
    
    private void findRoute() {
        String start = startField.getText().trim();
        String end = endField.getText().trim();
        
        if (start.isEmpty() || end.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please enter both start and end locations!", 
                "Input Required", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        outputArea.setText("⏳ Processing your request...\nCalculating optimal route...\n");
        statusLabel.setForeground(new Color(243, 156, 18));
        statusLabel.setText("⏳ Finding route...");
        
        findRouteBtn.setEnabled(false);
        
        // Simulate processing delay
        Timer timer = new Timer(1000, e -> {
            CppConnector connector = new CppConnector();
            String result = connector.findRoute(start, end);
            
            outputArea.setText(result);
            
            if (result.contains("SUCCESS")) {
                statusLabel.setForeground(new Color(46, 204, 113));
                statusLabel.setText("✓ Route found successfully!");
            } else {
                statusLabel.setForeground(new Color(231, 76, 60));
                statusLabel.setText("✗ Error: Could not find route!");
            }
            
            findRouteBtn.setEnabled(true);
        });
        timer.setRepeats(false);
        timer.start();
    }
    
    private void backToDashboard() {
        Dashboard dashboard = new Dashboard();
        this.dispose();
    }
}