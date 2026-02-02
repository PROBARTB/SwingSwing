package EALiodufiowAMS2.game.builders;

import EALiodufiowAMS2.engine.rendering.renderingObject.objects.cuboid.CuboidFaceType;
import EALiodufiowAMS2.engine.rendering.renderingObject.objects.cuboid.CuboidSurface;
import EALiodufiowAMS2.engine.rendering.renderingObject.objects.line.FillMode;
import EALiodufiowAMS2.engine.rendering.renderingObject.objects.line.Line;
import EALiodufiowAMS2.engine.rendering.renderingObject.objects.line.LineGeometry;
import EALiodufiowAMS2.engine.rendering.renderingObject.objects.line.lines.StraightLineGeometry;
import EALiodufiowAMS2.helpers.*;
import EALiodufiowAMS2.engine.rendering.TextureManager;
import EALiodufiowAMS2.engine.rendering.renderingObject.*;
import EALiodufiowAMS2.engine.rendering.renderingObject.objects.cuboid.CuboidGeometry;
import EALiodufiowAMS2.engine.rendering.renderingObject.Geometry;

import java.awt.Color;
import java.util.*;

public class BuilderTest3D implements Builder {
    private final Map<String, RenderingObject> objects = new HashMap<>();

    public BuilderTest3D() {
        List<CuboidSurface> surfaces = new ArrayList<>();
        surfaces.add(new CuboidSurface(CuboidFaceType.FRONT, new Material(Color.RED, TextureManager.getTexture("assets\\vehicles\\trams\\PesaSwing\\120Na-section-1.png"))));
        surfaces.add(new CuboidSurface(CuboidFaceType.BACK, new Material(Color.GREEN, TextureManager.getTexture("assets\\vehicles\\trams\\PesaSwing\\120Na-section-1.png"))));
        surfaces.add(new CuboidSurface(CuboidFaceType.TOP, new Material(Color.BLUE, TextureManager.getTexture("assets\\vehicles\\trams\\PesaSwing\\120Na-section-1.png"))));
        surfaces.add(new CuboidSurface(CuboidFaceType.BOTTOM, new Material(Color.YELLOW, TextureManager.getTexture("assets\\vehicles\\trams\\PesaSwing\\120Na-section-1.png"))));
        surfaces.add(new CuboidSurface(CuboidFaceType.LEFT, new Material(Color.CYAN, TextureManager.getTexture("assets\\vehicles\\trams\\PesaSwing\\120Na-section-1.png"))));
        surfaces.add(new CuboidSurface(CuboidFaceType.RIGHT, new Material(Color.MAGENTA, TextureManager.getTexture("assets\\vehicles\\trams\\PesaSwing\\120Na-section-1.png"))));

        List<CuboidSurface> surfacesColors = new ArrayList<>();
        surfacesColors.add(new CuboidSurface(CuboidFaceType.FRONT, new Material(new Color(0xffff0000, true))));
        surfacesColors.add(new CuboidSurface(CuboidFaceType.BACK, new Material(Color.GREEN)));
        surfacesColors.add(new CuboidSurface(CuboidFaceType.TOP, new Material(Color.BLUE)));
        surfacesColors.add(new CuboidSurface(CuboidFaceType.BOTTOM, new Material(Color.YELLOW)));
        surfacesColors.add(new CuboidSurface(CuboidFaceType.LEFT, new Material(new Color(0xFF00FFFF, true))));
        surfacesColors.add(new CuboidSurface(CuboidFaceType.RIGHT, new Material(Color.MAGENTA)));

        List<CuboidSurface> surfacesAlpha = new ArrayList<>();
        surfacesAlpha.add(new CuboidSurface(CuboidFaceType.FRONT, new Material(Color.RED, TextureManager.getTexture("assets\\vehicles\\trams\\PesaSwing\\120Na.png"))));
        surfacesAlpha.add(new CuboidSurface(CuboidFaceType.BACK, new Material(new Color(0x884BD51C, true))));
        surfacesAlpha.add(new CuboidSurface(CuboidFaceType.TOP, new Material(Color.BLUE)));
        surfacesAlpha.add(new CuboidSurface(CuboidFaceType.BOTTOM, new Material(new Color(0x54FFFB00, true))));
        surfacesAlpha.add(new CuboidSurface(CuboidFaceType.LEFT, new Material(new Color(0xA300F7FF, true))));
        surfacesAlpha.add(new CuboidSurface(CuboidFaceType.RIGHT, new Material(new Color(0x27D380D5, true))));

        Geometry cubeGeoTextured = new CuboidGeometry(surfaces);
        Transform t1 = new Transform();
        t1.setPos(new Vec3(0, 0.5, 1));
        t1.setSize(new Vec3(1, 1, 1));
        t1.setRot(Quaternion.fromEuler(new Vec3(Math.toRadians(0), 0, 0)));
        RenderingObject cubeWithCamera = new RenderingObject(cubeGeoTextured, t1);
        objects.put("cubeWithCamera", cubeWithCamera);

        Geometry cubeGeoColored = new CuboidGeometry(surfacesColors);
        Transform t2 = new Transform();
        t2.setPos(new Vec3(1.5, 0, 0));
        t2.setSize(new Vec3(1, 1, 1));
        t2.setRot(Quaternion.fromEuler(new Vec3(0, 0, 0)));
        RenderingObject rotatingCube1 = new RenderingObject(cubeGeoColored, t2);
        objects.put("rotatingCube1", rotatingCube1);

        Geometry cubeGeoAlpha = new CuboidGeometry(surfacesAlpha);
        Transform t3 = new Transform();
        t3.setPos(new Vec3(3, 0, 2));
        t3.setSize(new Vec3(2, 2, 2));
        t3.setRot(Quaternion.fromEuler(new Vec3(Math.toRadians(0), 0, 0)));
        RenderingObject cube3 = new RenderingObject(cubeGeoAlpha, t3);
        objects.put("cube3", cube3);

        Transform tf = new Transform();
        tf.setPos(new Vec3(0, 0, 0));
        tf.setSize(new Vec3(1, 1, 1));
        tf.setRot(Quaternion.fromEuler(new Vec3(0, 0, 0)));
        RenderingObject filler = new RenderingObject(cubeGeoColored, tf);

        // PONIŻSZY FRAGMENT POWODUJE ZAMRAŻANIE OBRAZU NA iGPU Intela
//        Transform tlpos = new Transform(new Vec3(1, 1, 0), Quaternion.fromEuler(new Vec3(Math.toRadians(0), Math.toRadians(0), Math.toRadians(0))));
//        Transform tlts = new Transform(new Vec3(0, 0, 0), Quaternion.fromEuler(new Vec3(Math.toRadians(0), Math.toRadians(0), Math.toRadians(0))));
//        Transform tlte = new Transform(new Vec3(3, 0, 1), Quaternion.fromEuler(new Vec3(Math.toRadians(0), Math.toRadians(0), Math.toRadians(0))));
//        StraightLineGeometry testLineGeo = new StraightLineGeometry(tlts, tlte);
//        Line<StraightLineGeometry> testLine = new Line<StraightLineGeometry>(testLineGeo, filler, tlpos, FillMode.STRETCH, new Color(0xffffffff, true));
//        objects.put("testLine", testLine);
        // DO TĄD


    }

    @Override
    public void update(double deltaTime) {
        double rotationSpeed = 2 * Math.PI / 10; // rad/s
        double deltaRotation = rotationSpeed * deltaTime;

        //for (Cuboid cube : cuboids.values()) {
        RenderingObject obj = objects.get("cubeWithCamera");
        obj.getTransform().rotateEuler(new Vec3(deltaRotation, 0, 0));

        RenderingObject obj1 = objects.get("rotatingCube1");
        obj1.getTransform().rotateEuler(new Vec3(0, deltaRotation*0.5, deltaRotation*0.5));
        //}
    }

    @Override
    public List<String> getObjectIds() {
        return new ArrayList<>(objects.keySet());
    }

    @Override
    public List<RenderingObject> getRenderingObjects() { return new ArrayList<>(objects.values());};

    @Override
    public RenderingObject buildRenderingObject(String id) {
        return objects.get(id);
    }

    @Override
    public Transform getObjectTransform(String id) {
        RenderingObject obj = objects.get(id);
        return obj != null ? obj.getTransform() : null;
    }
}
