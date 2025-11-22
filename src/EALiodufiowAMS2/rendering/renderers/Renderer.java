package EALiodufiowAMS2.rendering.renderers;

import EALiodufiowAMS2.helpers.Transform;
import EALiodufiowAMS2.rendering.renderingObjects.RenderingObject;
import EALiodufiowAMS2.world.Camera;

public interface Renderer {
    void update(double deltaTime);
    java.util.List<String> getObjectIds();
    Transform getObjectTransform(String id);

    default boolean isVisible(Camera camera, String id) {
        throw new UnsupportedOperationException("Visibility test not implemented");
    }

    RenderingObject buildRenderingObject(String id);
}

