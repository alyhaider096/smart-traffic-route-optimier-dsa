package trafficoptimizer.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import java.util.List;

public class LiveMapPanel extends JPanel {
    private javax.swing.Timer animTimer;
    private float glowPhase = 0f;
    private List<MovingVehicle> vehicles = new ArrayList<>();
    private Map<String, Point> nodePositions = new HashMap<>();
    private int[][] edges = new int[8][8];
    private float[] particles = new float[80];
    private Random rand = new Random();
    
    private final String[] locations = {
        "E-9", "H-11", "F-8", "Blue Area", 
        "Centaurus", "F-9 Park", "G-8", "Bahria"
    };
    
    private int totalVehicles = 0;
    private int routesCompleted = 0;

    class MovingVehicle {
        List<Integer> route;  // Complete path to follow
        int currentRouteIndex;
        float progress;
        float speed;
        Color color;
        String id;
        
        MovingVehicle(List<Integer> route, String id) {
            this.route = route;
            this.currentRouteIndex = 0;
            this.progress = 0f;
            this.speed = 0.004f + (float)(Math.random() * 0.006f);
            
            int r = 100 + (int)(Math.random() * 155);
            int g = 100 + (int)(Math.random() * 155);
            int b = 100 + (int)(Math.random() * 155);
            this.color = new Color(r, g, b);
            this.id = id;
        }
        
        void update() {
            progress += speed;
            if (progress >= 1f) {
                progress = 0f;
                currentRouteIndex++;
                
                // If reached end of route, generate new route
                if (currentRouteIndex >= route.size() - 1) {
                    route = generateRandomRoute();
                    currentRouteIndex = 0;
                    routesCompleted++;
                }
            }
        }
        
        Point getPosition() {
            if (currentRouteIndex >= route.size() - 1) {
                return nodePositions.get(locations[route.get(route.size() - 1)]);
            }
            
            Point p1 = nodePositions.get(locations[route.get(currentRouteIndex)]);
            Point p2 = nodePositions.get(locations[route.get(currentRouteIndex + 1)]);
            
            if (p1 == null || p2 == null) return new Point(0, 0);
            
            int x = (int)(p1.x + (p2.x - p1.x) * progress);
            int y = (int)(p1.y + (p2.y - p1.y) * progress);
            
            return new Point(x, y);
        }
        
        String getCurrentSegment() {
            if (currentRouteIndex >= route.size() - 1) return "";
            return locations[route.get(currentRouteIndex)] + " → " + 
                   locations[route.get(currentRouteIndex + 1)];
        }
    }

    public LiveMapPanel() {
        setLayout(null);
        setBackground(new Color(10, 15, 30));
        
        for (int i = 0; i < particles.length; i++) {
            particles[i] = rand.nextFloat();
        }
        
        initializeGraph();
        spawnVehicles();

        JLabel title = new JLabel("Live Traffic Simulation", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 42));
        title.setForeground(Color.WHITE);
        add(title);

        JLabel subtitle = new JLabel("Vehicles follow real graph edges using Dijkstra's algorithm", SwingConstants.CENTER);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        subtitle.setForeground(new Color(180, 200, 255));
        add(subtitle);

        JButton addVehicleBtn = createGlowButton("➕ Add Vehicle", new Color(50, 255, 100));
        addVehicleBtn.addActionListener(e -> {
            List<Integer> route = generateRandomRoute();
            vehicles.add(new MovingVehicle(route, "V-" + (totalVehicles++)));
        });
        add(addVehicleBtn);

        JButton clearBtn = createGlowButton("🗑️ Clear All", new Color(255, 80, 80));
        clearBtn.addActionListener(e -> {
            vehicles.clear();
            routesCompleted = 0;
        });
        add(clearBtn);
        
        JButton autoSpawnBtn = createGlowButton("🚀 Auto Spawn", new Color(255, 150, 50));
        autoSpawnBtn.addActionListener(e -> {
            for (int i = 0; i < 3; i++) {
                List<Integer> route = generateRandomRoute();
                vehicles.add(new MovingVehicle(route, "V-" + (totalVehicles++)));
            }
        });
        add(autoSpawnBtn);

        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                positionComponents();
            }
        });

        animTimer = new javax.swing.Timer(25, e -> {
            glowPhase += 0.03f;
            
            for (MovingVehicle v : vehicles) {
                v.update();
            }
            
            for (int i = 0; i < particles.length; i++) {
                particles[i] += 0.002f;
                if (particles[i] > 1f) particles[i] = 0f;
            }
            
            repaint();
        });
        animTimer.start();
    }
    
    // ============ DIJKSTRA'S ALGORITHM ============
    private List<Integer> findShortestPath(int start, int end) {
        int[] dist = new int[8];
        int[] parent = new int[8];
        boolean[] visited = new boolean[8];
        
        Arrays.fill(dist, 999999);
        Arrays.fill(parent, -1);
        dist[start] = 0;
        
        for (int count = 0; count < 8; count++) {
            int u = -1;
            int minDist = 999999;
            
            for (int i = 0; i < 8; i++) {
                if (!visited[i] && dist[i] < minDist) {
                    minDist = dist[i];
                    u = i;
                }
            }
            
            if (u == -1) break;
            visited[u] = true;
            
            for (int v = 0; v < 8; v++) {
                if (edges[u][v] > 0 && !visited[v]) {
                    int newDist = dist[u] + edges[u][v];
                    if (newDist < dist[v]) {
                        dist[v] = newDist;
                        parent[v] = u;
                    }
                }
            }
        }
        
        // Build path
        List<Integer> path = new ArrayList<>();
        if (dist[end] == 999999) return path; // No path exists
        
        int current = end;
        while (current != -1) {
            path.add(0, current);
            current = parent[current];
        }
        
        return path;
    }
    
    private List<Integer> generateRandomRoute() {
        int start = rand.nextInt(8);
        int end = rand.nextInt(8);
        
        // Make sure start and end are different
        while (end == start) {
            end = rand.nextInt(8);
        }
        
        // Find shortest path using Dijkstra
        List<Integer> path = findShortestPath(start, end);
        
        // If no path found, try another pair
        if (path.isEmpty()) {
            return generateRandomRoute();
        }
        
        return path;
    }
    
    private JButton createGlowButton(String text, Color baseColor) {
        JButton btn = new JButton(text) {
            private float pulse = 0f;
            private javax.swing.Timer pulseTimer;
            
            {
                pulseTimer = new javax.swing.Timer(40, e -> {
                    pulse += 0.08f;
                    repaint();
                });
                pulseTimer.start();
            }
            
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                float glowAlpha = (float)Math.abs(Math.sin(pulse)) * 0.4f + 0.3f;
                g2.setColor(new Color(baseColor.getRed(), baseColor.getGreen(), 
                                      baseColor.getBlue(), (int)(glowAlpha * 255)));
                g2.fillRoundRect(-5, -5, getWidth() + 10, getHeight() + 10, 20, 20);
                
                GradientPaint gradient = new GradientPaint(
                    0, 0, baseColor,
                    0, getHeight(), baseColor.darker()
                );
                g2.setPaint(gradient);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                
                g2.setFont(getFont());
                g2.setColor(Color.WHITE);
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), x, y);
            }
        };
        
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        return btn;
    }

    private void initializeGraph() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                edges[i][j] = -1;
            }
        }
        
        // Same edges as backend
        edges[0][2] = edges[2][0] = 15;
        edges[0][6] = edges[6][0] = 20;
        edges[1][7] = edges[7][1] = 25;
        edges[2][3] = edges[3][2] = 10;
        edges[2][4] = edges[4][2] = 12;
        edges[3][4] = edges[4][3] = 8;
        edges[4][5] = edges[5][4] = 7;
        edges[5][6] = edges[6][5] = 15;
        edges[6][7] = edges[7][6] = 30;
    }
    
    private void updateNodePositions(int w, int h) {
        int centerX = w / 2;
        int centerY = h / 2 + 20;
        
        nodePositions.clear();
        nodePositions.put("E-9", new Point(centerX - 240, centerY - 200));
        nodePositions.put("H-11", new Point(centerX + 240, centerY - 200));
        nodePositions.put("F-8", new Point(centerX - 150, centerY - 70));
        nodePositions.put("Blue Area", new Point(centerX - 50, centerY - 130));
        nodePositions.put("Centaurus", new Point(centerX + 110, centerY - 60));
        nodePositions.put("F-9 Park", new Point(centerX + 30, centerY + 90));
        nodePositions.put("G-8", new Point(centerX - 150, centerY + 150));
        nodePositions.put("Bahria", new Point(centerX + 220, centerY + 150));
    }

    private void spawnVehicles() {
        for (int i = 0; i < 8; i++) {
            List<Integer> route = generateRandomRoute();
            vehicles.add(new MovingVehicle(route, "V-" + (totalVehicles++)));
        }
    }

    private void positionComponents() {
        int w = getWidth();
        int h = getHeight();
        
        getComponent(0).setBounds(50, 30, w - 100, 50);
        getComponent(1).setBounds(50, 85, w - 100, 25);
        getComponent(2).setBounds(w - 560, 35, 160, 50);
        getComponent(3).setBounds(w - 380, 35, 160, 50);
        getComponent(4).setBounds(w - 200, 35, 160, 50);
        
        updateNodePositions(w, h);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // Particles
        g2.setColor(new Color(90, 150, 255, 35));
        for (int i = 0; i < particles.length; i++) {
            int x = (int) ((i * 25) % w);
            int y = (int) (particles[i] * h);
            int size = 2 + (i % 3);
            g2.fillOval(x, y, size, size);
        }

        // Background glow
        int glowSize = 1100 + (int)(Math.sin(glowPhase) * 180);
        RadialGradientPaint glow = new RadialGradientPaint(
            w / 2, h / 2, glowSize / 2,
            new float[]{0f, 1f},
            new Color[]{new Color(90, 150, 255, 12), new Color(90, 150, 255, 0)}
        );
        g2.setPaint(glow);
        g2.fillOval(w / 2 - glowSize / 2, h / 2 - glowSize / 2, glowSize, glowSize);

        drawRoads(g2);
        drawNodes(g2);
        drawVehicles(g2);
        drawStats(g2, w, h);
    }

    private void drawRoads(Graphics2D g2) {
        g2.setStroke(new BasicStroke(5f));
        
        for (int i = 0; i < 8; i++) {
            for (int j = i + 1; j < 8; j++) {
                if (edges[i][j] > 0) {
                    Point p1 = nodePositions.get(locations[i]);
                    Point p2 = nodePositions.get(locations[j]);
                    
                    if (p1 != null && p2 != null) {
                        g2.setColor(new Color(90, 150, 255, 70));
                        g2.drawLine(p1.x, p1.y, p2.x, p2.y);
                        
                        // Draw edge weight
                        int mx = (p1.x + p2.x) / 2;
                        int my = (p1.y + p2.y) / 2;
                        
                        g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
                        String weight = edges[i][j] + "m";
                        FontMetrics fm = g2.getFontMetrics();
                        int labelW = fm.stringWidth(weight);
                        
                        g2.setColor(new Color(20, 30, 55, 220));
                        g2.fillRoundRect(mx - labelW/2 - 4, my - 10, labelW + 8, 18, 6, 6);
                        
                        g2.setColor(new Color(180, 200, 255));
                        g2.drawString(weight, mx - labelW/2, my + 3);
                    }
                }
            }
        }
    }

    private void drawNodes(Graphics2D g2) {
        for (int i = 0; i < locations.length; i++) {
            Point p = nodePositions.get(locations[i]);
            if (p == null) continue;
            
            float pulse = (float)Math.abs(Math.sin(glowPhase + i * 0.5f));
            int glowSize = (int)(60 + pulse * 15);
            g2.setColor(new Color(90, 150, 255, (int)(30 + pulse * 20)));
            g2.fillOval(p.x - glowSize/2, p.y - glowSize/2, glowSize, glowSize);
            
            g2.setColor(new Color(90, 150, 255));
            g2.fillOval(p.x - 26, p.y - 26, 52, 52);
            
            g2.setColor(new Color(20, 30, 55));
            g2.fillOval(p.x - 20, p.y - 20, 40, 40);
            
            g2.setColor(new Color(90, 150, 255));
            g2.fillOval(p.x - 14, p.y - 14, 28, 28);
            
            g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
            String label = locations[i];
            FontMetrics fm = g2.getFontMetrics();
            int labelW = fm.stringWidth(label);
            
            g2.setColor(new Color(20, 30, 55, 240));
            g2.fillRoundRect(p.x - labelW/2 - 6, p.y + 30, labelW + 12, 24, 10, 10);
            
            g2.setColor(new Color(180, 200, 255));
            g2.drawString(label, p.x - labelW/2, p.y + 47);
        }
    }

    private void drawVehicles(Graphics2D g2) {
        for (MovingVehicle v : vehicles) {
            Point pos = v.getPosition();
            
            // Trail glow
            float pulse = (float)Math.abs(Math.sin(glowPhase + v.id.hashCode()));
            g2.setColor(new Color(v.color.getRed(), v.color.getGreen(), 
                                  v.color.getBlue(), (int)(60 + pulse * 100)));
            g2.fillOval(pos.x - 22, pos.y - 22, 44, 44);
            
            // Vehicle body
            g2.setColor(v.color);
            g2.fillRoundRect(pos.x - 14, pos.y - 9, 28, 18, 8, 8);
            
            // Headlights
            g2.setColor(Color.WHITE);
            g2.fillOval(pos.x + 12, pos.y - 5, 5, 5);
            g2.fillOval(pos.x + 12, pos.y + 2, 5, 5);
            
            // Wheels
            g2.setColor(Color.BLACK);
            g2.fillOval(pos.x - 12, pos.y + 8, 8, 8);
            g2.fillOval(pos.x + 4, pos.y + 8, 8, 8);
        }
    }

    private void drawStats(Graphics2D g2, int w, int h) {
        g2.setColor(new Color(20, 35, 65, 240));
        g2.fillRoundRect(25, h - 130, 300, 100, 18, 18);
        
        g2.setColor(new Color(90, 150, 255, 150));
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(25, h - 130, 300, 100, 18, 18);
        
        g2.setFont(new Font("Segoe UI", Font.BOLD, 15));
        g2.setColor(new Color(180, 200, 255));
        g2.drawString("📊 Live Statistics", 42, h - 103);
        
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        g2.setColor(Color.WHITE);
        g2.drawString("Active Vehicles: " + vehicles.size(), 42, h - 78);
        g2.drawString("Routes Generated: " + totalVehicles, 42, h - 58);
        g2.drawString("Routes Completed: " + routesCompleted, 42, h - 38);
    }
}