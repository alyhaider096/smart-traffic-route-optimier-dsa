package trafficoptimizer.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
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

    public TrafficMonitorPanel() {
        setLayout(null);
        setBackground(new Color(10, 15, 30));

        JLabel title = new JLabel("Live Traffic Monitor");
        title.setFont(new Font("Segoe UI", Font.BOLD, 32));
        title.setForeground(Color.WHITE);
        title.setBounds(40, 30, 500, 40);
        add(title);

        JLabel subtitle = new JLabel("Real-time congestion & speed analytics");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(new Color(180, 200, 255));
        subtitle.setBounds(40, 70, 500, 20);
        add(subtitle);

        // ================= TABLE =================
        model = new DefaultTableModel(
                new String[]{"Location", "Status", "Avg Speed", "Congestion"},
                0
        ) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        JTable table = new JTable(model);
        table.setRowHeight(46);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        table.setForeground(Color.WHITE);
        table.setBackground(new Color(18, 25, 45));
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));

        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 15));
        table.getTableHeader().setBackground(new Color(15, 20, 40));
        table.getTableHeader().setForeground(Color.WHITE);

        table.setDefaultRenderer(Object.class, new TrafficCellRenderer());

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBounds(40, 120, 900, 460);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(new Color(10, 15, 30));
        add(scroll);

        populateInitialData();

        // ================= AUTO UPDATE (REAL-TIME SIMULATION) =================
        new Timer(3000, e -> updateTraffic()).start();

        // ================= BACKGROUND ANIMATION =================
        new Timer(40, e -> {
            wave += 0.04f;
            repaint();
        }).start();
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

    // ================= CELL RENDERER =================
    private static class TrafficCellRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int col) {

            JLabel c = (JLabel) super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, col);

            c.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
            c.setOpaque(true);

            String status = table.getValueAt(row, 1).toString();
            int congestion = (int) table.getValueAt(row, 3);

            Color base;
            if (status.equals("Low")) base = new Color(90, 200, 140);
            else if (status.equals("Medium")) base = new Color(255, 200, 90);
            else base = new Color(255, 90, 90);

            if (col == 1) {
                c.setBackground(base);
                c.setForeground(Color.BLACK);
                c.setFont(new Font("Segoe UI", Font.BOLD, 14));
            }
            else if (col == 3) {
                c.setText(congestion + "%");
                c.setBackground(new Color(base.getRed(), base.getGreen(), base.getBlue(), 80));
                c.setForeground(Color.WHITE);
                c.setFont(new Font("Segoe UI", Font.BOLD, 13));
            }
            else {
                c.setBackground(new Color(18, 25, 45));
                c.setForeground(Color.WHITE);
            }

            if (isSelected) {
                c.setBackground(new Color(90, 150, 255));
                c.setForeground(Color.WHITE);
            }

            return c;
        }
    }

    // ================= BACKGROUND EFFECT =================
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Wave glow
        int glow = (int) (300 + Math.sin(wave) * 40);
        g2.setColor(new Color(90, 150, 255, 35));
        g2.fillOval(
                getWidth() / 2 - glow / 2,
                getHeight() / 2 - glow / 2,
                glow, glow
        );
    }
}

