package trafficoptimizer.ui;

import trafficoptimizer.utils.NetworkClient;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;

public class MainFrame extends JFrame {
    // ===== PAGE KEYS =====
    public static final String DASH_OVERVIEW   = "dash_overview";
    public static final String LIVE_MAP        = "live_map";
    public static final String PLAN_ROUTE      = "plan_route";
    public static final String ROUTE_RESULTS   = "route_results";
    public static final String TRAFFIC_MONITOR = "traffic_monitor";
    
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel mainPanel = new JPanel(cardLayout);
    private RouteResultPanel routeResultPanel;
    private NetworkClient client;
    
    private JButton[] navButtons = new JButton[5];
    private String currentPage = DASH_OVERVIEW;

    public MainFrame() {
        setTitle("Smart Traffic Route Optimizer");
        setSize(1400, 820);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // ===== CONNECT BACKEND =====
        try {
            client = new NetworkClient("127.0.0.1", 8080);
            System.out.println("✓ Java → Backend connected");
        } catch (Exception e) {
            client = null;
            System.err.println("⚠ Backend not connected - some features disabled");
        }
        
        add(createAnimatedSidebar(), BorderLayout.WEST);
        
        mainPanel.setBackground(new Color(10, 15, 30));
        
        // Add all panels
        mainPanel.add(new DashBoardOverviewPanel(this), DASH_OVERVIEW);
        mainPanel.add(new LiveMapPanel(), LIVE_MAP);
        mainPanel.add(new RouteSelectionPanel(this, client), PLAN_ROUTE);
        
        routeResultPanel = new RouteResultPanel();
        mainPanel.add(routeResultPanel, ROUTE_RESULTS);
        
        mainPanel.add(new TrafficMonitorPanel(), TRAFFIC_MONITOR);
        
        add(mainPanel, BorderLayout.CENTER);
        
        showPage(DASH_OVERVIEW);
    }
    
    private JPanel createAnimatedSidebar() {
        JPanel sidebar = new AnimatedSidebar();
        sidebar.setPreferredSize(new Dimension(240, 0));
        sidebar.setLayout(null);
        
        // Logo area
        JLabel logo = new JLabel("🚦", SwingConstants.CENTER);
        logo.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        logo.setBounds(0, 20, 240, 60);
        sidebar.add(logo);
        
        JLabel appName = new JLabel("Traffic Optimizer", SwingConstants.CENTER);
        appName.setFont(new Font("Segoe UI", Font.BOLD, 18));
        appName.setForeground(Color.WHITE);
        appName.setBounds(0, 85, 240, 25);
        sidebar.add(appName);
        
        JLabel version = new JLabel("v2.0 Ultimate", SwingConstants.CENTER);
        version.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        version.setForeground(new Color(180, 200, 255));
        version.setBounds(0, 110, 240, 20);
        sidebar.add(version);
        
        // Navigation buttons
        String[] labels = {"🏠 Dashboard", "🗺️ Live Map", "📍 Plan Route", "📊 Results", "⚠️ Monitor"};
        String[] pages = {DASH_OVERVIEW, LIVE_MAP, PLAN_ROUTE, ROUTE_RESULTS, TRAFFIC_MONITOR};
        
        int yPos = 160;
        for (int i = 0; i < 5; i++) {
            navButtons[i] = createNavButton(labels[i], pages[i], i);
            navButtons[i].setBounds(20, yPos, 200, 55);
            sidebar.add(navButtons[i]);
            yPos += 70;
        }
        
        // Connection status
        JPanel statusPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2.setColor(new Color(20, 35, 65, 200));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                
                g2.setColor(client != null ? new Color(50, 255, 100) : new Color(255, 80, 80));
                g2.fillOval(15, 15, 10, 10);
                
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                g2.setColor(Color.WHITE);
                g2.drawString(client != null ? "Backend Online" : "Backend Offline", 32, 24);
            }
        };
        statusPanel.setOpaque(false);
        statusPanel.setBounds(20, 720, 200, 40);
        sidebar.add(statusPanel);
        
        return sidebar;
    }
    
    private JButton createNavButton(String text, String page, int index) {
        JButton btn = new JButton(text) {
            private float hoverAnim = 0f;
            private boolean isActive = false;
            private javax.swing.Timer animTimer;
            
            {
                animTimer = new javax.swing.Timer(30, e -> {
                    if (isActive && hoverAnim < 1f) {
                        hoverAnim += 0.1f;
                    } else if (!isActive && hoverAnim > 0f) {
                        hoverAnim -= 0.1f;
                    }
                    repaint();
                });
                animTimer.start();
                
                addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseEntered(java.awt.event.MouseEvent e) {
                        if (!currentPage.equals(page)) {
                            isActive = true;
                        }
                    }
                    
                    @Override
                    public void mouseExited(java.awt.event.MouseEvent e) {
                        if (!currentPage.equals(page)) {
                            isActive = false;
                        }
                    }
                });
            }
            
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                boolean selected = currentPage.equals(page);
                
                // Background
                if (selected) {
                    GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(90, 150, 255),
                        0, getHeight(), new Color(70, 120, 220)
                    );
                    g2.setPaint(gradient);
                } else {
                    g2.setColor(new Color(18, 25, 45, (int)(100 + hoverAnim * 100)));
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                
                // Hover glow
                if (hoverAnim > 0 && !selected) {
                    g2.setColor(new Color(90, 150, 255, (int)(hoverAnim * 50)));
                    g2.setStroke(new BasicStroke(2));
                    g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 12, 12);
                }
                
                // Selection indicator
                if (selected) {
                    g2.setColor(new Color(255, 255, 255, 200));
                    g2.fillRoundRect(5, getHeight() / 2 - 15, 4, 30, 2, 2);
                }
                
                // Text
                g2.setFont(getFont());
                g2.setColor(selected ? Color.WHITE : new Color(180, 200, 255));
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), x, y);
            }
        };
        
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> showPage(page));
        
        return btn;
    }
    
    public void showRouteResults(int time, String path) {
        routeResultPanel.updateResult(time, path);
        showPage(ROUTE_RESULTS);
    }
    
    public void showPage(String key) {
        currentPage = key;
        cardLayout.show(mainPanel, key);
        
        // Update button states
        for (JButton btn : navButtons) {
            btn.repaint();
        }
    }
    
    // Animated sidebar background
    class AnimatedSidebar extends JPanel {
        private javax.swing.Timer timer;
        private float phase = 0f;
        
        public AnimatedSidebar() {
            setOpaque(true);
            timer = new javax.swing.Timer(50, e -> {
                phase += 0.02f;
                repaint();
            });
            timer.start();
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Base gradient
            GradientPaint gradient = new GradientPaint(
                0, 0, new Color(8, 12, 25),
                0, getHeight(), new Color(15, 20, 38)
            );
            g2.setPaint(gradient);
            g2.fillRect(0, 0, getWidth(), getHeight());
            
            // Animated glow
            int glowY = (int)(Math.sin(phase) * 100) + 200;
            RadialGradientPaint glow = new RadialGradientPaint(
                getWidth() / 2, glowY, 200,
                new float[]{0f, 1f},
                new Color[]{new Color(90, 150, 255, 30), new Color(90, 150, 255, 0)}
            );
            g2.setPaint(glow);
            g2.fillRect(0, 0, getWidth(), getHeight());
            
            // Border
            g2.setColor(new Color(90, 150, 255, 40));
            g2.setStroke(new BasicStroke(2));
            g2.drawLine(getWidth() - 1, 0, getWidth() - 1, getHeight());
        }
    }
}