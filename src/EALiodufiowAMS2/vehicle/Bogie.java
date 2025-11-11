package EALiodufiowAMS2.vehicle;

import EALiodufiowAMS2.helpers.Vec2;

public class Bogie {
    private final double offsetInSection;
    private final double height;
    private final double length;

    private double sAlongTrack;
    private Vec2 worldPos = new Vec2(0,0);
    private Vec2 worldDir = new Vec2(1,0);


    public Bogie(double offsetInSection, double lengthMeters, double heightMeters) {
        this.offsetInSection = offsetInSection;
        this.length = lengthMeters;
        this.height = heightMeters;
    }

    public double getOffsetInSection() { return offsetInSection; }

    public double getSAlongTrack() { return sAlongTrack; }
    public void setSAlongTrack(double s) { this.sAlongTrack = s; }

    public Vec2 getWorldPos() { return worldPos; }
    public Vec2 getWorldDir() { return worldDir; }
    public void setWorld(Vec2 pos, Vec2 dir) { this.worldPos = pos; this.worldDir = dir.norm(); }

    public double getHeight() { return height; }
    public double getLength() { return length; }
}
