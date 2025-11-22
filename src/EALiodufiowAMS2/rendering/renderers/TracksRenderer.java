//package EALiodufiowAMS2.rendering;
//
//import EALiodufiowAMS2.helpers.Vec2;
//import EALiodufiowAMS2.tracks.TrackLayout;
//import EALiodufiowAMS2.tracks.TrackSegment;
//
//import java.awt.*;
//import java.awt.geom.Path2D;
//import java.util.List;
//
///**
// * TracksRenderer: rysuje dwie szyny (rozstaw 1.435 m) w XZ.
// * Projekcja zgodna z RenderingEngine: X bez rotacji, Y zależne od Z względem kamery.
// */
//public class TracksRenderer {
//    private static final double GAUGE_M = 1.435;
//    private final RenderingEngine engine;
//
//    public TracksRenderer(RenderingEngine engine) { this.engine = engine; }
//
//    public TrackLayout buildLayout(List<TrackSegment> segments, Vec2 worldStart, double worldHeading) {
//        return new TrackLayout(segments, worldStart, worldHeading);
//    }
//
//    public void drawTracks(Graphics2D g2, TrackLayout layout, Dimension vp, Vec2 cameraPos) {
//        if (layout == null) return;
//        List<TrackLayout.Sample> samples = layout.getSamples();
//        if (samples.isEmpty()) return;
//
//        Path2D leftPath = new Path2D.Double();
//        Path2D rightPath = new Path2D.Double();
//
//        for (int i = 0; i < samples.size(); i++) {
//            TrackLayout.Sample s = samples.get(i);
//            Vec2 pos = s.worldPos;
//            Vec2 dir = s.worldDir;
//
//            Vec2 leftN = new Vec2(-dir.z, dir.x).norm();
//            Vec2 rightN = leftN.scale(-1.0);
//
//            Vec2 leftPos = new Vec2(pos.x + leftN.x * (GAUGE_M / 2.0),
//                    pos.z + leftN.z * (GAUGE_M / 2.0));
//            Vec2 rightPos = new Vec2(pos.x + rightN.x * (GAUGE_M / 2.0),
//                    pos.z + rightN.z * (GAUGE_M / 2.0));
//
//            Point lp = engine.railToScreen(leftPos, vp);
//            Point rp = engine.railToScreen(rightPos, vp);
//
//            if (i == 0) {
//                leftPath.moveTo(lp.x, lp.y);
//                rightPath.moveTo(rp.x, rp.y);
//            } else {
//                leftPath.lineTo(lp.x, lp.y);
//                rightPath.lineTo(rp.x, rp.y);
//            }
//        }
//
//        g2.setColor(new Color(60, 60, 60));
//        g2.setStroke(new BasicStroke(2.0f));
//        g2.draw(leftPath);
//        g2.draw(rightPath);
//    }
//}
