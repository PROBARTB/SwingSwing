package dasdwdjfhefhj.tracks;
import dasdwdjfhefhj.helpers.*;

public class BezierArc implements TrackSegment {
    private final Vec2 P0, P1, P2, P3;
    private final double lengthApprox;
    private TrackSegment next;

    // Konstruktor: start, headingStart, endpoint jako offsetX/Z od startu i exitAngle
    public BezierArc(Vec2 start, double headingStart, double offsetX, double offsetZ, double exitAngle) {
        this.P0 = start;
        this.P3 = start.add(new Vec2(offsetX, offsetZ));

        double d = P0.sub(P3).len();
        double k = d / 3.0;

        Vec2 t0 = Vec2.fromAngle(headingStart).scale(k);
        Vec2 t3 = Vec2.fromAngle(exitAngle).scale(k);

        this.P1 = P0.add(t0);
        this.P2 = P3.sub(t3);

        this.lengthApprox = approximateLength();
    }

    // Konstruktor: łuk kołowy jako Bézier — start, headingStart, długość, signed radius
    public BezierArc(Vec2 start, double headingStart, double length, double signedRadius) {
        int turnSign = signedRadius >= 0 ? +1 : -1;
        double R = Math.abs(signedRadius);
        double dTheta = (length / R) * turnSign;

        Vec2 dir = Vec2.fromAngle(headingStart);
        Vec2 normalLeft = new Vec2(-dir.z, dir.x);
        Vec2 center = turnSign == +1 ? start.add(normalLeft.scale(R)) : start.sub(normalLeft.scale(R));
        double theta0 = Math.atan2(start.z - center.z, start.x - center.x);
        double theta1 = theta0 + dTheta;

        Vec2 end = new Vec2(center.x + R * Math.cos(theta1), center.z + R * Math.sin(theta1));
        double exitAngle = headingStart + dTheta;

        this.P0 = start;
        this.P3 = end;
        double d = P0.sub(P3).len();
        double k = d / 3.0;
        Vec2 t0 = Vec2.fromAngle(headingStart).scale(k);
        Vec2 t3 = Vec2.fromAngle(exitAngle).scale(k);
        this.P1 = P0.add(t0);
        this.P2 = P3.sub(t3);

        this.lengthApprox = approximateLength();
    }

    public double getLength() { return lengthApprox; }

    public Vec2 posAt(double s) {
        double t = clamp01(s / lengthApprox);
        return bezierPoint(t);
    }

    public Vec2 tangentAt(double s) {
        double t = clamp01(s / lengthApprox);
        Vec2 d = bezierDerivative(t);
        return d.norm();
    }

    public TrackSegment getNext() { return next; }
    public void setNext(TrackSegment next) { this.next = next; }

    public double exitHeading() {
        Vec2 d = bezierDerivative(1.0).norm();
        return Math.atan2(d.z, d.x);
    }

    public Vec2 getEnd() { return P3; }

    private Vec2 bezierPoint(double t) {
        double u = 1 - t;
        double b0 = u*u*u, b1 = 3*u*u*t, b2 = 3*u*t*t, b3 = t*t*t;
        double x = b0*P0.x + b1*P1.x + b2*P2.x + b3*P3.x;
        double z = b0*P0.z + b1*P1.z + b2*P2.z + b3*P3.z;
        return new Vec2(x, z);
    }

    private Vec2 bezierDerivative(double t) {
        double u = 1 - t;
        Vec2 term1 = P1.sub(P0).scale(3*u*u);
        Vec2 term2 = P2.sub(P1).scale(6*u*t);
        Vec2 term3 = P3.sub(P2).scale(3*t*t);
        return term1.add(term2).add(term3);
    }

    private double approximateLength() {
        double L = 0; Vec2 prev = P0; int N = 96;
        for (int i = 1; i <= N; i++) {
            double t = i / (double) N;
            Vec2 p = bezierPoint(t);
            L += p.sub(prev).len();
            prev = p;
        }
        return L;
    }

    private double clamp01(double t) { return t < 0 ? 0 : (t > 1 ? 1 : t); }
}
