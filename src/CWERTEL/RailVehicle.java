package CWERTEL;

public abstract class RailVehicle {
    protected final double maxSpeed;

    public RailVehicle(double maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public double getMaxSpeed() {
        return maxSpeed;
    }
}

