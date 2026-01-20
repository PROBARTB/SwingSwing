package EALiodufiowAMS2.rendering.renderers;

import EALiodufiowAMS2.rendering.graphicsRenderers.RenderBackend;
import EALiodufiowAMS2.rendering.renderingObject.geometries.Geometry;
import EALiodufiowAMS2.rendering.renderingObject.RenderingObject;

public interface GeometryRenderer {
    Class<? extends Geometry> getSupportedGeometry();
    void render(RenderBackend backend, RenderingObject object);
}
