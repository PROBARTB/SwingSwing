package EALiodufiowAMS2.engine.rendering.geometryRenderer;

import EALiodufiowAMS2.engine.rendering.renderingObject.Geometry;

public interface GeometryRenderer<G extends Geometry> {

    Class<G> getSupportedGeometry();

    GeometryDrawData buildDrawData(G geometry);
}
