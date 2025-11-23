package EALiodufiowAMS2.world;

import EALiodufiowAMS2.helpers.Matrix3;
import EALiodufiowAMS2.helpers.Transform;
import EALiodufiowAMS2.helpers.Vec3;

public class Camera {
    private Transform attachedTransform;
    private Vec3 offsetPos;
    private Vec3 offsetDir;
    private Transform transform;
    private int fov;
    private double k;

    public Camera(Vec3 size) {
        this.offsetPos = new Vec3(0, 0,0);
        this.offsetDir = new Vec3(0, 0,0);;
        this.transform = new Transform(new Vec3(0, 0, 0),new Vec3(0, 0, 1),size);
        this.fov = 75;
        this.k = fovToK(fov, this.transform.getSize().x);
    }
    public Camera( Vec3 size, Vec3 offsetPos, Vec3 offsetDir, int fov) {
        this.offsetPos = offsetPos;
        this.offsetDir = offsetDir;
        this.transform = new Transform(new Vec3(0, 0, 0),new Vec3(0, 0, 1),size);
        this.fov = fov;
        this.k = fovToK(fov, this.transform.getSize().x);
    }

    public void attachTo(Transform attachedTransform) {
        this.attachedTransform = attachedTransform;
    }

    /** Pull latest transform from the attached section to update camera pose. */
    public void update() {
        if (attachedTransform == null) return;

        // Pozycja i rotacja obiektu
        Vec3 attachedPos = new Vec3(attachedTransform.getPos());
        Vec3 attachedDir = new Vec3(attachedTransform.getDir());

        // Offset kamery w lokalnym układzie obiektu (np. przed nim)
        Vec3 localOffset = offsetPos;

        // Macierz rotacji z Eulerów (pitch=X, yaw=Y, roll=Z)
        Matrix3 rot = Matrix3.fromEuler(attachedDir.x, attachedDir.y, attachedDir.z);

        // Przekształcenie offsetu do globalnych współrzędnych
        Vec3 worldOffset = rot.multiply(localOffset);

        // Globalna pozycja kamery
        Vec3 cameraPos = attachedPos.add(worldOffset);

        // Ustawienie transformacji kamery
        this.transform.setPos(cameraPos);
        this.transform.setDir(attachedDir);

        System.out.println("Camera update: pos=" + cameraPos + " dir=" + attachedDir);
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

    public int getFov() { return fov; }
    public void setFov(int fov) {
        this.fov = fov;
        this.k = fovToK(fov, this.transform.getSize().x);
    }
    public double getK() { return this.k; }

    public static double fovToK(double fovDeg, double screenWidthM) {
        double fovRad = Math.toRadians(fovDeg);
        return screenWidthM / (2.0 * Math.tan(fovRad / 2.0));
    }
}
