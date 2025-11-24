package EALiodufiowAMS2.helpers;

public class Matrix3 {

    public double m00, m01, m02;
    public double m10, m11, m12;
    public double m20, m21, m22;

    // --- Konstruktor pusty ---
    public Matrix3() {}

    // --- Konstruktor z wartościami ---
    public Matrix3(double m00, double m01, double m02,
                   double m10, double m11, double m12,
                   double m20, double m21, double m22) {
        this.m00 = m00; this.m01 = m01; this.m02 = m02;
        this.m10 = m10; this.m11 = m11; this.m12 = m12;
        this.m20 = m20; this.m21 = m21; this.m22 = m22;
    }

    public static Matrix3 identity() {
        return new Matrix3();
    }

    // --- Transpozycja macierzy ---
    public Matrix3 transpose() {
        return new Matrix3(
                m00, m10, m20,
                m01, m11, m21,
                m02, m12, m22
        );
    }

    // --- Konwersja macierzy na kąty Eulera (yaw, pitch, roll) ---
    // Zakładamy konwencję: yaw(Y), pitch(X), roll(Z)
    public Vec3 toEuler() {
        double pitch, yaw, roll;

        // pitch (X)
        pitch = Math.asin(-m20);

        // yaw (Y) i roll (Z) zależą od cos(pitch)
        if (Math.abs(m20) < 0.999999) {
            yaw  = Math.atan2(m10, m00);
            roll = Math.atan2(m21, m22);
        } else {
            // Gimbal lock
            yaw  = Math.atan2(-m01, m11);
            roll = 0;
        }

        return new Vec3(pitch, yaw, roll);
    }

    public Vec3 multiply(Vec3 v) {
        return new Vec3(
                m00 * v.x + m01 * v.y + m02 * v.z,
                m10 * v.x + m11 * v.y + m12 * v.z,
                m20 * v.x + m21 * v.y + m22 * v.z
        );
    }

    // --- Mnożenie macierzy ---
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

    // --- Tworzenie macierzy z Eulerów ---
    public static Matrix3 fromEuler(double yaw, double pitch, double roll) {
        Matrix3 Ry = rotY(yaw);
        Matrix3 Rx = rotX(pitch);
        Matrix3 Rz = rotZ(roll);

        // Kolejność mnożenia zależy od przyjętej konwencji!
        // Tutaj: najpierw yaw, potem pitch, potem roll
        return Rz.multiply(Rx).multiply(Ry);
    }

    // --- Rotacje wokół osi ---
    public static Matrix3 rotX(double a) {
        double c = Math.cos(a), s = Math.sin(a);
        return new Matrix3(
                1, 0, 0,
                0, c, -s,
                0, s, c
        );
    }

    public static Matrix3 rotY(double a) {
        double c = Math.cos(a), s = Math.sin(a);
        return new Matrix3(
                c, 0, s,
                0, 1, 0,
                -s, 0, c
        );
    }

    public static Matrix3 rotZ(double a) {
        double c = Math.cos(a), s = Math.sin(a);
        return new Matrix3(
                c, -s, 0,
                s, c, 0,
                0, 0, 1
        );
    }
}