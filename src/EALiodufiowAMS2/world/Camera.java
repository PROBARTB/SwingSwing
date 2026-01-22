package EALiodufiowAMS2.world;

import EALiodufiowAMS2.helpers.*;

public class Camera {
    private Transform attachedTransform;
    private Vec3 offsetPos;
    private Vec3 offsetRot;
    private final Transform transform;
    private int fov;
    private double k;

    private double nearPlane = 0.1;
    private double farPlane = 1000.0;
    private double aspectRatio;
    private boolean useDynamicAspectRatio = true;

    public Camera(Vec3 size) {
        this.offsetPos = new Vec3(0, 0,0);
        this.offsetRot = new Vec3(0, 0,0);;
        this.transform = new Transform(size);
        this.fov = 75;
        this.k = fovToK(fov, this.transform.getSize().x);
    }
    public Camera( Vec3 size, Vec3 offsetPos, Vec3 offsetRot, int fov) {
        this.offsetPos = offsetPos;
        this.offsetRot = offsetRot;
        this.transform = new Transform(size);
        this.fov = fov;
        this.k = fovToK(fov, this.transform.getSize().x);
    }
    public Camera( Vec3 size, Vec3 offsetPos, Vec3 offsetRot, int fov, double aspectRatio) {
        this(size, offsetPos, offsetRot, fov);
        this.useDynamicAspectRatio = false;
        this.aspectRatio = aspectRatio;
    }

    public void attachTo(Transform attachedTransform) {
        this.attachedTransform = attachedTransform;
    }

    public void update() {
        if (attachedTransform == null) return;

        Vec3 attachedPos = attachedTransform.getPos();
        Quaternion attachedRot = attachedTransform.getRot();

        Vec3 localOffsetPos = offsetPos;
        Quaternion localOffsetRot = Quaternion.fromEuler(offsetRot);

        Vec3 worldOffsetPos = localOffsetPos.rotate(attachedRot);

        Vec3 cameraPos = attachedPos.add(worldOffsetPos).add(new Vec3(0, attachedTransform.getSize().y/2, 0));

        Quaternion cameraRot = attachedRot.multiply(localOffsetRot);

        this.transform.setPos(cameraPos);
        this.transform.setRot(cameraRot);

        //System.out.println("Camera update: pos=" + cameraPos + " rot=" + cameraRot.toEuler());
    }



    public boolean isInFrustum(Transform t) {
        // TA metoda jest do poprawy, w ogóle nie była testowana i chyba nie działą.
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
        if(this.useDynamicAspectRatio) this.aspectRatio = w / h;

        System.out.println("Camera size set:" + w +"x"+ h + " " + this.transform.getSize());
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
    public void setNearPlane(double n) { this.nearPlane = n; }
    public void setFarPlane(double f) { this.farPlane = f; }
    public void setAspectRatio(double ratio) { this.aspectRatio = ratio; }
    public void setUseDynamicAspectRatio(boolean use) { this.useDynamicAspectRatio = use; }


//    public Matrix4 getViewMatrix() {
//        Quaternion rotInv = transform.getRot().normalize().conjugate();
//        Matrix4 trans = Matrix4.translation(transform.getPos().scale(-1));
//
//        return Matrix4.rotate(rotInv).multiply(trans);
//    }
//    public Matrix4 getViewMatrix() {
//        Quaternion camRotInv = transform.getRot().normalize().conjugate();
//
//        Quaternion flipZ = Quaternion.fromEuler(new Vec3(0, 0, Math.toRadians(180)));
//
//        Quaternion viewRot = flipZ.multiply(camRotInv);
//
//        Matrix4 rot = Matrix4.rotate(viewRot);
//        Matrix4 trans = Matrix4.translation(transform.getPos().scale(-1));
//
//        return rot.multiply(trans); // V = R_view * T(-C)
//    }
    public Matrix4 getViewMatrix() {
        Quaternion camRotInv = transform.getRot().normalize().conjugate();
        Matrix4 rot = Matrix4.rotate(camRotInv);
        Matrix4 trans = Matrix4.translation(transform.getPos().scale(-1));

        Matrix4 flipZ = Matrix4.scale(new Vec3(1, 1, -1));

        return flipZ.multiply(rot).multiply(trans);
    }

    public Matrix4 getProjectionMatrix() {
        double f = 1.0 / Math.tan(Math.toRadians(fov) / 2.0);
        double nf = 1.0 / (nearPlane - farPlane);
        double[] m = new double[16];
        m[0]  = f / aspectRatio;
        m[5]  = f;
        m[10] = (farPlane + nearPlane) * nf;
        m[11] = -1.0;
        m[14] = 2 * farPlane * nearPlane * nf;
        return new Matrix4(m);
    }


}