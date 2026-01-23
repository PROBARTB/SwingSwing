package EALiodufiowAMS2.engine.rendering.renderers;

import EALiodufiowAMS2.engine.rendering.graphicsRenderers.RenderBackend;
import EALiodufiowAMS2.engine.rendering.renderingObject.geometries.Geometry;
import EALiodufiowAMS2.engine.rendering.renderingObject.RenderingObject;

public interface GeometryRenderer {
    Class<? extends Geometry> getSupportedGeometry();
    void render(RenderBackend backend, RenderingObject object);
}
