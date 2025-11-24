package EALiodufiowAMS2.helpers;

public class Quaternion {
    public final double w, x, y, z;

    public Quaternion(double w, double x, double y, double z) {
        this.w = w; this.x = x; this.y = y; this.z = z;
    }

    public static Quaternion identity() {
        return new Quaternion(1,0,0,0);
    }

    public Quaternion normalize() {
        double len = Math.sqrt(w*w + x*x + y*y + z*z);
        return len == 0 ? identity() : new Quaternion(w/len, x/len, y/len, z/len);
    }

    public Quaternion conjugate() {
        return new Quaternion(w, -x, -y, -z);
    }

    public Quaternion multiply(Quaternion q) {
        return new Quaternion(
                w*q.w - x*q.x - y*q.y - z*q.z,
                w*q.x + x*q.w + y*q.z - z*q.y,
                w*q.y - x*q.z + y*q.w + z*q.x,
                w*q.z + x*q.y - y*q.x + z*q.w
        );
    }

    public static Quaternion fromEuler(Vec3 eulerRad) {
        double cy = Math.cos(eulerRad.y * 0.5);
        double sy = Math.sin(eulerRad.y * 0.5);
        double cp = Math.cos(eulerRad.x * 0.5);
        double sp = Math.sin(eulerRad.x * 0.5);
        double cr = Math.cos(eulerRad.z * 0.5);
        double sr = Math.sin(eulerRad.z * 0.5);

        return new Quaternion(
                cr*cp*cy + sr*sp*sy,
                sr*cp*cy - cr*sp*sy,
                cr*sp*cy + sr*cp*sy,
                cr*cp*sy - sr*sp*cy
        ).normalize();
    }

    public Vec3 toEuler() {
        double sinp = 2.0 * (w*x + y*z);
        double cosp = 1.0 - 2.0 * (x*x + y*y);
        double pitch = Math.atan2(sinp, cosp);

        double siny = 2.0 * (w*y - z*x);
        double yaw = Math.abs(siny) >= 1 ? Math.copySign(Math.PI/2, siny) : Math.asin(siny);

        double sinr = 2.0 * (w*z + x*y);
        double cosr = 1.0 - 2.0 * (y*y + z*z);
        double roll = Math.atan2(sinr, cosr);

        return new Vec3(pitch, yaw, roll);
    }

    public Matrix3 toMatrix3() {
        double xx = x*x, yy = y*y, zz = z*z;
        double xy = x*y, xz = x*z, yz = y*z;
        double wx = w*x, wy = w*y, wz = w*z;

        return new Matrix3(
                1 - 2*(yy+zz), 2*(xy - wz),     2*(xz + wy),
                2*(xy + wz),   1 - 2*(xx+zz),   2*(yz - wx),
                2*(xz - wy),   2*(yz + wx),     1 - 2*(xx+yy)
        );
    }

    public static Quaternion fromMatrix3(Matrix3 m) {
        double trace = m.m00 + m.m11 + m.m22;
        double w,x,y,z;

        if (trace > 0) {
            double s = Math.sqrt(trace + 1.0) * 2;
            w = 0.25 * s;
            x = (m.m21 - m.m12) / s;
            y = (m.m02 - m.m20) / s;
            z = (m.m10 - m.m01) / s;
        } else if (m.m00 > m.m11 && m.m00 > m.m22) {
            double s = Math.sqrt(1.0 + m.m00 - m.m11 - m.m22) * 2;
            w = (m.m21 - m.m12) / s;
            x = 0.25 * s;
            y = (m.m01 + m.m10) / s;
            z = (m.m02 + m.m20) / s;
        } else if (m.m11 > m.m22) {
            double s = Math.sqrt(1.0 + m.m11 - m.m00 - m.m22) * 2;
            w = (m.m02 - m.m20) / s;
            x = (m.m01 + m.m10) / s;
            y = 0.25 * s;
            z = (m.m12 + m.m21) / s;
        } else {
            double s = Math.sqrt(1.0 + m.m22 - m.m00 - m.m11) * 2;
            w = (m.m10 - m.m01) / s;
            x = (m.m02 + m.m20) / s;
            y = (m.m12 + m.m21) / s;
            z = 0.25 * s;
        }

        return new Quaternion(w,x,y,z).normalize();
    }

    @Override
    public String toString() {
        return String.format("Quaternion{w=%.3f, x=%.3f, y=%.3f, z=%.3f}", w, x, y, z);
    }
}
