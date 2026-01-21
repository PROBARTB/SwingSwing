package EALiodufiowAMS2.rendering.renderViews;

import EALiodufiowAMS2.rendering.graphicsRenderers.GpuBackend;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.awt.AWTGLCanvas;
import org.lwjgl.opengl.awt.GLData;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;

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
//@Override
//public void paintGL() {
//        System.out.println("PAINT GL");
//    GL.createCapabilities(); // tymczasowo, jeśli coś jest nie tak z initGL
//    glBindFramebuffer(GL_FRAMEBUFFER, 0);
//    glViewport(0, 0, getWidth(), getHeight());
//    glClearColor(1f, 0f, 1f, 1f); // magenta
//    glClear(GL_COLOR_BUFFER_BIT);
//    swapBuffers();
//}


//    @Override
//    public void reshape(int x, int y, int width, int height) {
//        backend.resize(width, height);
//    }

    @Override
    public void removeNotify() {
        backend.dispose();
        super.removeNotify();
    }

}