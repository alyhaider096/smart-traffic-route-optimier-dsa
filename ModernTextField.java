package trafficoptimizer.models;

import javax.swing.*;
import java.awt.*;

public class ModernTextField extends JTextField {

    public ModernTextField() {
        setOpaque(false);
        setForeground(Color.WHITE);
        setCaretColor(Color.WHITE);

        setFont(getFont().deriveFont(Font.PLAIN, 14f));
        setBorder(BorderFactory.createLineBorder(new Color(90, 90, 110), 1, true));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
    }
}
