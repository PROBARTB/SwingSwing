package EALiodufiowAMS2.world;

import EALiodufiowAMS2.helpers.Transform;
import EALiodufiowAMS2.helpers.Vec3;

public class Camera {
    private Transform attachedTransform;
    private Vec3 offsetPos;
    private Vec3 offsetDir;
    private Transform transform;

    public Camera(Vec3 size) {
        this.offsetPos = new Vec3(0, 0,0);
        this.offsetDir = new Vec3(0, 0,0);;
        this.transform = new Transform(new Vec3(0, 0, 0),new Vec3(0, 0, 1),size);
    }
    public Camera( Vec3 size, Vec3 offsetPos, Vec3 offsetDir) {
        this.offsetPos = offsetPos;
        this.offsetDir = offsetDir;
        this.transform = new Transform(new Vec3(0, 0, 0),new Vec3(0, 0, 1),size);
    }

    public void attachTo(Transform attachedTransform) {
        this.attachedTransform = attachedTransform;
    }

    /** Pull latest transform from the attached section to update camera pose. */
    public void update() {
        if (attachedTransform == null) return;

        // DEBUG
        Vec3 pos__ = attachedTransform.getPos();
        Vec3 dir__ = attachedTransform.getDir();
        Vec3 size__ = attachedTransform.getSize();
        System.out.printf("Camera update: attachedTransform POS(%f, %f, %f) DIR(%f, %f, %f) SIZE(%f, %f, %f)\n", pos__.x, pos__.y, pos__.z, dir__.x, dir__.y, dir__.z, size__.x, size__.y, size__.z);
        //

        Vec3 secPos = attachedTransform.getPos();
        Vec3 secDir = attachedTransform.getDir();

        this.transform.setPos(secPos.add(offsetPos));
        this.transform.setDir(secDir.add(offsetDir));
    }

    public boolean isInFrustum(Transform t) {
        Vec3 camPos = transform.getPos();
        Vec3 camSize = transform.getSize();
        Vec3 objPos = t.getPos();
        Vec3 objSize = t.getPos();

        // uproszczony test AABB vs. frustum, ai pisało i nie uwzględnia kierunku !!!!!!!!!!!!!!!

        boolean inX = objPos.x + objSize.x >= camPos.x && objPos.x <= camPos.x + camSize.x;
        boolean inY = objPos.y + objSize.y >= camPos.y && objPos.y <= camPos.y + camSize.y;
        boolean inZ = objPos.z + objSize.z >= camPos.z && objPos.z <= camPos.z + camSize.z;
        return inX && inY && inZ;
    }

    public Transform getTransform() { return transform; }

    public void setSize(double w, double h) {
        this.transform.setSize(new Vec3(w, h, 0));
        System.out.println(w +"x"+ h + " " + this.transform.getSize());
    }
}
