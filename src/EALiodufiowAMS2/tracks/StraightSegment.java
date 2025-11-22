//package EALiodufiowAMS2.tracks;
//
//import EALiodufiowAMS2.helpers.Vec3;
//
//public class StraightSegment implements TrackSegment {
//    private final String id;
//    private final double length;
//
//    public StraightSegment(String id, double lengthMeters) {
//        this.id = id;
//        this.length = Math.max(0, lengthMeters);
//    }
//
//    public String getId() { return id; }
//
//    @Override public double getLength() { return length; }
//
//    @Override
//    public Vec2 localPosAt(double sMeters) {
//        double s = Math.max(0, Math.min(length, sMeters));
//        return new Vec2(s, 0);
//    }
//
//    @Override
//    public Vec2 localTangentAt(double sMeters) {
//        return new Vec2(1, 0);
//    }
//}
