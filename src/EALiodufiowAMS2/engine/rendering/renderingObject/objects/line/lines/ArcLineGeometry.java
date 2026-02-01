package EALiodufiowAMS2.engine.rendering.renderingObject.objects.line.lines;

import EALiodufiowAMS2.engine.rendering.renderingObject.objects.line.LineGeometry;
import EALiodufiowAMS2.helpers.Transform;
import EALiodufiowAMS2.helpers.Vec3;

public final class ArcLineGeometry implements LineGeometry {

    private final Vec3 center;
    private final Vec3 normal; // normalized
    private final double radius;
    private final double startAngleRad;
    private final double sweepAngleRad;
    private final Transform start;
    private final Transform end;

    public ArcLineGeometry(Vec3 center,
                           Vec3 normal,
                           double radius,
                           double startAngleRad,
                           double sweepAngleRad,
                           Transform start,
                           Transform end) {
        if (radius <= 0.0) {
            throw new IllegalArgumentException("Radius must be > 0");
        }
        this.center = center;
        this.normal = normal.normalize();
        this.radius = radius;
        this.startAngleRad = startAngleRad;
        this.sweepAngleRad = sweepAngleRad;
        this.start = start;
        this.end = end;
    }

    public static ArcLineGeometry fromDegrees(Vec3 center,
                                              Vec3 normal,
                                              double radius,
                                              double startAngleDeg,
                                              double sweepAngleDeg,
                                              Transform start,
                                              Transform end) {
        return new ArcLineGeometry(
                center,
                normal,
                radius,
                Math.toRadians(startAngleDeg),
                Math.toRadians(sweepAngleDeg),
                start,
                end
        );
    }

    public static ArcLineGeometry fromArcLength(Vec3 center,
                                                Vec3 normal,
                                                double radius,
                                                double startAngleRad,
                                                double arcLength,
                                                Transform start,
                                                Transform end) {
        double sweep = arcLength / radius;
        return new ArcLineGeometry(center, normal, radius, startAngleRad, sweep, start, end);
    }

    public Vec3 getCenter() {
        return center;
    }

    public Vec3 getNormal() {
        return normal;
    }

    public double getRadius() {
        return radius;
    }

    public double getStartAngleRad() {
        return startAngleRad;
    }

    public double getSweepAngleRad() {
        return sweepAngleRad;
    }

    public Transform getStart() {
        return start;
    }

    public Transform getEnd() {
        return end;
    }

    @Override
    public LineGeometry parallelOffset(double offset) {
        double newRadius = radius + offset;
        if (newRadius <= 0.0) {
            throw new IllegalArgumentException("Resulting radius must be > 0");
        }

        Vec3 n = normal;
        Vec3 arbitrary = Math.abs(n.y) < 0.99 ? new Vec3(0, 1, 0) : new Vec3(1, 0, 0);
        Vec3 tangent0 = arbitrary.cross(n).normalize();
        Vec3 radial0 = n.cross(tangent0).normalize();

        double cosStart = Math.cos(startAngleRad);
        double sinStart = Math.sin(startAngleRad);
        Vec3 radialStart = radial0.scale(cosStart).add(tangent0.scale(sinStart));

        double endAngleRad = startAngleRad + sweepAngleRad;
        double cosEnd = Math.cos(endAngleRad);
        double sinEnd = Math.sin(endAngleRad);
        Vec3 radialEnd = radial0.scale(cosEnd).add(tangent0.scale(sinEnd));

        Vec3 newStartPos = center.add(radialStart.scale(newRadius));
        Vec3 newEndPos = center.add(radialEnd.scale(newRadius));

        Transform newStart = new Transform(newStartPos, start.getRot(), start.getSize());
        Transform newEnd = new Transform(newEndPos, end.getRot(), end.getSize());

        return new ArcLineGeometry(center, normal, newRadius, startAngleRad, sweepAngleRad, newStart, newEnd);
    }
}
