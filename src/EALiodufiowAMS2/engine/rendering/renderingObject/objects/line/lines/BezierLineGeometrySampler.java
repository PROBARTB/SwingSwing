package EALiodufiowAMS2.engine.rendering.renderingObject.objects.line.lines;

import EALiodufiowAMS2.engine.rendering.renderingObject.objects.line.LineGeometrySampler;
import EALiodufiowAMS2.engine.rendering.renderingObject.objects.line.LineGeometrySample;
import EALiodufiowAMS2.helpers.*;

public final class BezierLineGeometrySampler implements LineGeometrySampler<BezierLineGeometry> {

    private final BezierLineGeometry geometry;

    private final int sampleCount;
    private final double[] tSamples;
    private final double[] lengthSamples;
    private final double length;

    private final Vec3[] tFrame; // tangent
    private final Vec3[] nFrame; // normal
    private final Vec3[] bFrame; // binormal

    private final double offset;

    public BezierLineGeometrySampler(BezierLineGeometry geometry, int sampleCount) {
        if (sampleCount < 4) {
            throw new IllegalArgumentException("sampleCount must be >= 4");
        }
        this.geometry = geometry;
        this.sampleCount = sampleCount;
        this.tSamples = new double[sampleCount];
        this.lengthSamples = new double[sampleCount];
        this.tFrame = new Vec3[sampleCount];
        this.nFrame = new Vec3[sampleCount];
        this.bFrame = new Vec3[sampleCount];

        this.offset = geometry.getOffset();

        this.length = buildLengthAndFrameTables();
    }

    @Override
    public double length() {
        return length;
    }

    private double buildLengthAndFrameTables() {
        Vec3 prevPos = positionAtT(0.0);
        tSamples[0] = 0.0;
        lengthSamples[0] = 0.0;

        Vec3 d0 = firstDerivativeAtT(0.0);
        Vec3 T0 = d0.length() > 0.0 ? d0.normalize() : new Vec3(1, 0, 0);

        Vec3 up = Math.abs(T0.y) < 0.99 ? new Vec3(0, 1, 0) : new Vec3(1, 0, 0);
        Vec3 B0 = T0.cross(up).normalize();
        Vec3 N0 = B0.cross(T0).normalize();

        tFrame[0] = T0;
        nFrame[0] = N0;
        bFrame[0] = B0;

        double cuscaleative = 0.0;

        for (int i = 1; i < sampleCount; i++) {
            double t = (double) i / (sampleCount - 1);
            Vec3 pos = positionAtT(t);

            cuscaleative += pos.sub(prevPos).length();
            tSamples[i] = t;
            lengthSamples[i] = cuscaleative;
            prevPos = pos;

            Vec3 d1 = firstDerivativeAtT(t);
            Vec3 Ti = d1.length() > 0.0 ? d1.normalize() : tFrame[i - 1];

            Vec3 Tprev = tFrame[i - 1];
            Vec3 Nprev = nFrame[i - 1];
            Vec3 Bprev = bFrame[i - 1];

            Vec3 axis = Tprev.cross(Ti);
            double axisLen = axis.length();

            if (axisLen < 1e-6) {
                tFrame[i] = Ti;
                nFrame[i] = Nprev;
                bFrame[i] = Bprev;
            } else {
                axis = axis.scale(1.0 / axisLen);
                double cosTheta = Tprev.dot(Ti);
                cosTheta = Math.max(-1.0, Math.min(1.0, cosTheta));
                double theta = Math.acos(cosTheta);

                Vec3 Ni = rotateAroundAxis(Nprev, axis, theta);
                Vec3 Bi = rotateAroundAxis(Bprev, axis, theta);

                tFrame[i] = Ti;
                nFrame[i] = Ni.normalize();
                bFrame[i] = Bi.normalize();
            }
        }

        return cuscaleative;
    }

    private static Vec3 rotateAroundAxis(Vec3 v, Vec3 axis, double angle) {
        double c = Math.cos(angle);
        double s = Math.sin(angle);
        double dot = v.dot(axis);

        Vec3 term1 = v.scale(c);
        Vec3 term2 = axis.cross(v).scale(s);
        Vec3 term3 = axis.scale(dot * (1.0 - c));

        return term1.add(term2).add(term3);
    }

    private Vec3 positionAtT(double t) {
        double u = 1.0 - t;
        double u2 = u * u;
        double u3 = u2 * u;
        double t2 = t * t;
        double t3 = t2 * t;

        Vec3 p0 = geometry.getP0();
        Vec3 p1 = geometry.getP1();
        Vec3 p2 = geometry.getP2();
        Vec3 p3 = geometry.getP3();

        return p0.scale(u3)
                .add(p1.scale(3.0 * u2 * t))
                .add(p2.scale(3.0 * u * t2))
                .add(p3.scale(t3));
    }

    private Vec3 firstDerivativeAtT(double t) {
        Vec3 p0 = geometry.getP0();
        Vec3 p1 = geometry.getP1();
        Vec3 p2 = geometry.getP2();
        Vec3 p3 = geometry.getP3();

        double u = 1.0 - t;
        double u2 = u * u;
        double t2 = t * t;

        Vec3 term1 = p1.sub(p0).scale(3.0 * u2);
        Vec3 term2 = p2.sub(p1).scale(6.0 * u * t);
        Vec3 term3 = p3.sub(p2).scale(3.0 * t2);

        return term1.add(term2).add(term3);
    }

    private Vec3 secondDerivativeAtT(double t) {
        Vec3 p0 = geometry.getP0();
        Vec3 p1 = geometry.getP1();
        Vec3 p2 = geometry.getP2();
        Vec3 p3 = geometry.getP3();

        double u = 1.0 - t;

        Vec3 a = p2.sub(p1.scale(2.0)).add(p0);
        Vec3 b = p3.sub(p2.scale(2.0)).add(p1);

        Vec3 term1 = a.scale(6.0 * u);
        Vec3 term2 = b.scale(6.0 * t);

        return term1.add(term2);
    }

    private double tForArcLength(double s) {
        if (length == 0.0) return 0.0;
        if (s <= 0.0) return 0.0;
        if (s >= length) return 1.0;

        int low = 0;
        int high = sampleCount - 1;

        while (low <= high) {
            int mid = (low + high) >>> 1;
            double midLen = lengthSamples[mid];
            if (midLen < s) {
                low = mid + 1;
            } else if (midLen > s) {
                high = mid - 1;
            } else {
                return tSamples[mid];
            }
        }

        int i1 = Math.max(1, low);
        int i0 = i1 - 1;

        double l0 = lengthSamples[i0];
        double l1 = lengthSamples[i1];
        double t0 = tSamples[i0];
        double t1 = tSamples[i1];

        double segmentLen = l1 - l0;
        if (segmentLen <= 1e-9) {
            return t0;
        }
        double alpha = (s - l0) / segmentLen;
        return t0 + (t1 - t0) * alpha;
    }

    private int findSegmentForS(double s) {
        if (s <= 0.0) return 0;
        if (s >= length) return sampleCount - 2;

        int low = 0;
        int high = sampleCount - 1;

        while (low <= high) {
            int mid = (low + high) >>> 1;
            double midLen = lengthSamples[mid];
            if (midLen < s) {
                low = mid + 1;
            } else if (midLen > s) {
                high = mid - 1;
            } else {
                return Math.max(0, mid - 1);
            }
        }
        return Math.max(0, low - 1);
    }

    private Frame frameAt(double s) {
        if (length == 0.0) {
            return new Frame(
                    new Vec3(1, 0, 0),
                    new Vec3(0, 1, 0),
                    new Vec3(0, 0, 1)
            );
        }

        int i0 = findSegmentForS(s);
        int i1 = i0 + 1;

        double l0 = lengthSamples[i0];
        double l1 = lengthSamples[i1];
        double segmentLen = l1 - l0;

        double alpha = segmentLen > 1e-9 ? (s - l0) / segmentLen : 0.0;
        alpha = Math.max(0.0, Math.min(1.0, alpha));

        Vec3 T0 = tFrame[i0];
        Vec3 N0 = nFrame[i0];
        Vec3 B0 = bFrame[i0];

        Vec3 T1 = tFrame[i1];
        Vec3 N1 = nFrame[i1];
        Vec3 B1 = bFrame[i1];

        Vec3 T = T0.lerp(T1, alpha).normalize();
        Vec3 N = N0.lerp(N1, alpha).normalize();
        Vec3 B = B0.lerp(B1, alpha).normalize();

        T = T.normalize();
        N = N.sub(T.scale(T.dot(N))).normalize();
        B = T.cross(N).normalize();

        return new Frame(T, N, B);
    }

    private static final class Frame {
        final Vec3 T;
        final Vec3 N;
        final Vec3 B;

        Frame(Vec3 T, Vec3 N, Vec3 B) {
            this.T = T;
            this.N = N;
            this.B = B;
        }
    }


    @Override
    public Transform transformAt(double s) {
        if (length == 0.0) {
            Vec3 p0 = geometry.getP0();
            return new Transform(p0, Quaternion.identity());
        }

        double t = tForArcLength(s);
        Vec3 basePos = positionAtT(t);

        Frame f = frameAt(s);
        Vec3 pos = basePos.add(f.N.scale(offset));

        Quaternion rot = Quaternion.fromMatrix3(Matrix3.fromBasis(f.T, f.N, f.B));

        return new Transform(pos, rot);
    }

    @Override
    public Vec3 tangentAt(double s) {
        return frameAt(s).T;
    }

    @Override
    public Vec3 normalAt(double s) {
        return frameAt(s).N;
    }

    @Override
    public Vec3 binormalAt(double s) {
        return frameAt(s).B;
    }

    @Override
    public double curvatureAt(double s) {
        if (length == 0.0) {
            return 0.0;
        }
        double t = tForArcLength(s);
        Vec3 d1 = firstDerivativeAtT(t);
        Vec3 d2 = secondDerivativeAtT(t);

        double len1 = d1.length();
        if (len1 == 0.0) {
            return 0.0;
        }
        Vec3 cross = d1.cross(d2);
        double num = cross.length();
        return num / (len1 * len1 * len1);
    }

    @Override
    public LineGeometrySample sampleAt(double s) {
        Transform tr = transformAt(s);
        Frame f = frameAt(s);
        double k = curvatureAt(s);
        return new LineGeometrySample(tr, f.T, f.N, f.B, k);
    }
}



