package EALiodufiowAMS2.game.builders;

import EALiodufiowAMS2.helpers.Transform;
import EALiodufiowAMS2.engine.rendering.renderingObject.RenderingObject;
import EALiodufiowAMS2.general.Camera;

public interface Builder {
    void update(double deltaTime);
    java.util.List<String> getObjectIds();
    Transform getObjectTransform(String id);

    default boolean isVisible(Camera camera, String id) {
        throw new UnsupportedOperationException("Visibility test not implemented");
    }

    RenderingObject buildRenderingObject(String id);
}

