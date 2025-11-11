package EALiodufiowAMS2.vehicle;

import EALiodufiowAMS2.helpers.Vec2;

import java.util.ArrayList;
import java.util.List;

public class Section {
    private final double length;
    private final double height;
    private final List<Bogie> bogies = new ArrayList<>();

    private Vec2 worldPos = new Vec2(0,0); // środek
    private Vec2 worldDir = new Vec2(1,0);
    private double worldAngle = 0.0;

    // default height 1.8 m
    public Section(double lengthMeters) {
        this(lengthMeters, 4.0);
    }

    public Section(double lengthMeters, double heightMeters) {
        this.length = lengthMeters;
        this.height = heightMeters;
    }

    public double getLength() { return length; }
    public double getHeight() { return height; }

    public Bogie addBogie(double offsetFromStartMeters, double lengthMeters, double heightMeters) {
        Bogie b = new Bogie(offsetFromStartMeters, lengthMeters, heightMeters);
        bogies.add(b);
        return b;
    }

    public List<Bogie> getBogies() { return bogies; }


    public Vec2 getWorldPos() { return worldPos; }
    public Vec2 getWorldDir() { return worldDir; }
    public double getWorldAngle() { return worldAngle; }

    public void setWorld(Vec2 pos, Vec2 dir) {
        this.worldPos = pos;
        this.worldDir = dir.norm();
        this.worldAngle = Math.atan2(worldDir.z, worldDir.x);
    }

    public Vec2 getEnd() {
        return new Vec2(worldPos.x + worldDir.x * (length/2.0), worldPos.z + worldDir.z * (length/2.0));
    }
    public Vec2 getStart() {
        return new Vec2(worldPos.x - worldDir.x * (length/2.0), worldPos.z - worldDir.z * (length/2.0));
    }
}
