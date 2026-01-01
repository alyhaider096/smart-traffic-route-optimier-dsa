package trafficoptimizer.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.geom.*;
import java.util.Random;

public class TrafficMonitorPanel extends JPanel {

    private final String[] locations = {
            "Air University (E-9)",
            "Air University (H-11)",
            "F-8 Sector",
            "Blue Area",
            "Centaurus Mall",
            "F-9 Park",
            "G-8 Sector",
            "Bahria Town"
    };

    private final DefaultTableModel model;
    private final Random rand = new Random();

    private float wave = 0f;
    private float[] particles = new float[60];

    public TrafficMonitorPanel() {
        setLayout(null);
        setBackground(new Color(10, 15, 30));
        
        for (int i = 0; i < particles.length; i++) {
            particles[i] = rand.nextFloat();
        }

        // Title with icon
        JLabel icon = new JLabel("⚠️", SwingConstants.CENTER);
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 44));
        icon.setBounds(30, 25, 60, 60);
        add(icon);

        JLabel title = new JLabel("Live Traffic Monitor");
        title.setFont(new Font("Segoe UI", Font.BOLD, 38));
        title.setForeground(Color.WHITE);
        title.setBounds(100, 35, 500, 45);
        add(title);

        JLabel subtitle = new JLabel("Real-time congestion & speed analytics • Auto-refresh every 3s");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(new Color(180, 200, 255));
        subtitle.setBounds(100, 80, 600, 20);
        add(subtitle);

        // Stats cards
        StatsCard avgSpeedCard = new StatsCard("🚗", "Avg Speed", "45 km/h", new Color(50, 255, 100));
        avgSpeedCard.setBounds(800, 30, 180, 90);
        add(avgSpeedCard);

        StatsCard congestionCard = new StatsCard("🚦", "Congestion", "Medium", new Color(255, 150, 50));
        congestionCard.setBounds(1000, 30, 180, 90);
        add(congestionCard);

        // ================= TABLE =================
        model = new DefaultTableModel(
                new String[]{"📍 Location", "🚦 Status", "⚡ Speed", "📊 Congestion"},
                0
        ) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        JTable table = new JTable(model);
        table.setRowHeight(52);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        table.setForeground(Color.WHITE);
        table.setBackground(new Color(18, 25, 45));
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));

        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 16));
        table.getTableHeader().setBackground(new Color(15, 20, 40));
        table.getTableHeader().setForeground(new Color(180, 200, 255));
        table.getTableHeader().setPreferredSize(new Dimension(0, 50));

        table.setDefaultRenderer(Object.class, new TrafficCellRenderer());

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBounds(40, 140, 1140, 520);
        scroll.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(90, 150, 255, 100), 2),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        scroll.getViewport().setBackground(new Color(10, 15, 30));
        add(scroll);

        populateInitialData();

        // ================= AUTO UPDATE =================
        Timer updateTimer = new Timer(3000, e -> {
            updateTraffic();
            updateStatsCards(avgSpeedCard, congestionCard);
        });
        updateTimer.start();

        // ================= ANIMATION =================
        Timer animTimer = new Timer(35, e -> {
            wave += 0.04f;
            
            for (int i = 0; i < particles.length; i++) {
                particles[i] += 0.002f;
                if (particles[i] > 1f) particles[i] = 0f;
            }
            
            repaint();
        });
        animTimer.start();
    }

    // ================= DATA =================
    private void populateInitialData() {
        model.setRowCount(0);
        for (String loc : locations) {
            addRandomRow(loc);
        }
    }

    private void updateTraffic() {
        model.setRowCount(0);
        for (String loc : locations) {
            addRandomRow(loc);
        }
    }

    private void addRandomRow(String location) {
        int congestion = rand.nextInt(100);

        String status;
        if (congestion < 35) status = "Low";
        else if (congestion < 70) status = "Medium";
        else status = "High";

        int speed = 25 + rand.nextInt(35);

        model.addRow(new Object[]{
                location,
                status,
                speed + " km/h",
                congestion
        });
    }

    private void updateStatsCards(StatsCard speedCard, StatsCard congestionCard) {
        int totalSpeed = 0;
        int totalCongestion = 0;
        
        for (int i = 0; i < model.getRowCount(); i++) {
            String speedStr = model.getValueAt(i, 2).toString();
            speedStr = speedStr.replace(" km/h", "");
            totalSpeed += Integer.parseInt(speedStr);
            totalCongestion += (int) model.getValueAt(i, 3);
        }
        
        int avgSpeed = totalSpeed / model.getRowCount();
        int avgCongestion = totalCongestion / model.getRowCount();
        
        speedCard.updateValue(avgSpeed + " km/h");
        
        String congStatus = avgCongestion < 35 ? "Low" : (avgCongestion < 70 ? "Medium" : "High");
        congestionCard.updateValue(congStatus);
    }

    // ================= STATS CARD =================
    class StatsCard extends JPanel {
        private String icon, label, value;
        private Color accentColor;
        private float pulse = 0f;
        
        StatsCard(String icon, String label, String value, Color accentColor) {
            this.icon = icon;
            this.label = label;
            this.value = value;
            this.accentColor = accentColor;
            setOpaque(false);
            
            Timer pulseTimer = new Timer(40, e -> {
                pulse += 0.06f;
                repaint();
            });
            pulseTimer.start();
        }
        
        void updateValue(String newValue) {
            this.value = newValue;
            repaint();
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Card background
            g2.setColor(new Color(20, 35, 65, 230));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
            
            // Pulsing border
            float pulseAlpha = (float)Math.abs(Math.sin(pulse)) * 0.5f + 0.5f;
            g2.setColor(new Color(accentColor.getRed(), accentColor.getGreen(), 
                                  accentColor.getBlue(), (int)(pulseAlpha * 150)));
            g2.setStroke(new BasicStroke(2));
            g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 18, 18);
            
            // Icon
            g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
            g2.setColor(accentColor);
            g2.drawString(icon, 15, 40);
            
            // Label
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            g2.setColor(new Color(180, 200, 255));
            g2.drawString(label, 55, 30);
            
            // Value
            g2.setFont(new Font("Segoe UI", Font.BOLD, 20));
            g2.setColor(Color.WHITE);
            g2.drawString(value, 55, 55);
        }
    }

    // ================= CELL RENDERER =================
    private static class TrafficCellRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int col) {

            JLabel c = (JLabel) super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, col);

            c.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
            c.setOpaque(true);

            String status = table.getValueAt(row, 1).toString();
            int congestion = (int) table.getValueAt(row, 3);

            Color base;
            if (status.equals("Low")) base = new Color(90, 200, 140);
            else if (status.equals("Medium")) base = new Color(255, 200, 90);
            else base = new Color(255, 90, 90);

            if (col == 1) {
                // Status column
                c.setBackground(base);
                c.setForeground(Color.BLACK);
                c.setFont(new Font("Segoe UI", Font.BOLD, 15));
                c.setHorizontalAlignment(SwingConstants.CENTER);
            }
            else if (col == 3) {
                // Congestion column
                c.setText(congestion + "%");
                c.setBackground(new Color(base.getRed(), base.getGreen(), base.getBlue(), 100));
                c.setForeground(Color.WHITE);
                c.setFont(new Font("Segoe UI", Font.BOLD, 14));
                c.setHorizontalAlignment(SwingConstants.CENTER);
            }
            else {
                // Other columns
                c.setBackground(row % 2 == 0 ? new Color(18, 25, 45) : new Color(22, 30, 50));
                c.setForeground(Color.WHITE);
                c.setHorizontalAlignment(col == 0 ? SwingConstants.LEFT : SwingConstants.CENTER);
            }

            if (isSelected) {
                c.setBackground(new Color(90, 150, 255, 180));
                c.setForeground(Color.WHITE);
            }

            return c;
        }
    }

    // ================= BACKGROUND =================
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // Particles
        g2.setColor(new Color(90, 150, 255, 40));
        for (int i = 0; i < particles.length; i++) {
            int x = (int) ((i * 25) % w);
            int y = (int) (particles[i] * h);
            int size = 2 + (i % 3);
            g2.fillOval(x, y, size, size);
        }

        // Central glow
        int glow = (int) (400 + Math.sin(wave) * 50);
        RadialGradientPaint radialGlow = new RadialGradientPaint(
            w / 2, 80, glow / 2,
            new float[]{0f, 1f},
            new Color[]{new Color(90, 150, 255, 40), new Color(90, 150, 255, 0)}
        );
        g2.setPaint(radialGlow);
        g2.fillOval(w / 2 - glow / 2, 80 - glow / 2, glow, glow);
    }
}