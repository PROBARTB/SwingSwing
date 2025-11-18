package EALiodufiowAMS2.vehicle;
;
import EALiodufiowAMS2.helpers.Transform;
import EALiodufiowAMS2.helpers.Vec3;

public class Bogie {
    private final double offsetInSection;

    private double posOnTrack;
    private Transform transform;

    // TA KLASA JEST JUŻ NOWA, ALE NIE DOKOŃCZONA

    public Bogie(double offsetInSection, Vec3 size) {
        this.offsetInSection = offsetInSection;
        this.transform = new Transform(null, null, size);
    }

    public double getOffsetInSection() { return offsetInSection; }

    public double getPosOnTrack() { return posOnTrack; }
    public void setPosOnTrack(double positionOnTrack) { this.posOnTrack = positionOnTrack; }

    public Transform getTransform() { return transform; }
    public void setTransform(Transform transform) { this.transform = transform; }
}
