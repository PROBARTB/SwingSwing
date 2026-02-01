package EALiodufiowAMS2.engine.rendering.renderingObject.objects.line.lines;

import EALiodufiowAMS2.engine.rendering.renderingObject.objects.line.LineGeometrySampler;
import EALiodufiowAMS2.engine.rendering.renderingObject.objects.line.LineGeometrySample;
import EALiodufiowAMS2.helpers.*;

public final class StraightLineGeometrySampler implements LineGeometrySampler<StraightLineGeometry> {

    private final StraightLineGeometry geometry;
    private final double length;
    private final Vec3 direction; // unit
    private final Vec3 normal;
    private final Vec3 binormal;

    public StraightLineGeometrySampler(StraightLineGeometry geometry) {
        this.geometry = geometry;
        Vec3 p0 = geometry.getStart().getPos();
        Vec3 p1 = geometry.getEnd().getPos();
        Vec3 delta = p1.sub(p0);
        double len = delta.length();
        this.length = len;
        this.direction = (len > 0.0) ? delta.scale(1.0 / len) : new Vec3(1, 0, 0);

        Vec3 up = Math.abs(direction.y) < 0.99 ? new Vec3(0, 1, 0) : new Vec3(1, 0, 0);
        this.binormal = direction.cross(up).normalize();
        this.normal = binormal.cross(direction).normalize();
    }

    @Override
    public double length() {
        return length;
    }

    @Override
    public Transform transformAt(double s) {
        double t = (length == 0.0) ? 0.0 : (s / length);
        Transform start = geometry.getStart();
        Transform end = geometry.getEnd();

        Vec3 p0 = start.getPos();
        Vec3 p1 = end.getPos();
        Vec3 pos = p0.lerp(p1, t);

        Quaternion r0 = start.getRot();
        Quaternion r1 = end.getRot();
        Quaternion rot = Quaternion.slerp(r0, r1, t);

        Vec3 s0 = start.getSize();
        Vec3 s1 = end.getSize();
        Vec3 size = s0.lerp(s1, t);

        return new Transform(pos, rot, size);
    }

    @Override
    public Vec3 tangentAt(double s) {
        return direction;
    }

    @Override
    public Vec3 normalAt(double s) {
        return normal;
    }

    @Override
    public Vec3 binormalAt(double s) {
        return binormal;
    }

    @Override
    public double curvatureAt(double s) {
        return 0.0;
    }

    @Override
    public LineGeometrySample sampleAt(double s) {
        Transform tr = transformAt(s);
        return new LineGeometrySample(tr, tangentAt(s), normalAt(s), binormalAt(s), curvatureAt(s));
    }
}
