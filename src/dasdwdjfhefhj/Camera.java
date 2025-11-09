package dasdwdjfhefhj;

import java.awt.*;
import java.awt.geom.AffineTransform;

public class Camera {
    public TramSection attached;

    public AffineTransform worldToCamera(TramSection ref, Dimension viewSize) {
        AffineTransform at = new AffineTransform();
        // centrum ekranu na (viewW/2, viewH/2)
        at.translate(viewSize.width / 2.0, viewSize.height / 2.0);
        // obrót tak, aby heading ref był poziomo
        at.rotate(-ref.heading);
        // przesunięcie ref origin na (0,0)
        at.translate(-ref.originXZ.x, -ref.originXZ.z);
        return at;
    }

    // pseudo-perspektywa: skala = f(zCamera)
    public double scaleForDepth(double zCam) {
        // łagodna pseudo-perspektywa; parametryzuj gain
        double k = 0.01;
        return 1.0 / (1.0 + k * zCam);
    }
}