package EALiodufiowAMS2.engine.rendering.renderViews;

import EALiodufiowAMS2.engine.rendering.GpuInfo;
import EALiodufiowAMS2.engine.rendering.graphicsRenderers.gpu.GpuBackend;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.awt.AWTGLCanvas;
import org.lwjgl.opengl.awt.GLData;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_VERSION;
import static org.lwjgl.opengl.GL11.glGetString;

public final class GlRenderCanvas extends AWTGLCanvas {
    private final GpuBackend backend;

    public GlRenderCanvas(GpuBackend backend) {
        super(createGLData());
        this.backend = backend;
    }

    private static GLData createGLData() {
        GLData data = new GLData();
        data.majorVersion = 3;
        data.minorVersion = 3;
        data.profile = GLData.Profile.CORE;
        data.samples = 4;
        return data;
    }

    @Override
    public void initGL() {
        GL.createCapabilities();
        backend.setGpuInfo(new GpuInfo(glGetString(GL_RENDERER), glGetString(GL_VENDOR), glGetString(GL_VERSION)));
        backend.init();
    }

    @Override
    public void paintGL() {
        backend.drawPreparedFrame();
        swapBuffers();
    }

    @Override
    public void removeNotify() {
        backend.dispose();
        super.removeNotify();
    }

}