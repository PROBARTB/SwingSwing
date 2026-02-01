package EALiodufiowAMS2.engine.rendering.renderingObject.objects.line.lines;

import EALiodufiowAMS2.engine.rendering.renderingObject.objects.line.LineGeometrySampler;
import EALiodufiowAMS2.engine.rendering.renderingObject.objects.line.LineGeometrySample;
import EALiodufiowAMS2.helpers.*;

public final class PolyLineGeometrySampler implements LineGeometrySampler<PolyLineGeometry> {

    private final PolyLineGeometry geometry;
    private final double[] cuscaleativeLengths;
    private final double length;
    private final double offset;

    public PolyLineGeometrySampler(PolyLineGeometry geometry) {
        this.geometry = geometry;
        this.cuscaleativeLengths = buildLengthTable();
        this.length = cuscaleativeLengths[cuscaleativeLengths.length - 1];
        this.offset = geometry.getOffset();
    }

    @Override
    public double length() {
        return length;
    }

    private double[] buildLengthTable() {
        int n = geometry.getPointCount();
        double[] cum = new double[n];
        cum[0] = 0.0;
        double total = 0.0;
        for (int i = 1; i < n; i++) {
            Transform t0 = geometry.getPoint(i - 1);
            Transform t1 = geometry.getPoint(i);
            Vec3 p0 = t0.getPos();
            Vec3 p1 = t1.getPos();
            total += p1.sub(p0).length();
            cum[i] = total;
        }
        return cum;
    }

    private int segmentIndexFor(double s) {
        int low = 0;
        int high = cuscaleativeLengths.length - 1;

        while (low <= high) {
            int mid = (low + high) >>> 1;
            double midLen = cuscaleativeLengths[mid];
            if (midLen < s) {
                low = mid + 1;
            } else if (midLen > s) {
                high = mid - 1;
            } else {
                return mid;
            }
        }
        return Math.max(0, low - 1);
    }

    private double localT(int segIndex, double s) {
        double segStart = cuscaleativeLengths[segIndex];
        double segEnd = cuscaleativeLengths[segIndex + 1];
        double segLen = segEnd - segStart;
        if (segLen <= 1e-9) {
            return 0.0;
        }
        return (s - segStart) / segLen;
    }

    @Override
    public Transform transformAt(double s) {
        if (length == 0.0) {
            return geometry.getPoint(0);
        }

        int n = geometry.getPointCount();
        if (s >= length) {
            return geometry.getPoint(n - 1);
        }

        int segIndex = segmentIndexFor(s);
        if (segIndex >= n - 1) {
            return geometry.getPoint(n - 1);
        }

        double t = localT(segIndex, s);

        Transform t0 = geometry.getPoint(segIndex);
        Transform t1 = geometry.getPoint(segIndex + 1);

        Vec3 p0 = t0.getPos();
        Vec3 p1 = t1.getPos();
        Vec3 pos = p0.lerp(p1, t);

        Quaternion r0 = t0.getRot();
        Quaternion r1 = t1.getRot();
        Quaternion rot = Quaternion.slerp(r0, r1, t);

        Vec3 s0 = t0.getSize();
        Vec3 s1 = t1.getSize();
        Vec3 size = s0.lerp(s1, t);

        // offset równoległy wzdłuż normalnej
        Vec3 nDir = normalAt(s);
        Vec3 offsetVec = nDir.scale(offset);
        Vec3 finalPos = pos.add(offsetVec);

        return new Transform(finalPos, rot, size);
    }

    @Override
    public Vec3 tangentAt(double s) {
        if (length == 0.0) {
            return new Vec3(1, 0, 0);
        }

        int n = geometry.getPointCount();
        if (s >= length) {
            Transform t0 = geometry.getPoint(n - 2);
            Transform t1 = geometry.getPoint(n - 1);
            Vec3 dir = t1.getPos().sub(t0.getPos());
            double len = dir.length();
            return (len > 0.0) ? dir.scale(1.0 / len) : new Vec3(1, 0, 0);
        }

        int segIndex = segmentIndexFor(s);
        if (segIndex >= n - 1) {
            segIndex = n - 2;
        }

        Transform t0 = geometry.getPoint(segIndex);
        Transform t1 = geometry.getPoint(segIndex + 1);
        Vec3 dir = t1.getPos().sub(t0.getPos());
        double len = dir.length();
        return (len > 0.0) ? dir.scale(1.0 / len) : new Vec3(1, 0, 0);
    }

    @Override
    public Vec3 normalAt(double s) {
        Vec3 t = tangentAt(s);
        Vec3 up = Math.abs(t.y) < 0.99 ? new Vec3(0, 1, 0) : new Vec3(1, 0, 0);
        Vec3 b = t.cross(up).normalize();
        return b.cross(t).normalize();
    }

    @Override
    public Vec3 binormalAt(double s) {
        Vec3 t = tangentAt(s);
        Vec3 n = normalAt(s);
        return t.cross(n).normalize();
    }

    @Override
    public double curvatureAt(double s) {
        return 0.0;
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