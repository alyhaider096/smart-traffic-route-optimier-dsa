package trafficoptimizer.utils;

public final class AnimationUtils {

    private AnimationUtils() {}

    // Smooth VisionOS easing curve
    public static float easeInOut(float t) {
        return (float)(-0.5 * (Math.cos(Math.PI * t) - 1));
    }

    // Linear interpolation
    public static float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }
}
