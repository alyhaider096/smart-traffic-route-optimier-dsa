package trafficoptimizer.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import java.util.List;

public class RouteResultPanel extends JPanel {
    private final DefaultTableModel model;
    private JTable table;
    private javax.swing.Timer animTimer;
    
    // Animation states
    private float glowPhase = 0f;
    private float[] sparkles = new float[80];
    private float routeProgress = 0f;
    private float carPosition = 0f;
    private Random rand = new Random();
    
    // Route data
    private String currentPath = "";
    private int currentTime = 0;
    private List<Integer> routeNodes = new ArrayList<>();
    private Map<String, Point> nodePositions = new HashMap<>();
    private int[][] graphEdges = new int[8][8];
    
    // Locations matching C++ backend order
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
    
    // Network topology
    private void initializeGraph() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                graphEdges[i][j] = -1;
            }
        }
        
        addEdge(0, 2, 15);  // E-9 <-> F-8
        addEdge(0, 6, 20);  // E-9 <-> G-8
        addEdge(1, 7, 25);  // H-11 <-> Bahria
        addEdge(2, 3, 10);  // F-8 <-> Blue Area
        addEdge(2, 4, 12);  // F-8 <-> Centaurus
        addEdge(3, 4, 8);   // Blue Area <-> Centaurus
        addEdge(4, 5, 7);   // Centaurus <-> F-9 Park
        addEdge(5, 6, 15);  // F-9 Park <-> G-8
        addEdge(6, 7, 30);  // G-8 <-> Bahria
    }
    
    private void addEdge(int u, int v, int weight) {
        graphEdges[u][v] = weight;
        graphEdges[v][u] = weight;
    }

    public RouteResultPanel() {
        setLayout(null);
        setBackground(new Color(10, 15, 30));
        
        initializeGraph();
        
        for (int i = 0; i < sparkles.length; i++) {
            sparkles[i] = rand.nextFloat();
        }

        // Title with glowing effect
        JLabel title = new JLabel("Route Analysis & Optimization", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 32));
        title.setForeground(Color.WHITE);
        add(title);

        JLabel subtitle = new JLabel("Powered by Dijkstra's Shortest Path Algorithm", SwingConstants.CENTER);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(new Color(180, 200, 255));
        add(subtitle);

        // Results table - COMPACT & CLEAN
        model = new DefaultTableModel(new Object[]{"📊 Metric", "📈 Value", "📝 Details"}, 0);
        table = new JTable(model) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? new Color(20, 30, 55) : new Color(25, 35, 60));
                }
                return c;
            }
        };
        
        table.setRowHeight(40);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setBackground(new Color(20, 30, 55));
        table.setForeground(Color.WHITE);
        table.setSelectionBackground(new Color(90, 150, 255, 120));
        table.setSelectionForeground(Color.WHITE);
        table.setGridColor(new Color(90, 150, 255, 40));
        
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(25, 40, 70));
        table.getTableHeader().setForeground(new Color(180, 200, 255));
        table.getTableHeader().setPreferredSize(new Dimension(0, 38));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(column == 2 ? JLabel.LEFT : JLabel.CENTER);
                setFont(new Font("Segoe UI", column == 1 ? Font.BOLD : Font.PLAIN, 12));
                return c;
            }
        };
        
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(0).setPreferredWidth(180);
        table.getColumnModel().getColumn(1).setPreferredWidth(150);
        table.getColumnModel().getColumn(2).setPreferredWidth(500);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(90, 150, 255, 120), 2),
            BorderFactory.createEmptyBorder(3, 3, 3, 3)
        ));
        scrollPane.getViewport().setBackground(new Color(15, 25, 45));
        add(scrollPane);

        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                positionComponents();
            }
        });

        animTimer = new javax.swing.Timer(35, e -> {
            glowPhase += 0.06f;
            
            if (!routeNodes.isEmpty()) {
                routeProgress += 0.012f;
                if (routeProgress > 1f) routeProgress = 0f;
                
                carPosition += 0.006f;
                if (carPosition > 1f) carPosition = 0f;
            }
            
            for (int i = 0; i < sparkles.length; i++) {
                sparkles[i] += 0.003f;
                if (sparkles[i] > 1f) sparkles[i] = 0f;
            }
            repaint();
        });
        animTimer.start();
    }

    private void positionComponents() {
        int w = getWidth();
        int h = getHeight();
        
        // Title at top
        getComponent(0).setBounds(50, 25, w - 100, 40);
        getComponent(1).setBounds(50, 68, w - 100, 20);
        
        // SPLIT SCREEN: Left = Graph Visualization, Right = Table
        int graphWidth = (int)(w * 0.58);  // 58% for graph
        int tableX = graphWidth + 30;
        int tableWidth = w - tableX - 30;
        
        // Table on RIGHT side - compact and clean
        ((JScrollPane)getComponent(2)).setBounds(tableX, 110, tableWidth, h - 140);
        
        // Update node positions for LEFT side graph
        updateNodePositions(graphWidth);
    }

    private void updateNodePositions(int graphWidth) {
        int centerX = graphWidth / 2;
        int centerY = 380;
        
        // Strategic circular-ish layout optimized for LEFT side
        nodePositions.clear();
        nodePositions.put(locations[0], new Point(centerX - 180, centerY - 140)); // E-9
        nodePositions.put(locations[1], new Point(centerX + 180, centerY - 140)); // H-11
        nodePositions.put(locations[2], new Point(centerX - 120, centerY - 40));  // F-8
        nodePositions.put(locations[3], new Point(centerX - 30, centerY - 90));   // Blue Area
        nodePositions.put(locations[4], new Point(centerX + 80, centerY - 30));   // Centaurus
        nodePositions.put(locations[5], new Point(centerX + 10, centerY + 70));   // F-9 Park
        nodePositions.put(locations[6], new Point(centerX - 120, centerY + 110)); // G-8
        nodePositions.put(locations[7], new Point(centerX + 180, centerY + 110)); // Bahria
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        int graphWidth = (int)(w * 0.58);

        // Sparkle background on LEFT side only
        g2.setColor(new Color(90, 150, 255, 40));
        for (int i = 0; i < sparkles.length; i++) {
            int x = (int) ((i * 15) % graphWidth);
            int y = (int) (sparkles[i] * h);
            int size = 2 + (i % 3);
            g2.fillOval(x, y, size, size);
        }

        // Massive central glow
        int glowSize = 700 + (int)(Math.sin(glowPhase) * 120);
        RadialGradientPaint glow = new RadialGradientPaint(
            graphWidth / 2, 380, glowSize / 2,
            new float[]{0f, 1f},
            new Color[]{new Color(90, 150, 255, 40), new Color(90, 150, 255, 0)}
        );
        g2.setPaint(glow);
        g2.fillOval(graphWidth / 2 - glowSize / 2, 380 - glowSize / 2, glowSize, glowSize);

        // Draw network graph on LEFT side
        drawNetworkGraph(g2, graphWidth);
        
        // Success indicator
        if (currentTime > 0) {
            float pulse = (float)Math.abs(Math.sin(glowPhase * 0.5f));
            
            // Glowing success badge
            int badgeX = graphWidth - 70;
            int badgeY = 30;
            
            g2.setColor(new Color(50, 255, 100, (int)(80 + pulse * 100)));
            g2.fillOval(badgeX - 5, badgeY - 5, 50, 50);
            
            g2.setColor(new Color(50, 255, 100));
            g2.fillOval(badgeX, badgeY, 40, 40);
            
            g2.setColor(new Color(20, 30, 55));
            g2.setFont(new Font("Segoe UI", Font.BOLD, 28));
            g2.drawString("✓", badgeX + 10, badgeY + 30);
        }
    }

    private void drawNetworkGraph(Graphics2D g2, int maxX) {
        // Draw all edges (gray/blue)
        g2.setStroke(new BasicStroke(3f));
        for (int i = 0; i < 8; i++) {
            for (int j = i + 1; j < 8; j++) {
                if (graphEdges[i][j] > 0) {
                    Point p1 = nodePositions.get(locations[i]);
                    Point p2 = nodePositions.get(locations[j]);
                    
                    g2.setColor(new Color(90, 150, 255, 50));
                    g2.drawLine(p1.x, p1.y, p2.x, p2.y);
                    
                    // Weight label
                    int mx = (p1.x + p2.x) / 2;
                    int my = (p1.y + p2.y) / 2;
                    g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
                    g2.setColor(new Color(180, 200, 255, 180));
                    String label = graphEdges[i][j] + "m";
                    FontMetrics fm = g2.getFontMetrics();
                    int labelW = fm.stringWidth(label);
                    
                    // Label background
                    g2.setColor(new Color(20, 30, 55, 220));
                    g2.fillRoundRect(mx - labelW/2 - 4, my - 10, labelW + 8, 18, 6, 6);
                    
                    g2.setColor(new Color(180, 200, 255));
                    g2.drawString(label, mx - labelW/2, my + 3);
                }
            }
        }

        // Draw optimal route (EPIC GREEN GLOW)
        if (routeNodes.size() > 1) {
            for (int i = 0; i < routeNodes.size() - 1; i++) {
                float segmentProgress = Math.max(0, Math.min(1, (routeProgress - i * 0.15f) * 6));
                
                if (segmentProgress > 0) {
                    Point p1 = nodePositions.get(locations[routeNodes.get(i)]);
                    Point p2 = nodePositions.get(locations[routeNodes.get(i + 1)]);
                    
                    int dx = (int)((p2.x - p1.x) * segmentProgress);
                    int dy = (int)((p2.y - p1.y) * segmentProgress);
                    
                    // Outer glow
                    g2.setColor(new Color(50, 255, 100, 60));
                    g2.setStroke(new BasicStroke(16f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2.drawLine(p1.x, p1.y, p1.x + dx, p1.y + dy);
                    
                    // Main route line
                    g2.setColor(new Color(50, 255, 100, 255));
                    g2.setStroke(new BasicStroke(7f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2.drawLine(p1.x, p1.y, p1.x + dx, p1.y + dy);
                    
                    // Inner highlight
                    g2.setColor(new Color(150, 255, 150, 200));
                    g2.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2.drawLine(p1.x, p1.y, p1.x + dx, p1.y + dy);
                }
            }
            
            // Animated car
            if (routeProgress > 0.08f) {
                int segmentIndex = (int)((routeNodes.size() - 1) * carPosition);
                float segmentPos = ((routeNodes.size() - 1) * carPosition) - segmentIndex;
                
                if (segmentIndex < routeNodes.size() - 1) {
                    Point p1 = nodePositions.get(locations[routeNodes.get(segmentIndex)]);
                    Point p2 = nodePositions.get(locations[routeNodes.get(segmentIndex + 1)]);
                    
                    int carX = (int)(p1.x + (p2.x - p1.x) * segmentPos);
                    int carY = (int)(p1.y + (p2.y - p1.y) * segmentPos);
                    
                    drawCar(g2, carX, carY);
                }
            }
        }

        // Draw nodes with EPIC styling
        for (int i = 0; i < locations.length; i++) {
            Point p = nodePositions.get(locations[i]);
            boolean isInRoute = routeNodes.contains(i);
            boolean isStartOrEnd = !routeNodes.isEmpty() && 
                (i == routeNodes.get(0) || i == routeNodes.get(routeNodes.size() - 1));
            
            // Massive node glow for route nodes
            if (isInRoute) {
                float pulse = (float)Math.abs(Math.sin(glowPhase + i * 0.5f));
                int glowSize = 60 + (int)(pulse * 20);
                g2.setColor(new Color(50, 255, 100, (int)(50 + pulse * 50)));
                g2.fillOval(p.x - glowSize/2, p.y - glowSize/2, glowSize, glowSize);
            }
            
            // Node outer ring
            g2.setColor(isInRoute ? new Color(50, 255, 100) : new Color(90, 150, 255));
            g2.fillOval(p.x - 24, p.y - 24, 48, 48);
            
            // Node inner
            g2.setColor(new Color(20, 30, 55));
            g2.fillOval(p.x - 18, p.y - 18, 36, 36);
            
            // Node core
            if (isStartOrEnd) {
                g2.setColor(new Color(255, 220, 50));
                g2.fillOval(p.x - 14, p.y - 14, 28, 28);
            } else {
                g2.setColor(isInRoute ? new Color(50, 255, 100) : new Color(90, 150, 255));
                g2.fillOval(p.x - 14, p.y - 14, 28, 28);
            }
            
            // Node label
            g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
            String label = locations[i];
            if (label.contains("(")) {
                label = label.substring(label.indexOf("("));
            } else {
                label = label.split(" ")[0];
            }
            
            FontMetrics fm = g2.getFontMetrics();
            int labelW = fm.stringWidth(label);
            
            // Label background with glow
            if (isInRoute) {
                g2.setColor(new Color(50, 255, 100, 200));
                g2.fillRoundRect(p.x - labelW/2 - 6, p.y + 30, labelW + 12, 22, 10, 10);
            }
            
            g2.setColor(new Color(20, 30, 55, 240));
            g2.fillRoundRect(p.x - labelW/2 - 5, p.y + 31, labelW + 10, 20, 8, 8);
            
            g2.setColor(isInRoute ? new Color(50, 255, 100) : Color.WHITE);
            g2.drawString(label, p.x - labelW/2, p.y + 45);
        }
    }

    private void drawCar(Graphics2D g2, int x, int y) {
        // Car outer glow
        float pulse = (float)Math.abs(Math.sin(glowPhase * 2));
        g2.setColor(new Color(255, 220, 50, (int)(100 + pulse * 100)));
        g2.fillOval(x - 20, y - 20, 40, 40);
        
        // Car body
        g2.setColor(new Color(255, 220, 50));
        g2.fillRoundRect(x - 14, y - 9, 28, 18, 8, 8);
        
        // Headlights
        g2.setColor(Color.WHITE);
        g2.fillOval(x + 12, y - 5, 6, 6);
        g2.fillOval(x + 12, y + 3, 6, 6);
        
        // Wheels
        g2.setColor(Color.BLACK);
        g2.fillOval(x - 10, y + 10, 7, 7);
        g2.fillOval(x + 5, y + 10, 7, 7);
    }

    public void updateResult(int time, String path) {
        model.setRowCount(0);
        currentTime = time;
        currentPath = path;
        routeProgress = 0f;
        carPosition = 0f;
        routeNodes.clear();
        
        if (time == -1) {
            model.addRow(new Object[]{"❌ Status", "No Route", "No valid path exists"});
            model.addRow(new Object[]{"💡 Suggestion", "Try Different", "Select alternate locations"});
        } else {
            // Parse route nodes
            String[] pathParts = path.split(" → ");
            for (String nodeName : pathParts) {
                for (int i = 0; i < locations.length; i++) {
                    if (locations[i].equals(nodeName)) {
                        routeNodes.add(i);
                        break;
                    }
                }
            }
            
            model.addRow(new Object[]{"✅ Status", "Optimal Found", "Shortest route calculated successfully"});
            model.addRow(new Object[]{"⏱️ Time", time + " min", "Estimated travel duration"});
            model.addRow(new Object[]{"🗺️ Path", pathParts.length + " nodes", path});
            model.addRow(new Object[]{"🔧 Algorithm", "Dijkstra", "Priority Queue | O((V+E) log V)"});
            model.addRow(new Object[]{"🌐 Backend", "C++ Socket", "Real-time graph processing"});
            
            // Route breakdown
            StringBuilder breakdown = new StringBuilder();
            for (int i = 0; i < routeNodes.size() - 1; i++) {
                int u = routeNodes.get(i);
                int v = routeNodes.get(i + 1);
                int segTime = graphEdges[u][v];
                String from = locations[u].split(" \\(")[0];
                String to = locations[v].split(" \\(")[0];
                breakdown.append(from).append("→").append(to).append(" (").append(segTime).append("m)");
                if (i < routeNodes.size() - 2) breakdown.append(" | ");
            }
            model.addRow(new Object[]{"🛣️ Segments", (pathParts.length - 1) + " hops", breakdown.toString()});
        }

        repaint();
    }
}