package EALiodufiowAMS2.game.vehicle;

import EALiodufiowAMS2.helpers.Transform;
import EALiodufiowAMS2.helpers.Vec3;

import java.awt.image.BufferedImage;

public class Joint {
    private final Transform transform;
    private final double maxBendRadians;
    private BufferedImage texture;

    public Joint(Vec3 size, double maxBendRadians, BufferedImage texture) {
        this.transform = new Transform(null, null, size);
        this.maxBendRadians = maxBendRadians;
    }

    public double getMaxBendRadians() { return maxBendRadians; }

    public Transform getTransform() { return transform; }

    public BufferedImage getTexture() { return texture; }
    public void setTexture(BufferedImage texture) { this.texture = texture; }

    public double clampAngleDifference(double baseAngle, double targetAngle) {
        double diff = normalize(targetAngle - baseAngle);
        if (diff > maxBendRadians) diff = maxBendRadians;
        if (diff < -maxBendRadians) diff = -maxBendRadians;
        return normalize(baseAngle + diff);
    }

    private double normalize(double a) {
        while (a > Math.PI) a -= 2*Math.PI;
        while (a < -Math.PI) a += 2*Math.PI;
        return a;
    }
}
