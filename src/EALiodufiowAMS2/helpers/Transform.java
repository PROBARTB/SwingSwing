package EALiodufiowAMS2.helpers;

import EALiodufiowAMS2.helpers.Vec3;

public class Transform {
    private Vec3 pos;
    private Vec3 dir;
    private Vec3 size;

    public Transform() {
        this.pos = new Vec3(0, 0, 0);
        this.dir = new Vec3(0, 0, 0);
        this.size = new Vec3(1, 1, 1);
    }

    public Transform(Vec3 position, Vec3 rotation, Vec3 size) {
        this.pos = position;
        this.dir = rotation;
        this.size = size;
    }

    public Transform(Transform transform) {
        this.pos = new Vec3(transform.getPos());
        this.dir = new Vec3(transform.getDir());
        this.size = new Vec3(transform.getSize());
    }

    public Vec3 getPos() { return pos; }
    public void setPos(Vec3 position) { this.pos = position; }

    public Vec3 getDir() { return dir; }
    public void setDir(Vec3 rotation) { this.dir = rotation; }

    public Vec3 getSize() { return size; }
    public void setSize(Vec3 size) { this.size = size; }


    public void translate(Vec3 delta) {
        this.pos.add(delta);
    }

    public void rotate(Vec3 delta) {
        this.dir.add(delta);
    }

    @Override
    public String toString() {
        return "Transform{" +
                "pos=" + pos +
                ", dir=" + dir +
                ", size=" + size +
                '}';
    }
}
