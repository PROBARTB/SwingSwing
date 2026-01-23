package EALiodufiowAMS2.game.vehicle;

import EALiodufiowAMS2.helpers.Transform;
import EALiodufiowAMS2.helpers.Vec3;

import java.awt.image.BufferedImage;

public class Bogie {
    private final double offsetInSection;

    private double posOnTrack;
    private Transform transform;
    private BufferedImage texture;

    public Bogie(double offsetInSection, Vec3 size, BufferedImage texture) {
        this.offsetInSection = offsetInSection;
        this.transform = new Transform(null, null, size);
        this.texture = texture;
    }

    public double getOffsetInSection() { return offsetInSection; }

    public double getPosOnTrack() { return posOnTrack; }
    public void setPosOnTrack(double positionOnTrack) { this.posOnTrack = positionOnTrack; }

    public Transform getTransform() { return transform; }
    public void setTransform(Transform transform) { this.transform = transform; }

    public BufferedImage getTexture() { return texture; }
    public void setTexture(BufferedImage texture) { this.texture = texture; }
}
