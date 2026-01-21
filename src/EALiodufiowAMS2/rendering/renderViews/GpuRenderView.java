package EALiodufiowAMS2.rendering.renderViews;

import EALiodufiowAMS2.rendering.graphicsRenderers.GpuBackend;


import javax.swing.*;
import java.awt.*;

public final class GpuRenderView extends JComponent implements RenderView {
    private final GlRenderCanvas canvas;

    public GpuRenderView(GpuBackend backend) {
        setLayout(new BorderLayout());
        this.canvas = new GlRenderCanvas(backend);
        add(canvas, BorderLayout.CENTER);
        setOpaque(false); // pozwala na overlay w ScenePanelu

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

