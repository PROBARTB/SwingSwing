import java.awt.*;

abstract class Tram extends RailVehicle {
    protected int length;

    public Tram(double maxSpeed, int length) {
        super(maxSpeed);
        this.length = length;
    }

    public abstract void draw(Graphics2D g);
}