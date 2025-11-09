package dasdwdjfhefhj;

import java.awt.*;
import java.awt.geom.Path2D;

public class WorldRenderer {
    private final double sampleStep = 0.5; // m

    public void drawTracks(Graphics2D g2, TrackSegment start) {
        Path2D left = new Path2D.Double();
        Path2D right = new Path2D.Double();
        TrackSegment seg = start;
        // przejdź kilka segmentów lub cały graf wedle potrzeb
        for (int i = 0; i < 100; i++) {
            for (double s = 0; s <= seg.getLength(); s += sampleStep) {
                Vec2 p = seg.posAt(s);
                Vec2 t = seg.tangentAt(s);
                Vec2 nHat = new Vec2(-t.z, t.x).norm();
                double w = 0.725; // półrozstaw (np. 0.725 m dla 1435 mm/2)
                Vec2 pL = p.add(nHat.scale(w));
                Vec2 pR = p.sub(nHat.scale(w));
                if (i == 0 && s == 0) { left.moveTo(pL.x, pL.z); right.moveTo(pR.x, pR.z); }
                else { left.lineTo(pL.x, pL.z); right.lineTo(pR.x, pR.z); }
            }
            seg = seg.getNext(null);
            if (seg == null) break;
        }
        g2.draw(left); g2.draw(right);
    }
}