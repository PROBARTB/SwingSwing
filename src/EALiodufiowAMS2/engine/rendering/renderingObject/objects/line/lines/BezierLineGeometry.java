package EALiodufiowAMS2.engine.rendering.renderingObject.objects.line.lines;

import EALiodufiowAMS2.engine.rendering.renderingObject.objects.line.LineGeometry;
import EALiodufiowAMS2.helpers.*;

public final class BezierLineGeometry implements LineGeometry {

    private final Vec3 p0; // start position
    private final Vec3 p1; // handle 1
    private final Vec3 p2; // handle 2
    private final Vec3 p3; // end position

    // parallel to Normal
    private final double offset;

    public BezierLineGeometry(Vec3 p0, Vec3 p1, Vec3 p2, Vec3 p3) {
        this(p0, p1, p2, p3, 0.0);
    }

    public BezierLineGeometry(Vec3 p0, Vec3 p1, Vec3 p2, Vec3 p3, double offset) {
        this.p0 = p0;
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
        this.offset = offset;
    }

    public Vec3 getP0() { return p0; }
    public Vec3 getP1() { return p1; }
    public Vec3 getP2() { return p2; }
    public Vec3 getP3() { return p3; }

    public double getOffset() { return offset; }

    public BezierLineGeometry(Transform start, Transform end, double startHandle, double endHandle) {
        this(start, end, startHandle, endHandle, 0.0);
    }

    public BezierLineGeometry(
            Transform start,
            Transform end,
            double startHandle,
            double endHandle,
            double offset
    ) {
        this.p0 = start.getPos();
        this.p3 = end.getPos();

        Vec3 dir = end.getPos().sub(start.getPos()).normalize();

        this.p1 = p0.add(dir.scale(startHandle));
        this.p2 = p3.sub(dir.scale(endHandle));
        this.offset = offset;
    }

    public BezierLineGeometry(
            Transform start,
            Transform end,
            Vec3 startHandleDir,
            double startHandleLength,
            Vec3 endHandleDir,
            double endHandleLength
    ) {
        this(start, end, startHandleDir, startHandleLength, endHandleDir, endHandleLength, 0.0);
    }

    public BezierLineGeometry(
            Transform start,
            Transform end,
            Vec3 startHandleDir,
            double startHandleLength,
            Vec3 endHandleDir,
            double endHandleLength,
            double offset
    ) {
        this.p0 = start.getPos();
        this.p3 = end.getPos();

        this.p1 = p0.add(startHandleDir.normalize().scale(startHandleLength));
        this.p2 = p3.add(endHandleDir.normalize().scale(endHandleLength));
        this.offset = offset;
    }

    public static BezierLineGeometry auto(Transform start, Transform end) {
        Vec3 p0 = start.getPos();
        Vec3 p3 = end.getPos();

        Vec3 dir = p3.sub(p0);
        double d = dir.length();
        Vec3 n = dir.normalize();

        Vec3 p1 = p0.add(n.scale(d / 3f));
        Vec3 p2 = p3.sub(n.scale(d / 3f));

        return new BezierLineGeometry(p0, p1, p2, p3, 0.0);
    }

    @Override
    public LineGeometry parallelOffset(double offset) {
        return new BezierLineGeometry(p0, p1, p2, p3, offset);
    }
}

