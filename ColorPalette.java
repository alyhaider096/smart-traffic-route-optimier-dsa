package trafficoptimizer.utils;

import java.awt.Color;

public final class ColorPalette {

    private ColorPalette() {}

    // Base VisionOS Dark background
    public static final Color BG_DARK       = new Color(3, 7, 18);
    public static final Color BG_DARK_ALT   = new Color(12, 18, 36);

    // Glass layers
    public static final Color GLASS_LIGHT   = new Color(30, 41, 59, 170);
    public static final Color GLASS_MEDIUM  = new Color(34, 43, 63, 200);
    public static final Color GLASS_STRONG  = new Color(18, 25, 51, 220);

    // Neon accents
    public static final Color ACCENT_TEAL    = new Color(94, 234, 212);
    public static final Color ACCENT_BLUE    = new Color(56, 189, 248);
    public static final Color ACCENT_INDIGO  = new Color(129, 140, 248);
    public static final Color ACCENT_PURPLE  = new Color(168, 85, 247);

    // Text colors
    public static final Color TEXT_PRIMARY    = new Color(248, 250, 252);
    public static final Color TEXT_SECONDARY  = new Color(180, 190, 200);
    public static final Color TEXT_MUTED      = new Color(110, 122, 140);

    // Borders / glows
    public static final Color BORDER_SOFT     = new Color(45, 55, 72);
    public static final Color BORDER_GLOW     = new Color(94, 234, 212, 160);

    // Status
    public static final Color SUCCESS         = new Color(34, 197, 94);
    public static final Color WARNING         = new Color(245, 158, 11);
    public static final Color DANGER          = new Color(248, 113, 113);
}
