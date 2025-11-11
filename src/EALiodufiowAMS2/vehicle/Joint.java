package EALiodufiowAMS2.vehicle;

public class Joint {
    private final double height;
    private final double length;
    private final double maxBendRadians;

    public Joint(double lengthMeters, double heightMeters, double maxBendRadians) {
        this.height = heightMeters;
        this.length = lengthMeters;
        this.maxBendRadians = maxBendRadians;
    }

    public double getHeight() {
        return height;
    }
    public double getLength() {
        return length;
    }
    public double getMaxBendRadians() {
        return maxBendRadians;
    }

    public double clampAngleDifference(double baseAngle, double targetAngle) {
        double diff = normalize(targetAngle - baseAngle);
        if (diff > maxBendRadians) diff = maxBendRadians;
        if (diff < -maxBendRadians) diff = -maxBendRadians;
        return normalize(baseAngle + diff);
    }

    private double normalize(double a) {
        while (a > Math.PI) a -= 2*Math.PI;
        while (a < -Math.PI) a += 2*Math.PI;
        return a;
    }
}
