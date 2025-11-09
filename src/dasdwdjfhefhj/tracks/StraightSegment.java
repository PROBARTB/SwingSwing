package dasdwdjfhefhj.tracks;
import dasdwdjfhefhj.helpers.*;

public class StraightSegment implements TrackSegment {
    private final Vec2 start;
    private final double heading; // kąt w x:z
    private final double length;
    private TrackSegment next;

    public StraightSegment(Vec2 start, double heading, double length) {
        this.start = start;
        this.heading = heading;
        this.length = length;
    }

    public double getLength() { return length; }

    public Vec2 posAt(double s) {
        Vec2 dir = Vec2.fromAngle(heading);
        return start.add(dir.scale(s));
    }

    public Vec2 tangentAt(double s) {
        // jednostkowy kierunek; dla spójności można zwrócić z długością = 1
        return Vec2.fromAngle(heading);
    }

    public TrackSegment getNext() { return next; }
    public void setNext(TrackSegment next) { this.next = next; }

    public double exitHeading() { return heading; }

    public Vec2 getStart() { return start; }
}
