package dasdwdjfhefhj;

//public record Vec2(double x, double z) {
//    public Vec2 add(Vec2 o) { return new Vec2(x + o.x, z + o.z); }
//    public Vec2 sub(Vec2 o) { return new Vec2(x - o.x, z - o.z); }
//    public Vec2 scale(double k) { return new Vec2(k * x, k * z); }
//    public double len() { return Math.hypot(x, z); }
//    public Vec2 norm() { double L = len(); return L == 0 ? this : new Vec2(x / L, z / L); }
//    public static Vec2 fromAngle(double theta) { return new Vec2(Math.cos(theta), Math.sin(theta)); }
//}
public class Vec2 {
    public final double x, z;
    public Vec2(double x, double z) { this.x = x; this.z = z; }
    public Vec2 add(Vec2 o) { return new Vec2(x+o.x, z+o.z); }
    public Vec2 sub(Vec2 o) { return new Vec2(x-o.x, z-o.z); }
    public Vec2 scale(double k) { return new Vec2(x*k, z*k); }
    public double dot(Vec2 o) { return x * o.x + z * o.z; }
    public double len() { return Math.hypot(x,z); }
    public Vec2 norm() { double L=len(); return new Vec2(x/L, z/L); }
    public static Vec2 fromAngle(double th) { return new Vec2(Math.cos(th), Math.sin(th)); }
}