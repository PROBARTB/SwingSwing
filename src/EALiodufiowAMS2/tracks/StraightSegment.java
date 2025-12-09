package EALiodufiowAMS2.tracks;

import EALiodufiowAMS2.helpers.*;

public class StraightSegment implements TrackSegment {
    private final String id;
    private final double length;

    public StraightSegment(String id, double length) {
        this.id = id;
        this.length = length;
    }

    @Override public String getId() { return id; }
    @Override public double getLength() { return length; }

    @Override
    public Vec3 localPosition(double s) {
        return new Vec3(s, 0, 0); // oś X
    }

    @Override
    public Quaternion localRotation(double s) {
        return Quaternion.identity(); // stała orientacja
    }

    @Override
    public TrackEndpoint getStart() { return new TrackEndpoint(new Vec3(0,0,0), localRotation(0)); }
    @Override
    public TrackEndpoint getEnd() { return new TrackEndpoint(localPosition(length), localRotation(length)); }
}
