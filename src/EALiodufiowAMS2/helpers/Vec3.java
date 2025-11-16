package EALiodufiowAMS2.helpers;

public class Vec3 {
    public double x;
    public double y;
    public double z;

    public Vec3(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vec3 add(Vec3 other) {
        this.x += other.x;
        this.y += other.y;
        this.z += other.z;
        return this;
    }

    public Vec3 sub(Vec3 other) {
        this.x -= other.x;
        this.y -= other.y;
        this.z -= other.z;
        return this;
    }

    public Vec3 scale(double scalar) {
        this.x *= scalar;
        this.y *= scalar;
        this.z *= scalar;
        return this;
    }

    public double dot(Vec3 other) {
        return this.x * other.x + this.y * other.y + this.z * other.z;
    }

    public Vec3 cross(Vec3 other) {
        return new Vec3(
                this.y * other.z - this.z * other.y,
                this.z * other.x - this.x * other.z,
                this.x * other.y - this.y * other.x
        );
    }

    public double length() {
        return Math.sqrt(x * x + y * y + z * z);
    }

    public Vec3 normalize() {
        double len = length();
        if (len != 0) {
            this.x /= len;
            this.y /= len;
            this.z /= len;
        }
        return this;
    }

    public double angleBetween(Vec3 other) {
        double dot = this.dot(other);
        double lengths = this.length() * other.length();
        if (lengths == 0) return 0;
        return Math.acos(dot / lengths);
    }

    public double angleXY() {
        return Math.atan2(this.y, this.x);
    }

    public double angleXZ() {
        return Math.atan2(this.z, this.x);
    }

    public static Vec3 fromAngleXY(double angleRadians) {
        return new Vec3(Math.cos(angleRadians), Math.sin(angleRadians), 0);
    }

    public static Vec3 fromAngleXZ(double angleRadians) {
        return new Vec3(Math.cos(angleRadians), 0, Math.sin(angleRadians));
    }

    @Override
    public String toString() {
        return String.format("Vec3(%.3f, %.3f, %.3f)", x, y, z);
    }
}
