package dasdwdjfhefhj.tracks;
import dasdwdjfhefhj.helpers.*;

public class TrackCursor {
    public TrackSegment segment;
    public double sLocal;

    public void advance(double ds) {
        while (ds > 0 && segment != null) {
            double remaining = segment.getLength() - sLocal;
            if (ds <= remaining) { sLocal += ds; ds = 0; }
            else {
                ds -= remaining;
                segment = segment.getNext(); // Switch zwróci gałąź
                sLocal = 0;
            }
        }
    }

    public Vec2 pos() { return segment.posAt(sLocal); }
    public Vec2 tangent() { return segment.tangentAt(sLocal); }
    public double heading() {
        Vec2 t = tangent();
        return Math.atan2(t.z, t.x);
    }
}

