package trafficoptimizer.models;

public class TrafficData {

    private final String area;
    private final int congestion;  // 0–100
    private final int averageSpeed;
    private final int incidents;

    public TrafficData(String area, int congestion, int averageSpeed, int incidents) {
        this.area = area;
        this.congestion = congestion;
        this.averageSpeed = averageSpeed;
        this.incidents = incidents;
    }

    public String getArea()        { return area; }
    public int getCongestion()     { return congestion; }
    public int getAverageSpeed()   { return averageSpeed; }
    public int getIncidents()      { return incidents; }
}
