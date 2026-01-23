package EALiodufiowAMS2.engine.rendering.renderViews;

import EALiodufiowAMS2.engine.rendering.graphicsRenderers.GpuBackend;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.awt.AWTGLCanvas;
import org.lwjgl.opengl.awt.GLData;

import java.awt.*;

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