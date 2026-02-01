package EALiodufiowAMS2.helpers;

public class Vec3 {
    public double x, y, z;

    public Vec3(double x, double y, double z) {
        this.x = x; this.y = y; this.z = z;
    }

    public Vec3(Vec3 other) {
        this(other.x, other.y, other.z);
    }

    public Vec3 add(Vec3 other) {
        return new Vec3(x + other.x, y + other.y, z + other.z);
    }

    public Vec3 sub(Vec3 other) {
        return new Vec3(x - other.x, y - other.y, z - other.z);
    }

    public Vec3 scale(double scalar) {
        return new Vec3(x * scalar, y * scalar, z * scalar);
    }

    public double dot(Vec3 other) {
        return x * other.x + y * other.y + z * other.z;
    }

    public Vec3 cross(Vec3 other) {
        return new Vec3(
                y * other.z - z * other.y,
                z * other.x - x * other.z,
                x * other.y - y * other.x
        );
    }

    public double length() {
        return Math.sqrt(x*x + y*y + z*z);
    }

    public Vec3 normalize() {
        double len = length();
        return len == 0 ? new Vec3(0,0,0) : new Vec3(x/len, y/len, z/len);
    }

    public double angleBetween(Vec3 other) {
        double dot = this.dot(other);
        double lengths = this.length() * other.length();
        return lengths == 0 ? 0 : Math.acos(dot / lengths);
    }

    public Vec3 rotate(Matrix3 m) {
        return m.multiply(this);
    }

    public Vec3 rotate(Quaternion q) {
        Quaternion v = new Quaternion(0, x, y, z);
        Quaternion qInv = q.conjugate(); // dla unit quaternion odwrotność = sprzężenie
        Quaternion result = q.multiply(v).multiply(qInv);
        return new Vec3(result.x, result.y, result.z);
    }

    public Vec3 lerp(Vec3 other, double t) {
        return new Vec3(
                this.x + (other.x - this.x) * t,
                this.y + (other.y - this.y) * t,
                this.z + (other.z - this.z) * t
        );
    }


    @Override
    public String toString() {
        return String.format("Vec3(%.3f, %.3f, %.3f)", x, y, z);
    }
}
