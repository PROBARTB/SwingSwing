package dasdwdjfhefhj;

public class ArcSegment implements TrackSegment {
    private final double length;
    private final Vec2 start;
    private final double heading0; // kąt wejścia w łuk
    private final Vec2 center;     // środek okręgu
    private final double R;        // promień > 0
    private final int turnSign;    // +1 lewo, -1 prawo
    private final double theta0;   // kąt polarny punktu start względem center
    private final double dTheta;   // przyrost kąta (signed)

    // Konstruktor A: length, offsetX/Z (środek), kąt końcowy (opcjonalny)
    public ArcSegment(Vec2 start, double heading0, double length, double offsetX, double offsetZ, Double desiredExitHeading) {
        this.start = start; this.heading0 = heading0; this.length = length;
        this.center = new Vec2(start.x + offsetX, start.z + offsetZ);
        this.R = start.sub(center).len();
        // Wyznacz znak skrętu: jeśli wektor (center->start) jest obrócony względem heading0 w lewo → +1
        Vec2 cs = start.sub(center);
        double theta0Raw = Math.atan2(cs.z, cs.x);
        this.theta0 = theta0Raw;
        // Kierunek jazdy po okręgu: styczna do okręgu w start odpowiada heading0.
        // Jeśli (heading0 - theta0 - π/2) ~ 0 → lewo; jeśli ~ -π/2 → prawo. Prościej: sprawdź znak cross( radial, tangent).
        Vec2 radial = cs.norm();
        Vec2 tangentLeft = new Vec2(-radial.z, radial.x); // lewoskrętna styczna
        double dotT = tangentLeft.dot(Vec2.fromAngle(heading0));
        this.turnSign = dotT >= 0 ? +1 : -1;

        this.dTheta = (length / R) * turnSign;
        // Jeżeli desiredExitHeading != null, można skorygować turnSign tak, by heading końca zbliżał się do desiredExitHeading
        // (opcjonalne dopasowanie).
    }

    // Konstruktor B: length, radius (signed for turn)
    public ArcSegment(Vec2 start, double heading0, double length, double signedRadius) {
        this.start = start; this.heading0 = heading0; this.length = length;
        this.turnSign = signedRadius >= 0 ? +1 : -1;
        this.R = Math.abs(signedRadius);
        // center z geometrii: środek leży w kierunku prostopadłym do heading0 o odległości R
        Vec2 dir = Vec2.fromAngle(heading0);
        Vec2 normalLeft = new Vec2(-dir.z, dir.x);
        this.center = turnSign == +1 ? start.add(normalLeft.scale(R)) : start.sub(normalLeft.scale(R));
        Vec2 cs = start.sub(center);
        this.theta0 = Math.atan2(cs.z, cs.x);
        this.dTheta = (length / R) * turnSign;
    }

    public double getLength() { return length; }

    public Vec2 posAt(double s) {
        double t = s / length;                // parametr łuku
        double theta = theta0 + dTheta * t;   // pozycja kątowa
        return new Vec2(center.x + R * Math.cos(theta),
                center.z + R * Math.sin(theta));
    }

    public Vec2 tangentAt(double s) {
        double t = s / length;
        double theta = theta0 + dTheta * t;
        // styczna = +/- rotacja radialnej o ±90°; skala |d s/dθ| = R
        Vec2 radial = new Vec2(Math.cos(theta), Math.sin(theta));
        Vec2 tangentLeft = new Vec2(-radial.z, radial.x).scale(R);
        return (turnSign == +1) ? tangentLeft : tangentLeft.scale(-1);
    }

    public double curvatureAt(double s) { return 1.0 / R * turnSign; }

    public TrackSegment getNext(Vec2 choiceHint) { return next; }
    private TrackSegment next;
    public void setNext(TrackSegment next) { this.next = next; }
}

