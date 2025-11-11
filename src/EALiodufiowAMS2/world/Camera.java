package EALiodufiowAMS2.world;

import EALiodufiowAMS2.helpers.Vec2;

public class Camera {
    private Vec2 attachPos = new Vec2(0,0);
    private Vec2 attachDir = new Vec2(0,0);

    public void attachTo(Vec2 pos, Vec2 angle) {
        this.attachPos = pos; this.attachDir = angle;
    }

    public Vec2 getAttachPos() { return attachPos; }
    public Vec2 getAttachAngle() { return attachDir; }
}
