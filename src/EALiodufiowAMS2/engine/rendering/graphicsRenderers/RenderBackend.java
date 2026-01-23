package EALiodufiowAMS2.engine.rendering.graphicsRenderers;

import EALiodufiowAMS2.engine.rendering.DrawCommand;
import EALiodufiowAMS2.general.Camera;
import EALiodufiowAMS2.general.Scene;

import java.awt.image.BufferedImage;

public interface RenderBackend {
    void resize(int width, int height);
    public void setScene(Scene scene);
    void beginFrame(Camera camera);
    void submit(DrawCommand cmd);
    void endFrame();
    BufferedImage getFrameBuffer();
}
