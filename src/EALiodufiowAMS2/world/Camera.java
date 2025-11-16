package EALiodufiowAMS2.world;

import EALiodufiowAMS2.helpers.Transform;
import EALiodufiowAMS2.helpers.Vec3;

public class Camera {
    private Transform attachedTransform;
    private Vec3 offsetPos; // offset relative to section center
    private Vec3 offsetDir; // offset relative to section center
    private Vec3 pos; // camera position in world coordinates (meters)
    private Vec3 dir; // normalized viewing direction

    public Camera(Vec3 offsetPos, Vec3 offsetDir) {
        this.offsetPos = offsetPos;
        this.offsetDir = offsetDir;
        this.pos = new Vec3(0,0,0);
        this.dir = new Vec3(0,0,1);
    }

    public void attachTo(Transform attachedTransform) {
        this.attachedTransform = attachedTransform;
    }

    /** Pull latest transform from the attached section to update camera pose. */
    public void update() {
        if (attachedTransform == null) return;

        Vec3 secPos = attachedTransform.getPos();      // meters
        Vec3 secDir = attachedTransform.getDir();     // normalized

        // Camera position = section position + offset
        this.pos = secPos.add(offsetPos);

        // Camera forward = section direction + offset
        this.dir = secDir.add(offsetDir);
    }

    public Vec3 getPos() { return pos; }
    public Vec3 getDir() { return dir; }
}
