package trafficoptimizer.models;

import trafficoptimizer.components.GlassCard;
import trafficoptimizer.utils.ColorPalette;
import trafficoptimizer.utils.FontManager;
import trafficoptimizer.utils.ResourceLoader;

import javax.swing.*;
import java.awt.*;

public class StatsCard extends GlassCard {

    private final JLabel titleLabel;
    private final JLabel valueLabel;
    private final JLabel subtitleLabel;

    public StatsCard(String title, String value, String subtitle, String iconName) {

        setLayout(new BorderLayout(10, 10));

        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        header.setOpaque(false);

        if (iconName != null) {
            Image icon = ResourceLoader.scaled(iconName, 26, 26);
            if (icon != null)
                header.add(new JLabel(new ImageIcon(icon)));
        }

        titleLabel = new JLabel(title);
        titleLabel.setFont(FontManager.subtitleFont());
        titleLabel.setForeground(ColorPalette.TEXT_SECONDARY);

        header.add(titleLabel);

        valueLabel = new JLabel(value);
        valueLabel.setFont(FontManager.metricFont());
        valueLabel.setForeground(Color.WHITE);

        subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(FontManager.tinyFont());
        subtitleLabel.setForeground(ColorPalette.TEXT_MUTED);

        add(header, BorderLayout.NORTH);
        add(valueLabel, BorderLayout.CENTER);
        add(subtitleLabel, BorderLayout.SOUTH);
    }

    public void setValue(String value) {
        valueLabel.setText(value);
    }
}
