package EALiodufiowAMS2.engine.uiRendering;

import javax.swing.*;
import java.util.Objects;

public final class UiOverlayElement {

    private final JComponent component;
    private UiOverlayConstraints constraints;

    JWindow window;

    private UiOverlayLayer layer;

    public UiOverlayElement(JComponent component, UiOverlayConstraints constraints) {
        this.component = Objects.requireNonNull(component, "component");
        this.constraints = Objects.requireNonNull(constraints, "constraints");
    }

    public JComponent getComponent() {
        return component;
    }

    public UiOverlayConstraints getConstraints() {
        return constraints;
    }

    public void setConstraints(UiOverlayConstraints constraints) {
        this.constraints = Objects.requireNonNull(constraints, "constraints");
    }

    void setLayer(UiOverlayLayer layer) { this.layer = layer; }
    public UiOverlayLayer getLayer() {  return layer; }

}
