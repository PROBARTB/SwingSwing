package EALiodufiowAMS2.engine.rendering.renderingObject.objects.line.lines;

import EALiodufiowAMS2.engine.rendering.renderingObject.objects.line.LineGeometrySampler;
import EALiodufiowAMS2.engine.rendering.renderingObject.objects.line.LineGeometrySample;
import EALiodufiowAMS2.helpers.*;

public final class ArcLineGeometrySampler implements LineGeometrySampler<ArcLineGeometry> {

    private final ArcLineGeometry geometry;
    private final double length;

    public ArcLineGeometrySampler(ArcLineGeometry geometry) {
        this.geometry = geometry;
        this.length = Math.abs(geometry.getRadius() * geometry.getSweepAngleRad());
    }

    @Override
    public double length() {
        return length;
    }

    private double angleAt(double s) {
        double t = (length == 0.0) ? 0.0 : (s / length);
        return geometry.getStartAngleRad() + geometry.getSweepAngleRad() * t;
    }

    private Vec3 radialAtAngle(double angle) {
        Vec3 n = geometry.getNormal();
        Vec3 arbitrary = Math.abs(n.y) < 0.99 ? new Vec3(0, 1, 0) : new Vec3(1, 0, 0);
        Vec3 tangent0 = arbitrary.cross(n).normalize();
        Vec3 radial0 = n.cross(tangent0).normalize();

        double cos = Math.cos(angle);
        double sin = Math.sin(angle);

        return radial0.scale(cos).add(tangent0.scale(sin));
    }

    @Override
    public Transform transformAt(double s) {
        double t = (length == 0.0) ? 0.0 : (s / length);
        double angle = angleAt(s);

        Vec3 radial = radialAtAngle(angle);
        Vec3 pos = geometry.getCenter().add(radial.scale(geometry.getRadius()));

        Transform start = geometry.getStart();
        Transform end = geometry.getEnd();

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
        double angle = angleAt(s);
        Vec3 n = geometry.getNormal();
        Vec3 radial = radialAtAngle(angle);
        return n.cross(radial).normalize();
    }

    @Override
    public Vec3 normalAt(double s) {
        double angle = angleAt(s);
        Vec3 radial = radialAtAngle(angle);
        return radial.scale(-1.0).normalize();
    }

    @Override
    public Vec3 binormalAt(double s) {
        Vec3 t = tangentAt(s);
        Vec3 n = normalAt(s);
        return t.cross(n).normalize();
    }

    @Override
    public double curvatureAt(double s) {
        return 1.0 / geometry.getRadius();
    }

    @Override
    public LineGeometrySample sampleAt(double s) {
        Transform tr = transformAt(s);
        Vec3 t = tangentAt(s);
        Vec3 n = normalAt(s);
        Vec3 b = binormalAt(s);
        double k = curvatureAt(s);
        return new LineGeometrySample(tr, t, n, b, k);
    }
}
