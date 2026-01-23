package EALiodufiowAMS2.engine.rendering.renderViews;

import EALiodufiowAMS2.engine.rendering.graphicsRenderers.RenderBackend;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public final class CpuRenderView extends JComponent implements RenderView {
    private final RenderBackend backend;

    public CpuRenderView(RenderBackend backend) {
        this.backend = backend;
        setDoubleBuffered(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        BufferedImage frame = backend.getFrameBuffer();
        if (frame == null) return;

        g.drawImage(frame, 0, 0, getWidth(), getHeight(), null);
    }

    @Override
    public JComponent getComponent() {
        return this;
    }

    @Override
    public void repaintView() {
        repaint();
    }
}
