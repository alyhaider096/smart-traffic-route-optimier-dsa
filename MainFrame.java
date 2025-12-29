package trafficoptimizer.ui;

import trafficoptimizer.utils.NetworkClient;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    // ===== PAGE KEYS =====
    public static final String DASH_OVERVIEW   = "dash_overview";
    public static final String DASHBOARD       = "dashboard";
    public static final String PLAN_ROUTE      = "plan_route";
    public static final String ROUTE_RESULTS   = "route_results";
    public static final String SIMULATION      = "simulation";
    public static final String TRAFFIC_MONITOR = "traffic_monitor";

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel mainPanel = new JPanel(cardLayout);

    private RouteResultPanel routeResultPanel;
    private NetworkClient client;

    public MainFrame() {
        setTitle("Smart Traffic Route Optimizer");
        setSize(1300, 780);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // ===== CONNECT BACKEND =====
        try {
            client = new NetworkClient("127.0.0.1", 8080);
            System.out.println("Java → Backend connected");
        } catch (Exception e) {
            client = null;
            JOptionPane.showMessageDialog(
                    this,
                    "Backend not running!\nStart C++ backend first.",
                    "Backend Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }

        add(createSidebar(), BorderLayout.WEST);

        mainPanel.setBackground(new Color(10, 15, 30));

        // IMPORTANT: use the REAL class name exactly
        mainPanel.add(new DashBoardOverviewPanel(this), DASH_OVERVIEW);
        mainPanel.add(new DashBoardPanel(), DASHBOARD);
        mainPanel.add(new RouteSelectionPanel(this, client), PLAN_ROUTE);

        routeResultPanel = new RouteResultPanel();
        mainPanel.add(routeResultPanel, ROUTE_RESULTS);

        mainPanel.add(new SimulationPanel(), SIMULATION);
        mainPanel.add(new TrafficMonitorPanel(), TRAFFIC_MONITOR);

        add(mainPanel, BorderLayout.CENTER);

        showPage(DASH_OVERVIEW);
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel(new GridLayout(5, 1, 15, 15));
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setBackground(new Color(8, 12, 25));
        sidebar.setBorder(BorderFactory.createEmptyBorder(25, 15, 25, 15));

        sidebar.add(sideButton("Dashboard", DASH_OVERVIEW));
        sidebar.add(sideButton("Plan Route", PLAN_ROUTE));
        sidebar.add(sideButton("Route Results", ROUTE_RESULTS));
        sidebar.add(sideButton("Simulation", SIMULATION));
        sidebar.add(sideButton("Traffic Monitor", TRAFFIC_MONITOR));

        return sidebar;
    }

    private JButton sideButton(String text, String page) {
        JButton btn = new JButton(text);
        btn.setBackground(new Color(18, 25, 45));
        btn.setForeground(new Color(210, 225, 255));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> showPage(page));
        return btn;
    }

    // called by RouteSelectionPanel after backend response
    public void showRouteResults(int time, String path) {
        routeResultPanel.updateResult(time, path);
        showPage(ROUTE_RESULTS);
    }

    public void showPage(String key) {
        cardLayout.show(mainPanel, key);
    }
}
