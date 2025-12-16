package EALiodufiowAMS2.tracks;

import EALiodufiowAMS2.helpers.*;

public class CurvedSegment extends TrackSegment {
    private Vec3 p0, p1, p2, p3; // Punkty kontrolne Beziera

    // Konstruktor 1: Promień i Długość (Aproksymacja łuku)
    public CurvedSegment(String id, double radius, double arcLength, boolean turnRight) {
        super(id);
        this.length = arcLength;

        double angle = arcLength / radius;
        // Uproszczone punkty kontrolne dla łuku 90st (dla mniejszych kątów wymaga skalowania)
        // Zakładamy start w (0,0,0) kierunek Z
        double handleDist = radius * 0.55228 * (angle / (Math.PI/2));

        this.p0 = new Vec3(0,0,0);
        this.p1 = new Vec3(0,0, handleDist); // Styczna startowa

        double endX = radius * (1 - Math.cos(angle)); // Jeśli obrót wokół osi
        double endZ = radius * Math.sin(angle);

        // Uwaga: W 3D matematyka obrotu zależy od układu współrzędnych
        // Tu zakładamy uproszczony model płaski XZ
        endX = turnRight ? endX : -endX;

        this.p3 = new Vec3(endX, 0, endZ);
        // P2 trzeba wyliczyć na podstawie rotacji końcowej
        this.p2 = new Vec3(endX, 0, endZ - handleDist); // Bardzo uproszczone!
    }

    // Konstruktor 2: Punkt końcowy w świecie (lokalnie względem startu)
    public CurvedSegment(String id, Transform localEndPoint) {
        super(id);
        this.p0 = new Vec3(0,0,0);
        this.p1 = new Vec3(0,0, localEndPoint.getPos().z * 0.5); // Heurystyka: control point w połowie drogi w osi Z

        this.p3 = localEndPoint.getPos();
        // P2 wyliczamy cofając się od celu zgodnie z jego rotacją
        Vec3 backwardDir = localEndPoint.getRot().rotate(new Vec3(0,0,-1));
        this.p2 = p3.add(backwardDir.scale(localEndPoint.getPos().z * 0.5));

        // Estymacja długości (bardzo zgrubna, w produkcji całkujemy)
        this.length = p0.add(p3).scale(0.5).z; // TODO: Prawdziwa długość łuku
    }

    @Override
    public Transform getLocalTransform(double distance) {
        // UPROSZCZONE: t liniowe (powoduje zmienną prędkość pociągu)
        double t = distance / length;
        if(t < 0) t = 0; if(t > 1) t = 1;

        // Cubic Bezier: (1-t)^3*P0 + 3(1-t)^2*t*P1 + 3(1-t)t^2*P2 + t^3*P3
        double u = 1 - t;
        double tt = t * t;
        double uu = u * u;
        double uuu = uu * u;
        double ttt = tt * t;

        Vec3 pos = p0.scale(uuu)
                .add(p1.scale(3 * uu * t))
                .add(p2.scale(3 * u * tt))
                .add(p3.scale(ttt));

        // Rotacja: LookAt na pochodną (styczną)
        // Tangent = 3(1-t)^2(P1-P0) + 6(1-t)t(P2-P1) + 3t^2(P3-P2)
        // Tutaj dla uproszczenia zwracam Identity lub wyliczoną
        return new Transform(pos, Quaternion.identity());
    }
}