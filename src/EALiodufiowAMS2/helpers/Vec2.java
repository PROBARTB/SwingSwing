//package EALiodufiowAMS2.helpers;
//
//public class Vec2 {
//    public final double x;
//    public final double z;
//
//    public Vec2(double x, double z) { this.x = x; this.z = z; }
//
//    public Vec2 add(Vec2 o) { return new Vec2(x + o.x, z + o.z); }
//    public Vec2 sub(Vec2 o) { return new Vec2(x - o.x, z - o.z); }
//    public Vec2 scale(double s) { return new Vec2(x * s, z * s); }
//    public double len() { return Math.sqrt(x*x + z*z); }
//    public Vec2 norm() { double l = len(); return l == 0 ? new Vec2(0,0) : new Vec2(x/l, z/l); }
//    public double angle() { return Math.atan2(z, x); }
//
//    public static Vec2 fromAngle(double a) { return new Vec2(Math.cos(a), Math.sin(a)); }
//
//    @Override public String toString() { return String.format("Vec2(%.3f, %.3f)", x, z); }
//}
