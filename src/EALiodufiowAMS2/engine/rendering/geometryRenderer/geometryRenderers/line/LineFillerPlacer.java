package EALiodufiowAMS2.engine.rendering.geometryRenderer.geometryRenderers.line;

import EALiodufiowAMS2.engine.rendering.DrawCommand;
import EALiodufiowAMS2.engine.rendering.graphicsRenderers.RenderBackend;
import EALiodufiowAMS2.engine.rendering.objectRenderers.DefaultRenderingObjectRenderer;
import EALiodufiowAMS2.engine.rendering.renderingObject.RenderingObject;
import EALiodufiowAMS2.engine.rendering.renderingObject.objects.line.Line;
import EALiodufiowAMS2.engine.rendering.renderingObject.objects.line.LineGeometry;
import EALiodufiowAMS2.helpers.Vec3;

import java.util.List;

public interface LineFillerPlacer {

    void placeFiller(Line<?> line,
                     List<Vec3> polylineWorld,
                     double lengthWorld,
                     RenderingObject filler,
                     DefaultRenderingObjectRenderer defaultRenderer,
                     List<DrawCommand> outCommands);
}
