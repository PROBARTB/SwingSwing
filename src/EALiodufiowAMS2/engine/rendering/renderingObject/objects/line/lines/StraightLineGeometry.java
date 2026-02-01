package EALiodufiowAMS2.engine.rendering.renderingObject.objects.line.lines;

import EALiodufiowAMS2.engine.rendering.renderingObject.objects.line.LineGeometry;
import EALiodufiowAMS2.helpers.*;

public final class StraightLineGeometry implements LineGeometry {

    private final Transform start;
    private final Transform end;

    public StraightLineGeometry(Transform start, Transform end) {
        this.start = start;
        this.end = end;
    }

    public Transform getStart() {
        return start;
    }

    public Transform getEnd() {
        return end;
    }

    @Override
    public LineGeometry parallelOffset(double offset) {
        Vec3 p0 = start.getPos();
        Vec3 p1 = end.getPos();
        Vec3 delta = p1.sub(p0);
        double len = delta.length();
        Vec3 direction = (len > 0.0) ? delta.scale(1.0 / len) : new Vec3(1, 0, 0);

        Vec3 up = Math.abs(direction.y) < 0.99 ? new Vec3(0, 1, 0) : new Vec3(1, 0, 0);
        Vec3 binormal = direction.cross(up).normalize();
        Vec3 normal = binormal.cross(direction).normalize();

        Vec3 offsetVec = normal.scale(offset);

        Transform newStart = new Transform(p0.add(offsetVec), start.getRot(), start.getSize());
        Transform newEnd = new Transform(p1.add(offsetVec), end.getRot(), end.getSize());

        return new StraightLineGeometry(newStart, newEnd);
    }
}