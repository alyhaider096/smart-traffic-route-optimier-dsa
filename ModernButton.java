package trafficoptimizer.components;

import trafficoptimizer.utils.ColorPalette;
import trafficoptimizer.utils.FontManager;
import trafficoptimizer.utils.ResourceLoader;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

public class ModernButton extends JButton {

    private boolean hovered = false;
    private boolean pressed = false;
    private final boolean sidebarStyle;
    private Image iconImage = null;

    public ModernButton(String text) {
        this(text, false, null);
    }

    public ModernButton(String text, boolean sidebarStyle) {
        this(text, sidebarStyle, null);
    }

    public ModernButton(String text, boolean sidebarStyle, String iconName) {
        super(text);
        this.sidebarStyle = sidebarStyle;

        // Load icon if specified
        if (iconName != null) {
            Image img = ResourceLoader.loadImage(iconName);
            if (img != null)
                iconImage = img.getScaledInstance(22, 22, Image.SCALE_SMOOTH);
        }

        setFont(FontManager.sidebarFont());
        setForeground(ColorPalette.TEXT_SECONDARY);

        setBorder(new EmptyBorder(10, 18, 10, 18));
        setOpaque(false);
        setFocusPainted(false);
        setContentAreaFilled(false);
        setHorizontalAlignment(SwingConstants.LEFT);
        setIconTextGap(12);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { hovered = true; repaint(); }
            @Override public void mouseExited(MouseEvent e)  { hovered = false; pressed = false; repaint(); }
            @Override public void mousePressed(MouseEvent e) { pressed = true; repaint(); }
            @Override public void mouseReleased(MouseEvent e){ pressed = false; repaint(); }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        if (sidebarStyle) {
            // background
            Color bg = hovered ? new Color(35, 47, 70, 240)
                               : new Color(30, 40, 60, 180);
            g2.setColor(bg);
            g2.fillRoundRect(0, 0, w, h, 18, 18);

            // glow
            if (hovered) {
                g2.setColor(ColorPalette.ACCENT_TEAL);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(1, 1, w - 3, h - 3, 18, 18);
                setForeground(Color.WHITE);
            } else {
                setForeground(ColorPalette.TEXT_SECONDARY);
            }
        }

        // icon rendering
        if (iconImage != null) {
            int size = 22;
            int y = (h - size) / 2;
            g2.drawImage(iconImage, 14, y, size, size, null);
        }

        g2.dispose();
        super.paintComponent(g);
    }

    @Override
    public void setContentAreaFilled(boolean b) {}
}
