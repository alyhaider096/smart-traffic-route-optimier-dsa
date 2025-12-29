package trafficoptimizer.models;

public class RouteData {

    private final String source;
    private final String destination;

    private final double distanceKm;
    private final double travelTimeMinutes;
    private final double congestionLevel; // 0.0 → 1.0

    public RouteData(String source, String destination, double distanceKm,
                     double travelTimeMinutes, double congestionLevel) {

        this.source = source;
        this.destination = destination;
        this.distanceKm = distanceKm;
        this.travelTimeMinutes = travelTimeMinutes;
        this.congestionLevel = congestionLevel;
    }

    public String getSource()            { return source; }
    public String getDestination()       { return destination; }
    public double getDistanceKm()        { return distanceKm; }
    public double getTravelTimeMinutes() { return travelTimeMinutes; }
    public double getCongestionLevel()   { return congestionLevel; }
}
