package smartrafficroute.optimizer.gui;

import smartrafficroute.optimizer.integration.CppConnector;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class TrafficSimulation extends JFrame {

    private JPanel mainPanel;
    private JPanel headerPanel;
    private JPanel controlPanel;
    private JPanel displayPanel;

    private JTextArea simulationArea;

    private JButton startBtn;
    private JButton stopBtn;
    private JButton refreshBtn;
    private JButton backBtn;

    public TrafficSimulation() {
        initComponents();
        setupUI();
    }

    private void initComponents() {
        setTitle("Traffic Simulation - Smart Traffic Optimizer");
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

        // ================= HEADER ==================
        headerPanel = new JPanel();
        headerPanel.setBounds(0, 0, 900, 80);
        headerPanel.setBackground(new Color(46, 134, 193));
        headerPanel.setLayout(null);

        JLabel titleLabel = new JLabel("Traffic Simulation");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 26));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(30, 20, 400, 40);
        headerPanel.add(titleLabel);

        backBtn = new JButton("← Back");
        backBtn.setBounds(780, 25, 90, 30);
        backBtn.setBackground(new Color(189, 195, 199));
        backBtn.setForeground(Color.BLACK);
        backBtn.setBorderPainted(false);
        backBtn.setFocusPainted(false);
        backBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backBtn.addActionListener(e -> {
            new Dashboard();
            dispose();
        });
        headerPanel.add(backBtn);

        mainPanel.add(headerPanel);

        // ================= CONTROL PANEL ==================
        controlPanel = new JPanel();
        controlPanel.setBounds(50, 100, 800, 100);
        controlPanel.setBackground(Color.WHITE);
        controlPanel.setBorder(BorderFactory.createLineBorder(new Color(46, 134, 193), 2));
        controlPanel.setLayout(null);

        startBtn = new JButton("Start Simulation");
        startBtn.setBounds(40, 30, 170, 40);
        startBtn.setBackground(new Color(39, 174, 96));
        startBtn.setForeground(Color.WHITE);
        startBtn.setFocusPainted(false);
        startBtn.addActionListener(e -> startSimulation());
        controlPanel.add(startBtn);

        stopBtn = new JButton("Stop");
        stopBtn.setBounds(230, 30, 120, 40);
        stopBtn.setBackground(new Color(231, 76, 60));
        stopBtn.setForeground(Color.WHITE);
        stopBtn.setFocusPainted(false);
        stopBtn.addActionListener(e -> stopSimulation());
        controlPanel.add(stopBtn);

        refreshBtn = new JButton("Refresh");
        refreshBtn.setBounds(370, 30, 120, 40);
        refreshBtn.setBackground(new Color(52, 152, 219));
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.setFocusPainted(false);
        refreshBtn.addActionListener(e -> loadTrafficData());
        controlPanel.add(refreshBtn);

        mainPanel.add(controlPanel);

        // ================= DISPLAY PANEL ==================
        displayPanel = new JPanel();
        displayPanel.setBounds(50, 220, 800, 360);
        displayPanel.setBackground(Color.WHITE);
        displayPanel.setBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(46, 134, 193), 2),
                        " Real-Time Traffic Data ",
                        0, 0,
                        new Font("Arial", Font.BOLD, 16),
                        new Color(46, 134, 193)
                )
        );
        displayPanel.setLayout(null);

        simulationArea = new JTextArea();
        simulationArea.setEditable(false);
        simulationArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        simulationArea.setForeground(Color.BLACK);
        simulationArea.setBackground(new Color(250, 250, 250));
        simulationArea.setMargin(new Insets(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(simulationArea);
        scrollPane.setBounds(20, 30, 760, 300);
        displayPanel.add(scrollPane);

        mainPanel.add(displayPanel);

        setVisible(true);
    }

    // ========================== LOGIC METHODS =============================

    private void startSimulation() {
        simulationArea.setText("⏳ Starting simulation...\n\nTraffic is updating...\n");
        Timer timer = new Timer(1200, e -> loadTrafficData());
        timer.setRepeats(false);
        timer.start();
    }

    private void stopSimulation() {
        simulationArea.setText("⛔ Simulation stopped.\n");
    }

    private void loadTrafficData() {
        CppConnector connector = new CppConnector();
        String data = connector.getTrafficSimulation();

        simulationArea.setText(data);
    }
}
