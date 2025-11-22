//package EALiodufiowAMS2.tracks;
//
//import EALiodufiowAMS2.helpers.Vec2;
//
///**
// * CurvedSegment: krzywa Béziera kubiczna w lokalnym układzie (P0=(0,0), heading=0).
// * Dwa sposoby konstrukcji:
// * - radius+arcLength (łuk kołowy aproksymowany Bézierem)
// * - offsetX, offsetZ, deltaAngle (koniec i wyjściowy kąt względem początku)
// */
//public class CurvedSegment implements TrackSegment {
//    private final String id;
//    private final Vec2 P0, P1, P2, P3;
//    private final double lengthApprox;
//
//    // Łuk kołowy (signed radius: znak kierunku skrętu), arcLength w metrach.
//    public CurvedSegment(String id, double radiusMeters, double arcLengthMeters) {
//        this.id = id;
//        int turnSign = radiusMeters >= 0 ? +1 : -1;
//        double R = Math.abs(radiusMeters);
//        double dTheta = (arcLengthMeters / R) * turnSign;
//
//        Vec2 start = new Vec2(0,0);
//        Vec2 center = turnSign == +1 ? start.add(new Vec2(0, R)) : start.sub(new Vec2(0, R));
//        double theta0 = Math.atan2(start.z - center.z, start.x - center.x);
//        double theta1 = theta0 + dTheta;
//
//        Vec2 end = new Vec2(center.x + R * Math.cos(theta1), center.z + R * Math.sin(theta1));
//        double exitAngle = dTheta;
//
//        this.P0 = start;
//        this.P3 = end;
//
//        double d = P0.sub(P3).len();
//        double k = d / 3.0;
//        Vec2 t0 = Vec2.fromAngle(0.0).scale(k);
//        Vec2 t3 = Vec2.fromAngle(exitAngle).scale(k);
//        this.P1 = P0.add(t0);
//        this.P2 = P3.sub(t3);
//
//        this.lengthApprox = approximateLength();
//    }
//
//    // Offset końca i deltaAngle (wyjściowy kąt względem startowego)
//    public CurvedSegment(String id, double offsetX, double offsetZ, double deltaAngle) {
//        this.id = id;
//        this.P0 = new Vec2(0,0);
//        this.P3 = new Vec2(offsetX, offsetZ);
//
//        double d = P0.sub(P3).len();
//        double k = d / 3.0;
//        Vec2 t0 = Vec2.fromAngle(0.0).scale(k);
//        Vec2 t3 = Vec2.fromAngle(deltaAngle).scale(k);
//        this.P1 = P0.add(t0);
//        this.P2 = P3.sub(t3);
//
//        this.lengthApprox = approximateLength();
//    }
//
//    public String getId() { return id; }
//
//    @Override public double getLength() { return lengthApprox; }
//
//    @Override
//    public Vec2 localPosAt(double sMeters) {
//        double t = clamp01(sMeters / lengthApprox);
//        return bezierPoint(t);
//    }
//
//    @Override
//    public Vec2 localTangentAt(double sMeters) {
//        double t = clamp01(sMeters / lengthApprox);
//        return bezierDerivative(t).norm();
//    }
//
//    private Vec2 bezierPoint(double t) {
//        double u = 1 - t;
//        double b0 = u*u*u, b1 = 3*u*u*t, b2 = 3*u*t*t, b3 = t*t*t;
//        double x = b0*P0.x + b1*P1.x + b2*P2.x + b3*P3.x;
//        double z = b0*P0.z + b1*P1.z + b2*P2.z + b3*P3.z;
//        return new Vec2(x, z);
//    }
//
//    private Vec2 bezierDerivative(double t) {
//        double u = 1 - t;
//        Vec2 term1 = P1.sub(P0).scale(3*u*u);
//        Vec2 term2 = P2.sub(P1).scale(6*u*t);
//        Vec2 term3 = P3.sub(P2).scale(3*t*t);
//        return term1.add(term2).add(term3);
//    }
//
//    private double approximateLength() {
//        double L = 0; Vec2 prev = P0; int N = 96;
//        for (int i = 1; i <= N; i++) {
//            double tt = i / (double) N;
//            Vec2 p = bezierPoint(tt);
//            L += p.sub(prev).len();
//            prev = p;
//        }
//        return L;
//    }
//
//    private static double clamp01(double t) { return t < 0 ? 0 : (t > 1 ? 1 : t); }
//}
