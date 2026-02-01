package EALiodufiowAMS2.helpers;

public class Matrix3 {
    public final double m00, m01, m02;
    public final double m10, m11, m12;
    public final double m20, m21, m22;

    public Matrix3(double m00, double m01, double m02,
                   double m10, double m11, double m12,
                   double m20, double m21, double m22) {
        this.m00 = m00; this.m01 = m01; this.m02 = m02;
        this.m10 = m10; this.m11 = m11; this.m12 = m12;
        this.m20 = m20; this.m21 = m21; this.m22 = m22;
    }

    public static Matrix3 fromBasis(Vec3 x, Vec3 y, Vec3 z) {
        return new Matrix3(
                x.x, y.x, z.x,
                x.y, y.y, z.y,
                x.z, y.z, z.z
        );
    }

    public static Matrix3 identity() {
        return new Matrix3(
                1,0,0,
                0,1,0,
                0,0,1
        );
    }

    public Matrix3 transpose() {
        return new Matrix3(
                m00, m10, m20,
                m01, m11, m21,
                m02, m12, m22
        );
    }

    public Vec3 multiply(Vec3 v) {
        return new Vec3(
                m00*v.x + m01*v.y + m02*v.z,
                m10*v.x + m11*v.y + m12*v.z,
                m20*v.x + m21*v.y + m22*v.z
        );
    }

    public Matrix3 multiply(Matrix3 o) {
        return new Matrix3(
                m00*o.m00 + m01*o.m10 + m02*o.m20,
                m00*o.m01 + m01*o.m11 + m02*o.m21,
                m00*o.m02 + m01*o.m12 + m02*o.m22,

                m10*o.m00 + m11*o.m10 + m12*o.m20,
                m10*o.m01 + m11*o.m11 + m12*o.m21,
                m10*o.m02 + m11*o.m12 + m12*o.m22,

                m20*o.m00 + m21*o.m10 + m22*o.m20,
                m20*o.m01 + m21*o.m11 + m22*o.m21,
                m20*o.m02 + m21*o.m12 + m22*o.m22
        );
    }

    public static Matrix3 rotX(double a) {
        double c = Math.cos(a), s = Math.sin(a);
        return new Matrix3(
                1,0,0,
                0,c,-s,
                0,s,c
        );
    }

    public static Matrix3 rotY(double a) {
        double c = Math.cos(a), s = Math.sin(a);
        return new Matrix3(
                c,0,s,
                0,1,0,
                -s,0,c
        );
    }

    public static Matrix3 rotZ(double a) {
        double c = Math.cos(a), s = Math.sin(a);
        return new Matrix3(
                c,-s,0,
                s,c,0,
                0,0,1
        );
    }
}
