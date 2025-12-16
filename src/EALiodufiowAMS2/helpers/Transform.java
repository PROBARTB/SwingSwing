package EALiodufiowAMS2.helpers;

import EALiodufiowAMS2.helpers.Vec3;

public class Transform {
    private Vec3 pos;
    private Quaternion rot;
    private Vec3 size;

    public Transform() {
        this(new Vec3(0,0,0), Quaternion.identity(), new Vec3(1,1,1));
    }
    public Transform(Vec3 size) {
        this(new Vec3(0,0,0), Quaternion.identity(), size);
    }
    public Transform(Vec3 pos, Quaternion rot) { this(pos, rot, null); }
    public Transform(Vec3 pos, Quaternion rot, Vec3 size) {
        this.pos = pos;
        this.rot = rot;
        this.size = size;
    }

    public Vec3 getPos() { return pos; }
    public void setPos(Vec3 pos) { this.pos = pos; }

    public Quaternion getRot() { return rot; }
    public void setRot(Quaternion rot) { this.rot = rot; }

    public Vec3 getSize() { return size; }
    public void setSize(Vec3 size) { this.size = size; }


    public void setRotationEuler(Vec3 eulerDegrees) {
        this.rot = Quaternion.fromEuler(eulerDegrees);
    }
    public Vec3 getRotationEuler() {
        return rot.toEuler();
    }


    public void translate(Vec3 delta) {
        this.pos = this.pos.add(delta);
    }

    public void rotateEuler(Vec3 deltaEuler) {
        Quaternion delta = Quaternion.fromEuler(deltaEuler);
        this.rot = this.rot.multiply(delta);
    }

    @Override
    public String toString() {
        return "Transform{" +
                "pos=" + (pos != null ? pos : "null") +
                ", rotEuler=" + (rot != null ? rot.toEuler() : "null") +
                ", size=" + (size != null ? size : "null") +
                '}';
    }

}

