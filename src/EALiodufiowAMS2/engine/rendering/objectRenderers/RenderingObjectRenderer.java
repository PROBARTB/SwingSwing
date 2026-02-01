package EALiodufiowAMS2.engine.rendering.objectRenderers;


import EALiodufiowAMS2.engine.rendering.DrawCommand;
import EALiodufiowAMS2.engine.rendering.graphicsRenderers.RenderBackend;
import EALiodufiowAMS2.engine.rendering.renderingObject.RenderingObject;

import java.util.List;

public interface RenderingObjectRenderer {

    Class<? extends RenderingObject> getSupportedType();

    void buildDrawCommands(RenderBackend backend,
                           RenderingObject object,
                           List<DrawCommand> outCommands);
}
