package dasdwdjfhefhj;

import java.awt.Dimension;
import java.awt.*;
import java.awt.geom.AffineTransform;

import dasdwdjfhefhj.vechicle.tram.TramSection;
import dasdwdjfhefhj.helpers.*;

public class Camera {
    public TramSection attached;

    public AffineTransform worldToCamera(Dimension view) {
        AffineTransform at = new AffineTransform();
        at.translate(view.width / 2.0, view.height / 2.0);
        at.rotate(-attached.heading);
        at.translate(-attached.originXZ.x * Units.M_TO_PX, -attached.originXZ.z * Units.M_TO_PX);
        return at;
    }

    public double scaleForDepth(double zCamPx) {
        double k = 0.0015;
        return 1.0 / (1.0 + k * zCamPx);
    }
}