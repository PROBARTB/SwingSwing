//package EALiodufiowAMS2.tracks;
//
//import EALiodufiowAMS2.helpers.Vec2;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * TrackLayout: układa segmenty globalnie (bez modyfikacji segmentów).
// * Generuje próbki (worldPos, worldDir, sGlobal) dla renderingu i ruchu pojazdu.
// */
//public class TrackLayout {
//    public static class Sample {
//        public final Vec2 worldPos;
//        public final Vec2 worldDir;
//        public final double sGlobal;
//        public Sample(Vec2 p, Vec2 d, double s) { worldPos = p; worldDir = d; sGlobal = s; }
//    }
//
//    private final List<Sample> samples = new ArrayList<>();
//    private double totalLength = 0.0;
//
//    public TrackLayout(List<TrackSegment> segments, Vec2 worldStart, double worldHeading) {
//        build(segments, worldStart, worldHeading);
//    }
//
//    private void build(List<TrackSegment> segments, Vec2 startPos, double startHeading) {
//        samples.clear();
//        totalLength = 0.0;
//
//        Vec2 curOrigin = startPos;
//        double curHeading = startHeading;
//        int samplesPerSegment = 96;
//
//        for (TrackSegment seg : segments) {
//            double segLen = seg.getLength();
//            for (int i = 0; i <= samplesPerSegment; i++) {
//                double sLocal = segLen * i / samplesPerSegment;
//                Vec2 lp = seg.localPosAt(sLocal);
//                Vec2 ld = seg.localTangentAt(sLocal).norm();
//
//                double ca = Math.cos(curHeading), sa = Math.sin(curHeading);
//                Vec2 wp = new Vec2(curOrigin.x + lp.x * ca - lp.z * sa,
//                        curOrigin.z + lp.x * sa + lp.z * ca);
//                Vec2 wd = new Vec2(ld.x * ca - ld.z * sa,
//                        ld.x * sa + ld.z * ca).norm();
//
//                double sGlobal = totalLength + sLocal;
//                samples.add(new Sample(wp, wd, sGlobal));
//            }
//
//            // Przejście na koniec segmentu
//            Vec2 lend = seg.localPosAt(segLen);
//            Vec2 ledir = seg.localTangentAt(segLen).norm();
//            double ca = Math.cos(curHeading), sa = Math.sin(curHeading);
//            Vec2 wend = new Vec2(curOrigin.x + lend.x * ca - lend.z * sa,
//                    curOrigin.z + lend.x * sa + lend.z * ca);
//            Vec2 wexitDir = new Vec2(ledir.x * ca - ledir.z * sa,
//                    ledir.x * sa + ledir.z * ca).norm();
//            curOrigin = wend;
//            curHeading = Math.atan2(wexitDir.z, wexitDir.x);
//
//            totalLength += segLen;
//        }
//    }
//
//    public List<Sample> getSamples() { return samples; }
//    public double getTotalLength() { return totalLength; }
//
//    public Sample sampleAt(double sGlobal) {
//        if (samples.isEmpty()) return null;
//        if (sGlobal <= 0) return samples.get(0);
//        if (sGlobal >= samples.get(samples.size() - 1).sGlobal) return samples.get(samples.size() - 1);
//
//        Sample prev = samples.get(0);
//        for (int i = 1; i < samples.size(); i++) {
//            Sample cur = samples.get(i);
//            if (cur.sGlobal >= sGlobal) {
//                double t = (sGlobal - prev.sGlobal) / Math.max(1e-6, (cur.sGlobal - prev.sGlobal));
//                Vec2 pos = new Vec2(prev.worldPos.x + (cur.worldPos.x - prev.worldPos.x) * t,
//                        prev.worldPos.z + (cur.worldPos.z - prev.worldPos.z) * t);
//                Vec2 dir = new Vec2(prev.worldDir.x + (cur.worldDir.x - prev.worldDir.x) * t,
//                        prev.worldDir.z + (cur.worldDir.z - prev.worldDir.z) * t).norm();
//                return new Sample(pos, dir, sGlobal);
//            }
//            prev = cur;
//        }
//        return samples.get(samples.size() - 1);
//    }
//}
