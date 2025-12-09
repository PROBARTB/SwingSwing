package EALiodufiowAMS2.tracks;

import EALiodufiowAMS2.helpers.*;

public class CurvedSegment implements TrackSegment {
    private final String id;
    private final double radius;
    private final double arcLength;
    private final double offsetX, offsetZ, deltaAngle;
    private final boolean useArc;

    public CurvedSegment(String id, double radiusMeters, double arcLengthMeters) {
        this.id = id;
        this.radius = radiusMeters;
        this.arcLength = arcLengthMeters;
        this.useArc = true;
        this.offsetX = 0; this.offsetZ = 0; this.deltaAngle = 0;
    }

    public CurvedSegment(String id, double offsetX, double offsetZ, double deltaAngle) {
        this.id = id;
        this.offsetX = offsetX;
        this.offsetZ = offsetZ;
        this.deltaAngle = deltaAngle;
        this.useArc = false;
        this.radius = 0; this.arcLength = 0;
    }

    @Override public String getId() { return id; }

    @Override public double getLength() {
        return useArc ? arcLength : Math.sqrt(offsetX*offsetX + offsetZ*offsetZ);
    }

    @Override
    public Vec3 localPosition(double s) {
        if (useArc) {
            double angle = s / radius;
            return new Vec3(radius * Math.sin(angle), 0, radius * (1 - Math.cos(angle)));
        } else {
            double t = s / getLength();
            return new Vec3(offsetX * t, 0, offsetZ * t);
        }
    }

    @Override
    public Quaternion localRotation(double s) {
        if (useArc) {
            double angle = s / radius;
            return Quaternion.fromEuler(new Vec3(0, angle, 0));
        } else {
            double angle = deltaAngle * (s/getLength());
            return Quaternion.fromEuler(new Vec3(0, angle, 0));
        }
    }

    @Override
    public TrackEndpoint getStart() { return new TrackEndpoint(new Vec3(0,0,0), localRotation(0)); }
    @Override
    public TrackEndpoint getEnd() { return new TrackEndpoint(localPosition(getLength()), localRotation(getLength())); }
}