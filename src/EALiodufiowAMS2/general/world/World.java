package EALiodufiowAMS2.general.world;

import EALiodufiowAMS2.engine.rendering.renderingObject.RenderingObject;

import java.util.List;

public interface World {
    void update(double dt);
    List<RenderingObject> getRenderingObjects();
}
