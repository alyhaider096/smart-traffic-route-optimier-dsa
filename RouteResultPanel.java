package trafficoptimizer.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.geom.*;
import java.util.Random;

public class RouteResultPanel extends JPanel {
    private final DefaultTableModel model;
    private JTable table;
    private float progressAnim = 0f;
    private float glowPhase = 0f;
    private Random rand = new Random();
    private float[] sparkles = new float[100];
    private float mapAnimation = 0f;
    private Timer animTimer;
    private String currentPath = "";
    private int currentTime = 0;
    private float carPosition = 0f;

    public RouteResultPanel() {
        setLayout(null);
        setBackground(new Color(10, 15, 30));

        for (int i = 0; i < sparkles.length; i++) {
            sparkles[i] = rand.nextFloat();
        }

        // Title with icon
        JLabel iconLabel = new JLabel("🎯", SwingConstants.CENTER);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 56));
        add(iconLabel);

        JLabel title = new JLabel("Route Analysis & Optimization");
        title.setFont(new Font("Segoe UI", Font.BOLD, 38));
        title.setForeground(Color.WHITE);
        add(title);

        JLabel subtitle = new JLabel("Powered by Dijkstra's Shortest Path Algorithm");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        subtitle.setForeground(new Color(180, 200, 255));
        add(subtitle);

        model = new DefaultTableModel(
                new Object[]{"Metric", "Value", "Details"},
                0
        );

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
        
        table.setRowHeight(55);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        table.setBackground(new Color(20, 30, 55));
        table.setForeground(Color.WHITE);
        table.setSelectionBackground(new Color(90, 150, 255, 120));
        table.setSelectionForeground(Color.WHITE);
        table.setGridColor(new Color(90, 150, 255, 40));
        table.setShowGrid(true);
        table.setIntercellSpacing(new Dimension(1, 1));
        
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 16));
        table.getTableHeader().setBackground(new Color(25, 40, 70));
        table.getTableHeader().setForeground(new Color(180, 200, 255));
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setPreferredSize(new Dimension(0, 45));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(JLabel.CENTER);
                setFont(new Font("Segoe UI", Font.BOLD, 14));
                return c;
            }
        };
        
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(0).setPreferredWidth(250);
        table.getColumnModel().getColumn(1).setPreferredWidth(200);
        table.getColumnModel().getColumn(2).setPreferredWidth(650);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(90, 150, 255, 120), 3),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        scrollPane.getViewport().setBackground(new Color(15, 25, 45));
        add(scrollPane);

        // Algorithm visualization panel
        JPanel algoPanel = createAlgorithmPanel();
        add(algoPanel);

        // Stats cards
        JPanel statsPanel = createStatsPanel();
        add(statsPanel);

        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                positionComponents();
            }
        });

        animTimer = new Timer(35, e -> {
            progressAnim += 0.008f;
            if (progressAnim > 1f) progressAnim = 0f;
            
            glowPhase += 0.06f;
            mapAnimation += 0.01f;
            carPosition += 0.01f;
            if (carPosition > 1f) carPosition = 0f;
            
            for (int i = 0; i < sparkles.length; i++) {
                sparkles[i] += 0.004f;
                if (sparkles[i] > 1f) sparkles[i] = 0f;
            }
            repaint();
        });
        animTimer.start();
    }

    private JPanel createAlgorithmPanel() {
        JPanel panel = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2.setColor(new Color(20, 35, 65, 220));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 28, 28);
                
                g2.setColor(new Color(90, 150, 255, 100));
                g2.setStroke(new BasicStroke(3));
                g2.drawRoundRect(2, 2, getWidth() - 5, getHeight() - 5, 28, 28);
                
                // Animated route visualization
                int cx = getWidth() / 2;
                int cy = getHeight() / 2;
                
                // Draw nodes
                for (int i = 0; i < 5; i++) {
                    double angle = (Math.PI * 2 * i / 5) + mapAnimation;
                    int nx = cx + (int)(80 * Math.cos(angle));
                    int ny = cy + (int)(80 * Math.sin(angle));
                    
                    g2.setColor(new Color(90, 150, 255, 180));
                    g2.fillOval(nx - 10, ny - 10, 20, 20);
                    
                    g2.setColor(new Color(255, 255, 255, 200));
                    g2.setStroke(new BasicStroke(2));
                    g2.drawOval(nx - 10, ny - 10, 20, 20);
                }
                
                // Draw connecting lines
                g2.setColor(new Color(90, 150, 255, 60));
                g2.setStroke(new BasicStroke(2));
                for (int i = 0; i < 5; i++) {
                    double angle1 = (Math.PI * 2 * i / 5) + mapAnimation;
                    double angle2 = (Math.PI * 2 * (i + 1) / 5) + mapAnimation;
                    int x1 = cx + (int)(80 * Math.cos(angle1));
                    int y1 = cy + (int)(80 * Math.sin(angle1));
                    int x2 = cx + (int)(80 * Math.cos(angle2));
                    int y2 = cy + (int)(80 * Math.sin(angle2));
                    g2.drawLine(x1, y1, x2, y2);
                }
            }
        };
        panel.setOpaque(false);
        
        JLabel algoTitle = new JLabel("📊 Algorithm Visualization", SwingConstants.CENTER);
        algoTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        algoTitle.setForeground(new Color(180, 200, 255));
        algoTitle.setBounds(0, 15, 300, 30);
        panel.add(algoTitle);
        
        return panel;
    }

    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2.setColor(new Color(20, 35, 65, 220));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 28, 28);
                
                g2.setColor(new Color(90, 150, 255, 100));
                g2.setStroke(new BasicStroke(3));
                g2.drawRoundRect(2, 2, getWidth() - 5, getHeight() - 5, 28, 28);
            }
        };
        panel.setOpaque(false);
        
        JLabel statsTitle = new JLabel("🔍 Route Statistics", SwingConstants.CENTER);
        statsTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        statsTitle.setForeground(new Color(180, 200, 255));
        statsTitle.setBounds(0, 15, 300, 30);
        panel.add(statsTitle);
        
        String[] stats = {
            "• Graph: Adjacency List",
            "• Priority Queue: Min-Heap",
            "• Time Complexity: O((V+E) log V)",
            "• Space Complexity: O(V)",
            "• Backend: C++ Socket Server"
        };

        int yPos = 55;
        for (String stat : stats) {
            JLabel label = new JLabel(stat);
            label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            label.setForeground(Color.WHITE);
            label.setBounds(25, yPos, 270, 25);
            panel.add(label);
            yPos += 32;
        }
        
        return panel;
    }

    private void positionComponents() {
        int w = getWidth();
        int h = getHeight();
        
        getComponent(0).setBounds((w - 70) / 2, 30, 70, 70);  // Icon
        getComponent(1).setBounds(50, 110, w - 100, 45);  // Title
        getComponent(2).setBounds(50, 160, w - 100, 25);  // Subtitle
        
        ((JScrollPane)getComponent(3)).setBounds(50, 210, w - 100, h - 520);
        
        int panelY = h - 280;
        ((JPanel)getComponent(4)).setBounds(50, panelY, 300, 250);  // Algo panel
        ((JPanel)getComponent(5)).setBounds(w - 350, panelY, 300, 250);  // Stats panel
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // Animated sparkles
        g2.setColor(new Color(90, 150, 255, 70));
        for (int i = 0; i < sparkles.length; i++) {
            int x = (int) ((i * 20) % w);
            int y = (int) (sparkles[i] * h);
            int size = 2 + (i % 4);
            g2.fillOval(x, y, size, size);
        }

        // Central glow
        int glowSize = 500 + (int)(Math.sin(glowPhase) * 80);
        RadialGradientPaint glow = new RadialGradientPaint(
            w / 2, 200, glowSize / 2,
            new float[]{0f, 1f},
            new Color[]{new Color(90, 150, 255, 40), new Color(90, 150, 255, 0)}
        );
        g2.setPaint(glow);
        g2.fillOval(w / 2 - glowSize / 2, 200 - glowSize / 2, glowSize, glowSize);

        // Decorative route line
        if (!currentPath.isEmpty()) {
            g2.setColor(new Color(90, 150, 255, 50));
            g2.setStroke(new BasicStroke(4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                                         0, new float[]{20, 10}, progressAnim * 30));
            
            Path2D path = new Path2D.Float();
            path.moveTo(380, h - 350);
            path.curveTo(w / 2 - 100, h - 300, w / 2 + 100, h - 300, w - 380, h - 350);
            g2.draw(path);
            
            // Moving car on path
            int carX = (int)(380 + (w - 760) * carPosition);
            int carY = h - 350 + (int)(Math.sin(carPosition * Math.PI) * -30);
            drawCarOnPath(g2, carX, carY);
        }

        // Success indicator when route exists
        if (currentTime > 0) {
            float pulse = (float)Math.abs(Math.sin(glowPhase * 0.5f));
            g2.setColor(new Color(50, 255, 50, (int)(100 + pulse * 100)));
            g2.fillOval(w - 80, 30, 40, 40);
            
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 24));
            g2.drawString("✓", w - 68, 58);
        }
    }

    private void drawCarOnPath(Graphics2D g2, int x, int y) {
        g2.setColor(new Color(90, 150, 255, 200));
        g2.fillRoundRect(x - 15, y - 8, 30, 16, 8, 8);
        
        g2.setColor(Color.WHITE);
        g2.fillOval(x + 12, y - 3, 4, 4);
        g2.fillOval(x + 12, y + 3, 4, 4);
        
        g2.setColor(Color.BLACK);
        g2.fillOval(x - 10, y + 6, 8, 8);
        g2.fillOval(x + 2, y + 6, 8, 8);
    }

    public void updateResult(int time, String path) {
        model.setRowCount(0);
        currentTime = time;
        currentPath = path;
        carPosition = 0f;
        
        if (time == -1) {
            model.addRow(new Object[]{
                    "❌ Status",
                    "No Route Found",
                    "No valid path exists between the selected locations"
            });
            
            model.addRow(new Object[]{
                    "📍 Suggestion",
                    "Try Different Locations",
                    "Select alternate source or destination points"
            });
            
        } else {
            model.addRow(new Object[]{
                    "✅ Route Status",
                    "Optimal Path Found",
                    "Shortest route calculated using Dijkstra's algorithm"
            });
            
            model.addRow(new Object[]{
                    "⏱️ Travel Time",
                    time + " minutes",
                    "Estimated time based on current traffic conditions"
            });

            model.addRow(new Object[]{
                    "🗺️ Route Path",
                    "Multi-Node",
                    path
            });

            String[] pathNodes = path.split(" → ");
            model.addRow(new Object[]{
                    "📊 Total Intersections",
                    pathNodes.length + " nodes",
                    "Number of waypoints in the optimal route"
            });

            model.addRow(new Object[]{
                    "🔧 Algorithm",
                    "Dijkstra's Shortest Path",
                    "Priority Queue (Min-Heap) | Time: O((V+E) log V)"
            });

            model.addRow(new Object[]{
                    "🌐 Data Source",
                    "C++ Backend Server",
                    "Real-time graph processing with socket communication"
            });

            model.addRow(new Object[]{
                    "💾 Data Structure",
                    "Adjacency List + HashMap",
                    "Efficient graph representation for route optimization"
            });
        }

        progressAnim = 0f;
        repaint();
    }
}