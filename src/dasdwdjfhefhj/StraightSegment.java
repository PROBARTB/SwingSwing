package dasdwdjfhefhj;

public class StraightSegment implements TrackSegment {
    private final double length;
    private final Vec2 start;
    private final double heading; // kąt w płaszczyźnie x:z

    public StraightSegment(Vec2 start, double heading, double length) {
        this.start = start; this.heading = heading; this.length = length;
    }

    public double getLength() { return length; }
    public Vec2 posAt(double s) { return start.add(Vec2.fromAngle(heading).scale(s)); }
    public Vec2 tangentAt(double s) { return Vec2.fromAngle(heading); }
    public double curvatureAt(double s) { return 0.0; }
    public TrackSegment getNext(Vec2 choiceHint) { return next; }
    private TrackSegment next;
    public void setNext(TrackSegment next) { this.next = next; }
}
