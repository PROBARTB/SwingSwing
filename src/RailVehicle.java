abstract class RailVehicle {
    protected double speed = 0;       // current speed (px/s)
    protected double acceleration = 0; // current acceleration (px/s^2)
    protected double maxSpeed;

    public RailVehicle(double maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public void update(double deltaTime) {
        speed += acceleration * deltaTime;
        if (speed < 0) speed = 0;
        if (speed > maxSpeed) speed = maxSpeed;
    }

    public double getSpeed() { return speed; }
    public void setAcceleration(double a) { this.acceleration = a; }
}