package EALiodufiowAMS2.engine.rendering.renderingObject.objects.line.lines;

import EALiodufiowAMS2.engine.rendering.renderingObject.objects.line.LineGeometry;
import EALiodufiowAMS2.helpers.Transform;

public final class PolyLineGeometry implements LineGeometry {

    private final Transform[] points;
    private final double offset;

    public PolyLineGeometry(Transform[] points) {
        this(points, 0.0);
    }

    public PolyLineGeometry(Transform[] points, double offset) {
        if (points == null || points.length < 2) {
            throw new IllegalArgumentException("PolyLineGeometry requires at least 2 points.");
        }
        this.points = points.clone();
        this.offset = offset;
    }

    public int getPointCount() {
        return points.length;
    }

    public Transform getPoint(int index) {
        return points[index];
    }

    public Transform[] getPoints() {
        return points.clone();
    }

    public double getOffset() {
        return offset;
    }

    @Override
    public LineGeometry parallelOffset(double offset) {
        return new PolyLineGeometry(points, offset);
    }
}
