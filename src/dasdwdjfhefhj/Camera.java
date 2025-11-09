package dasdwdjfhefhj;

import java.awt.*;
import java.awt.geom.AffineTransform;

import dasdwdjfhefhj.vechicle.tram.TramSection;
import dasdwdjfhefhj.helpers.*;

public class Camera {
    public TramSection attached; // człon do którego przyczepiamy kamerę

    public AffineTransform worldToCamera(Dimension view) {
        AffineTransform at = new AffineTransform();
        // centrum
        at.translate(view.width / 2.0, view.height / 2.0);
        // obrót: tak aby attached heading był poziomo
        at.rotate(-attached.heading);
        // przesunięcie: attached origin na (0,0)
        at.translate(-attached.originXZ.x * Units.M_TO_PX, -attached.originXZ.z * Units.M_TO_PX);
        return at;
    }

    public double scaleForDepth(double zCamPx) {
        // pseudo-perspektywa umiarkowana
        double k = 0.0015; // współczynnik zależny od skali pikselowej
        return 1.0 / (1.0 + k * zCamPx);
    }
}