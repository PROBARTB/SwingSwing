package EALiodufiowAMS2.engine.rendering.renderingObject.objects.line;

import EALiodufiowAMS2.engine.rendering.renderingObject.RenderingObject;
import EALiodufiowAMS2.helpers.*;

import java.awt.*;

public final class Line<T extends LineGeometry> extends RenderingObject {

    private final T geometry;
    private final RenderingObject filler;
    private final FillMode fillMode;
    private final Color lineColor;

    public Line(T geometry,
                RenderingObject filler,
                Transform transform,
                FillMode fillMode,
                Color lineColor) {
        super(geometry, transform);
        this.geometry = geometry;
        this.filler = filler;
        this.fillMode = fillMode;
        this.lineColor = lineColor;
    }

    @Override
    public T getGeometry() {
        return geometry;
    }

    public T getLineGeometry() {
        return geometry;
    }

    public RenderingObject getFiller() {
        return filler;
    }

    public FillMode getFillMode() {
        return fillMode;
    }

    public Color getLineColor() {
        return lineColor;
    }
}
