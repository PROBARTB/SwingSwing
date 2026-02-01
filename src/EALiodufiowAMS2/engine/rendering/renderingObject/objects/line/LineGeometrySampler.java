package EALiodufiowAMS2.engine.rendering.renderingObject.objects.line;

import EALiodufiowAMS2.helpers.*;

public interface LineGeometrySampler<T extends LineGeometry> {

    double length();

    Transform transformAt(double s);

    Vec3 tangentAt(double s);

    Vec3 normalAt(double s);

    Vec3 binormalAt(double s);

    double curvatureAt(double s);

    LineGeometrySample sampleAt(double s);
}
