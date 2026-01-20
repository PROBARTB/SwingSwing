package EALiodufiowAMS2.helpers;

public final class Matrix4 {
    public final double[] m; // 16 elementów, kolumnowo lub wierszowo – byle konsekwentnie

    public Matrix4(double[] m) { this.m = m; }

    public static Matrix4 identity() {
        return new Matrix4(new double[] {
                1,0,0,0,
                0,1,0,0,
                0,0,1,0,
                0,0,0,1
        });
    }

    public static Matrix4 translation(Vec3 t) {
        double[] m = identity().m.clone();
        m[12] = t.x;
        m[13] = t.y;
        m[14] = t.z;
        return new Matrix4(m);
    }

    public static Matrix4 scale(Vec3 s) {
        double[] m = identity().m.clone();
        m[0]  = s.x;
        m[5]  = s.y;
        m[10] = s.z;
        return new Matrix4(m);
    }

    public Matrix4 multiply(Matrix4 o) {
        double[] r = new double[16];
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                double sum = 0;
                for (int k = 0; k < 4; k++) {
                    sum += this.m[row + 4*k] * o.m[k + 4*col];
                }
                r[row + 4*col] = sum;
            }
        }
        return new Matrix4(r);
    }

    public Vec3 transformPoint(Vec3 v) {
        double x = v.x, y = v.y, z = v.z;
        double nx = m[0]*x + m[4]*y + m[8]*z + m[12];
        double ny = m[1]*x + m[5]*y + m[9]*z + m[13];
        double nz = m[2]*x + m[6]*y + m[10]*z + m[14];
        double w  = m[3]*x + m[7]*y + m[11]*z + m[15];
        if (w != 0.0) {
            nx /= w; ny /= w; nz /= w;
        }
        return new Vec3(nx, ny, nz);
    }

    public static Matrix4 rotate(Quaternion q) {
        double x = q.x, y = q.y, z = q.z, w = q.w;

        double[] m = new double[16];

        m[0] = 1 - 2*y*y - 2*z*z;
        m[1] = 2*x*y + 2*w*z;
        m[2] = 2*x*z - 2*w*y;
        m[3] = 0;

        m[4] = 2*x*y - 2*w*z;
        m[5] = 1 - 2*x*x - 2*z*z;
        m[6] = 2*y*z + 2*w*x;
        m[7] = 0;

        m[8] = 2*x*z + 2*w*y;
        m[9] = 2*y*z - 2*w*x;
        m[10] = 1 - 2*x*x - 2*y*y;
        m[11] = 0;

        m[12] = 0;
        m[13] = 0;
        m[14] = 0;
        m[15] = 1;

        return new Matrix4(m);
    }

    public Matrix4 invertAffine() {
        double[] r = new double[16];

        // Transpozycja rotacji (3x3)
        r[0] = m[0];  r[1] = m[4];  r[2] = m[8];
        r[4] = m[1];  r[5] = m[5];  r[6] = m[9];
        r[8] = m[2];  r[9] = m[6];  r[10] = m[10];

        // Odwrócenie translacji
        r[12] = -(r[0]*m[12] + r[4]*m[13] + r[8]*m[14]);
        r[13] = -(r[1]*m[12] + r[5]*m[13] + r[9]*m[14]);
        r[14] = -(r[2]*m[12] + r[6]*m[13] + r[10]*m[14]);

        // Ostatnia kolumna
        r[3] = 0;
        r[7] = 0;
        r[11] = 0;
        r[15] = 1;

        return new Matrix4(r);
    }

    public Matrix4 invert() {
        double[] inv = new double[16];
        double[] m = this.m;

        inv[0] = m[5]  * m[10] * m[15] -
                m[5]  * m[11] * m[14] -
                m[9]  * m[6]  * m[15] +
                m[9]  * m[7]  * m[14] +
                m[13] * m[6]  * m[11] -
                m[13] * m[7]  * m[10];

        inv[4] = -m[4]  * m[10] * m[15] +
                m[4]  * m[11] * m[14] +
                m[8]  * m[6]  * m[15] -
                m[8]  * m[7]  * m[14] -
                m[12] * m[6]  * m[11] +
                m[12] * m[7]  * m[10];

        inv[8] = m[4]  * m[9] * m[15] -
                m[4]  * m[11] * m[13] -
                m[8]  * m[5] * m[15] +
                m[8]  * m[7] * m[13] +
                m[12] * m[5] * m[11] -
                m[12] * m[7] * m[9];

        inv[12] = -m[4]  * m[9] * m[14] +
                m[4]  * m[10] * m[13] +
                m[8]  * m[5] * m[14] -
                m[8]  * m[6] * m[13] -
                m[12] * m[5] * m[10] +
                m[12] * m[6] * m[9];

        inv[1] = -m[1]  * m[10] * m[15] +
                m[1]  * m[11] * m[14] +
                m[9]  * m[2] * m[15] -
                m[9]  * m[3] * m[14] -
                m[13] * m[2] * m[11] +
                m[13] * m[3] * m[10];

        inv[5] = m[0]  * m[10] * m[15] -
                m[0]  * m[11] * m[14] -
                m[8]  * m[2] * m[15] +
                m[8]  * m[3] * m[14] +
                m[12] * m[2] * m[11] -
                m[12] * m[3] * m[10];

        inv[9] = -m[0]  * m[9] * m[15] +
                m[0]  * m[11] * m[13] +
                m[8]  * m[1] * m[15] -
                m[8]  * m[3] * m[13] -
                m[12] * m[1] * m[11] +
                m[12] * m[3] * m[9];

        inv[13] = m[0]  * m[9] * m[14] -
                m[0]  * m[10] * m[13] -
                m[8]  * m[1] * m[14] +
                m[8]  * m[2] * m[13] +
                m[12] * m[1] * m[10] -
                m[12] * m[2] * m[9];

        inv[2] = m[1]  * m[6] * m[15] -
                m[1]  * m[7] * m[14] -
                m[5]  * m[2] * m[15] +
                m[5]  * m[3] * m[14] +
                m[13] * m[2] * m[7] -
                m[13] * m[3] * m[6];

        inv[6] = -m[0]  * m[6] * m[15] +
                m[0]  * m[7] * m[14] +
                m[4]  * m[2] * m[15] -
                m[4]  * m[3] * m[14] -
                m[12] * m[2] * m[7] +
                m[12] * m[3] * m[6];

        inv[10] = m[0]  * m[5] * m[15] -
                m[0]  * m[7] * m[13] -
                m[4]  * m[1] * m[15] +
                m[4]  * m[3] * m[13] +
                m[12] * m[1] * m[7] -
                m[12] * m[3] * m[5];

        inv[14] = -m[0]  * m[5] * m[14] +
                m[0]  * m[6] * m[13] +
                m[4]  * m[1] * m[14] -
                m[4]  * m[2] * m[13] -
                m[12] * m[1] * m[6] +
                m[12] * m[2] * m[5];

        inv[3] = -m[1] * m[6] * m[11] +
                m[1] * m[7] * m[10] +
                m[5] * m[2] * m[11] -
                m[5] * m[3] * m[10] -
                m[9] * m[2] * m[7] +
                m[9] * m[3] * m[6];

        inv[7] = m[0] * m[6] * m[11] -
                m[0] * m[7] * m[10] -
                m[4] * m[2] * m[11] +
                m[4] * m[3] * m[10] +
                m[8] * m[2] * m[7] -
                m[8] * m[3] * m[6];

        inv[11] = -m[0] * m[5] * m[11] +
                m[0] * m[7] * m[9] +
                m[4] * m[1] * m[11] -
                m[4] * m[3] * m[9] -
                m[8] * m[1] * m[7] +
                m[8] * m[3] * m[5];

        inv[15] = m[0] * m[5] * m[10] -
                m[0] * m[6] * m[9] -
                m[4] * m[1] * m[10] +
                m[4] * m[2] * m[9] +
                m[8] * m[1] * m[6] -
                m[8] * m[2] * m[5];

        double det = m[0] * inv[0] + m[1] * inv[4] + m[2] * inv[8] + m[3] * inv[12];

        if (det == 0) return Matrix4.identity();

        det = 1.0 / det;
        for (int i = 0; i < 16; i++) inv[i] *= det;

        return new Matrix4(inv);
    }


}
