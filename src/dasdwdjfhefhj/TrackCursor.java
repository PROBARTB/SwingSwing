package dasdwdjfhefhj;

public class TrackCursor {
    public TrackSegment segment;
    public double sLocal;
    public void advance(double ds) {
        double remaining = segment.getLength() - sLocal;
        if (ds <= remaining) { sLocal += ds; return; }
        ds -= remaining;
        segment = segment.getNext(null);
        sLocal = 0;
        advance(ds);
    }
    public Vec2 pos() { return segment.posAt(sLocal); }
    public Vec2 tangent() { return segment.tangentAt(sLocal); }
    public double heading() {
        Vec2 t = tangent();
        return Math.atan2(t.z, t.x);
    }
}
