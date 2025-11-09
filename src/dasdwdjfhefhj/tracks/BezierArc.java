package dasdwdjfhefhj.tracks;
import dasdwdjfhefhj.helpers.*;

public class BezierArc implements TrackSegment {
    private final Vec2 P0, P1, P2, P3;
    private final double lengthApprox; // przybliżona długość (sumowanie odcinków)
    private TrackSegment next;

    // Konstruktor 1: początek, headingStart, endpoint (offsetX/offsetZ względem początku) i exitAngle (rad)
    public BezierArc(Vec2 start, double headingStart, double offsetX, double offsetZ, double exitAngle) {
        this.P0 = start;
        this.P3 = start.add(new Vec2(offsetX, offsetZ));

        double d = P0.sub(P3).len();
        double k = d / 3.0; // długość „ciągnięcia” kontrolnych

        Vec2 t0 = Vec2.fromAngle(headingStart).scale(k);
        Vec2 t3 = Vec2.fromAngle(exitAngle).scale(k);

        this.P1 = P0.add(t0);
        this.P2 = P3.sub(t3);

        this.lengthApprox = approximateLength();
    }

    // Konstruktor 2: początek, headingStart, długość łuku, promień (signed), wylicza P3 i exitAngle
    public BezierArc(Vec2 start, double headingStart, double length, double signedRadius) {
        // Wyznacz łuk kołowy: środek, dTheta, exitHeading, końcowy punkt
        int turnSign = signedRadius >= 0 ? +1 : -1;
        double R = Math.abs(signedRadius);
        double dTheta = (length / R) * turnSign;

        Vec2 dir = Vec2.fromAngle(headingStart);
        Vec2 normalLeft = new Vec2(-dir.z, dir.x);
        Vec2 center = turnSign == +1 ? start.add(normalLeft.scale(R)) : start.sub(normalLeft.scale(R));
        // kąt polarny startu
        double theta0 = Math.atan2(start.z - center.z, start.x - center.x);
        double theta1 = theta0 + dTheta;

        Vec2 end = new Vec2(center.x + R * Math.cos(theta1), center.z + R * Math.sin(theta1));
        double exitAngle = headingStart + dTheta; // styczna obraca się o tyle, co kąt łuku

        // Teraz zbudujmy równoważną krzywą Béziera dla spójności API, jak w konstruktorze 1:
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
        // zwróć jednostkową styczną
        return d.norm();
    }

    public TrackSegment getNext() { return next; }
    public void setNext(TrackSegment next) { this.next = next; }

    public double exitHeading() {
        Vec2 d = bezierDerivative(1.0).norm();
        return Math.atan2(d.z, d.x);
    }

    // --- Bézier core ---
    private Vec2 bezierPoint(double t) {
        double u = 1 - t;
        double b0 = u*u*u;
        double b1 = 3*u*u*t;
        double b2 = 3*u*t*t;
        double b3 = t*t*t;
        double x = b0*P0.x + b1*P1.x + b2*P2.x + b3*P3.x;
        double z = b0*P0.z + b1*P1.z + b2*P2.z + b3*P3.z;
        return new Vec2(x, z);
    }

    private Vec2 bezierDerivative(double t) {
        double u = 1 - t;
        // dB/dt = 3*u*u*(P1-P0) + 6*u*t*(P2-P1) + 3*t*t*(P3-P2)
        Vec2 term1 = P1.sub(P0).scale(3*u*u);
        Vec2 term2 = P2.sub(P1).scale(6*u*t);
        Vec2 term3 = P3.sub(P2).scale(3*t*t);
        return term1.add(term2).add(term3);
    }

    private double approximateLength() {
        // adaptacyjne próbkowanie (tu stałe): 64 odcinki
        double L = 0;
        Vec2 prev = P0;
        int N = 64;
        for (int i = 1; i <= N; i++) {
            double t = i / (double) N;
            Vec2 p = bezierPoint(t);
            L += p.sub(prev).len();
            prev = p;
        }
        return L;
    }

    private double clamp01(double t) { return t < 0 ? 0 : (t > 1 ? 1 : t); }

    public Vec2 getStart() { return P0; }
    public Vec2 getEnd() { return P3; }
}
