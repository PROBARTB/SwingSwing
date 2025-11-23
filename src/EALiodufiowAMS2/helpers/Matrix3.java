package EALiodufiowAMS2.helpers;

public class Matrix3 {
    // elementy macierzy wierszami
    public double m00, m01, m02;
    public double m10, m11, m12;
    public double m20, m21, m22;

    // Konstruktor domyślny = macierz jednostkowa
    public Matrix3() {
        this.m00 = 1; this.m01 = 0; this.m02 = 0;
        this.m10 = 0; this.m11 = 1; this.m12 = 0;
        this.m20 = 0; this.m21 = 0; this.m22 = 1;
    }

    // Konstruktor z wartościami
    public Matrix3(double m00, double m01, double m02,
                   double m10, double m11, double m12,
                   double m20, double m21, double m22) {
        this.m00 = m00; this.m01 = m01; this.m02 = m02;
        this.m10 = m10; this.m11 = m11; this.m12 = m12;
        this.m20 = m20; this.m21 = m21; this.m22 = m22;
    }

    // Macierz jednostkowa
    public static Matrix3 identity() {
        return new Matrix3();
    }

    // Mnożenie macierzy przez wektor
    public Vec3 multiply(Vec3 v) {
        return new Vec3(
                m00 * v.x + m01 * v.y + m02 * v.z,
                m10 * v.x + m11 * v.y + m12 * v.z,
                m20 * v.x + m21 * v.y + m22 * v.z
        );
    }

    // Mnożenie macierzy przez macierz
    public Matrix3 multiply(Matrix3 r) {
        return new Matrix3(
                m00*r.m00 + m01*r.m10 + m02*r.m20,
                m00*r.m01 + m01*r.m11 + m02*r.m21,
                m00*r.m02 + m01*r.m12 + m02*r.m22,

                m10*r.m00 + m11*r.m10 + m12*r.m20,
                m10*r.m01 + m11*r.m11 + m12*r.m21,
                m10*r.m02 + m11*r.m12 + m12*r.m22,

                m20*r.m00 + m21*r.m10 + m22*r.m20,
                m20*r.m01 + m21*r.m11 + m22*r.m21,
                m20*r.m02 + m21*r.m12 + m22*r.m22
        );
    }

    // Rotacja wokół osi X
    public static Matrix3 rotX(double angle) {
        double c = Math.cos(angle);
        double s = Math.sin(angle);
        return new Matrix3(
                1, 0, 0,
                0, c,-s,
                0, s, c
        );
    }

    // Rotacja wokół osi Y
    public static Matrix3 rotY(double angle) {
        double c = Math.cos(angle);
        double s = Math.sin(angle);
        return new Matrix3(
                c, 0, s,
                0, 1, 0,
                -s, 0, c
        );
    }

    // Rotacja wokół osi Z
    public static Matrix3 rotZ(double angle) {
        double c = Math.cos(angle);
        double s = Math.sin(angle);
        return new Matrix3(
                c,-s, 0,
                s, c, 0,
                0, 0, 1
        );
    }

    // Tworzenie macierzy z Eulerów (yaw, pitch, roll)
    // Konwencja: yaw = obrót wokół Y, pitch = wokół X, roll = wokół Z
    // Kolejność: Rz(roll) * Rx(pitch) * Ry(yaw)
    public static Matrix3 fromEuler(double yaw, double pitch, double roll) {
        Matrix3 Ry = rotY(yaw);
        Matrix3 Rx = rotX(pitch);
        Matrix3 Rz = rotZ(roll);

        // Kolejność mnożenia zależy od przyjętej konwencji!
        // Tutaj: najpierw yaw, potem pitch, potem roll
        return Rz.multiply(Rx).multiply(Ry);
    }
}
