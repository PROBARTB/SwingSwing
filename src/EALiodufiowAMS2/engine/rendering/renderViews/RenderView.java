package EALiodufiowAMS2.engine.rendering.renderViews;

import EALiodufiowAMS2.engine.rendering.RenderingEngineListener;

import javax.swing.*;

public interface RenderView {
    JComponent getComponent();
    void repaintView();
}
