package EALiodufiowAMS2.engine.rendering.renderViews;

import EALiodufiowAMS2.engine.rendering.graphicsRenderers.gpu.GpuBackend;


import javax.swing.*;
import java.awt.*;

public final class GpuRenderView extends JComponent implements RenderView {
    private final GlRenderCanvas canvas;

    public GpuRenderView(GpuBackend backend) {
        setLayout(new BorderLayout());
        this.canvas = new GlRenderCanvas(backend);
        setDoubleBuffered(false);

        add(canvas, BorderLayout.CENTER);

        setBorder(BorderFactory.createLineBorder(Color.GREEN, 3));
    }

    @Override
    public JComponent getComponent() {
        return this;
    }

    @Override
    public void repaintView() {
        canvas.render();
    }

}

