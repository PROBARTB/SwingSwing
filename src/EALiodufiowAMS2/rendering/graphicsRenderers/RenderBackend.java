package EALiodufiowAMS2.rendering.graphicsRenderers;

import EALiodufiowAMS2.rendering.DrawCommand;
import EALiodufiowAMS2.world.Camera;
import EALiodufiowAMS2.world.scenes.Scene;

import java.awt.image.BufferedImage;

public interface RenderBackend {
    void resize(int width, int height);
    public void setScene(Scene scene);
    void beginFrame(Camera camera);
    void submit(DrawCommand cmd);
    void endFrame();
    BufferedImage getFrameBuffer();
}
