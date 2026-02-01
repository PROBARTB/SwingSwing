package EALiodufiowAMS2.engine.rendering.renderingObject.objects.line;

import EALiodufiowAMS2.engine.rendering.renderingObject.Geometry;

public interface LineGeometry extends Geometry {
    LineGeometry parallelOffset(double offset);
}
