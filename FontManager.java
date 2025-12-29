package trafficoptimizer.utils;

import java.awt.Font;
import java.awt.GraphicsEnvironment;

public final class FontManager {

    private static Font base = new Font("SansSerif", Font.PLAIN, 14);

    private static final String[] APPLE_FONTS = {
            "SF Pro Display", "SF Pro Text", "SF Pro", "SF UI Text", "Helvetica Neue"
    };

    static {
        try {
            String[] fonts = GraphicsEnvironment
                    .getLocalGraphicsEnvironment()
                    .getAvailableFontFamilyNames();

            outer:
            for (String preferred : APPLE_FONTS) {
                for (String f : fonts) {
                    if (preferred.equalsIgnoreCase(f)) {
                        base = new Font(f, Font.PLAIN, 14);
                        break outer;
                    }
                }
            }
        } catch (Exception ignored) {}
    }

    public static Font titleFont()      { return base.deriveFont(Font.BOLD, 30f); }
    public static Font headingFont()    { return base.deriveFont(Font.BOLD, 22f); }
    public static Font metricFont()     { return base.deriveFont(Font.BOLD, 26f); }
    public static Font subtitleFont()   { return base.deriveFont(Font.PLAIN, 15f); }
    public static Font bodyFont()       { return base.deriveFont(Font.PLAIN, 13f); }
    public static Font tinyFont()       { return base.deriveFont(Font.PLAIN, 11f); }
    public static Font sidebarFont()    { return base.deriveFont(Font.BOLD, 15f); }
    public static Font buttonFont()     { return base.deriveFont(Font.BOLD, 14f); }

    private FontManager() {}
}
