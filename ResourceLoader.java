package trafficoptimizer.utils;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.HashMap;

public final class ResourceLoader {

    // ------------------------------------------------------------------
    // FULL LIST OF VISUALS + ICONS FOR THE ENTIRE APP (VISIONOS THEME)
    // ------------------------------------------------------------------
    private static final String[] ASSETS = {

            // --- Background Visuals ---
            "city-glow.png",          // blurred city glow behind map
            "neon-lines.png",         // blue/purple neon streaks
            "glass-bubble.png",       // soft blurred circles
            "grid-overlay.png",       // subtle VisionOS grid

            // --- Login / Signup Hero Images ---
            "login-hero.png",
            "signup-hero.png",
            "orb-gradient.png",       // blurred orb for background
            "vision-ring.png",        // loading ring graphic

            // --- Sidebar Duotone Icons ---
            "icon-dashboard.png",
            "icon-route.png",
            "icon-results.png",
            "icon-simulation.png",
            "icon-monitor.png",

            // --- Routing Visuals ---
            "route-doodle.png",
            "pin-source.png",
            "pin-destination.png",
            "road-network.png",

            // --- Traffic Visuals ---
            "hotspot-glow.png",
            "incident-marker.png",
            "speed-indicator.png",

            // --- Simulation Visuals ---
            "sim-wave.png",
            "sim-flow.png",

            // --- Graph Enhancements ---
            "dotted-path.png",
            "gradient-fill.png",

            // --- Map Additional Layers ---
            "roads-overlay.png",
            "city-nodes.png",

            // --- Optional UI Decorations ---
            "corner-flare.png",
            "soft-vignette.png"
    };

    // ------------------------------------------------------------------
    // CACHE
    // ------------------------------------------------------------------
    private static final HashMap<String, ImageIcon> cache = new HashMap<>();

    static {
        // Preload everything into the cache if available
        for (String asset : ASSETS) {
            ImageIcon icon = loadInternal(asset);
            if (icon != null)
                cache.put(asset, icon);
            else
                System.err.println("⚠ Missing image (not crashing): " + asset);
        }
    }

    private static ImageIcon loadInternal(String name) {
        try {
            URL url = ResourceLoader.class.getResource("/images/" + name);
            if (url == null) return null;
            return new ImageIcon(url);
        } catch (Exception e) {
            return null;
        }
    }

    // ------------------------------------------------------------------
    // PUBLIC METHODS
    // ------------------------------------------------------------------

    public static ImageIcon loadIcon(String name) {
        if (cache.containsKey(name))
            return cache.get(name);

        ImageIcon icon = loadInternal(name);
        if (icon != null) cache.put(name, icon);
        return icon;
    }

    public static Image loadImage(String name) {
        ImageIcon icon = loadIcon(name);
        return icon != null ? icon.getImage() : null;
    }

    public static Image scaled(String name, int w, int h) {
        Image img = loadImage(name);
        if (img == null) return null;
        return img.getScaledInstance(w, h, Image.SCALE_SMOOTH);
    }
}
