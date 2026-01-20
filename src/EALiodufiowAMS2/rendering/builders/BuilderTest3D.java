package EALiodufiowAMS2.rendering.builders;

import EALiodufiowAMS2.helpers.*;
import EALiodufiowAMS2.rendering.renderingObject.FaceType;
import EALiodufiowAMS2.rendering.renderingObject.Surface;
import EALiodufiowAMS2.rendering.TextureManager;
import EALiodufiowAMS2.rendering.renderingObject.*;
import EALiodufiowAMS2.rendering.renderingObject.geometries.CuboidGeometry;
import EALiodufiowAMS2.rendering.renderingObject.geometries.Geometry;

import java.awt.Color;
import java.util.*;

public class BuilderTest3D implements Builder {
    private final Map<String, RenderingObject> objects = new HashMap<>();

    public BuilderTest3D() {
        // wspólne tekstury dla przykładu
        List<Surface> surfaces = new ArrayList<>();
        surfaces.add(new Surface(FaceType.FRONT, new Material(Color.RED, TextureManager.getTexture("assets\\vehicles\\trams\\PesaSwing\\120Na-section-1.png"))));
        surfaces.add(new Surface(FaceType.BACK, new Material(Color.GREEN, TextureManager.getTexture("assets\\vehicles\\trams\\PesaSwing\\120Na-section-1.png"))));
        surfaces.add(new Surface(FaceType.TOP, new Material(Color.BLUE, TextureManager.getTexture("assets\\vehicles\\trams\\PesaSwing\\120Na-section-1.png"))));
        surfaces.add(new Surface(FaceType.BOTTOM, new Material(Color.YELLOW, TextureManager.getTexture("assets\\vehicles\\trams\\PesaSwing\\120Na-section-1.png"))));
        surfaces.add(new Surface(FaceType.LEFT, new Material(Color.CYAN, TextureManager.getTexture("assets\\vehicles\\trams\\PesaSwing\\120Na-section-1.png"))));
        surfaces.add(new Surface(FaceType.RIGHT, new Material(Color.MAGENTA, TextureManager.getTexture("assets\\vehicles\\trams\\PesaSwing\\120Na-section-1.png"))));

        List<Surface> surfacesColors = new ArrayList<>();
        surfacesColors.add(new Surface(FaceType.FRONT, new Material(Color.RED)));
        surfacesColors.add(new Surface(FaceType.BACK, new Material(Color.GREEN)));
        surfacesColors.add(new Surface(FaceType.TOP, new Material(Color.BLUE)));
        surfacesColors.add(new Surface(FaceType.BOTTOM, new Material(Color.YELLOW)));
        surfacesColors.add(new Surface(FaceType.LEFT, new Material(Color.CYAN)));
        surfacesColors.add(new Surface(FaceType.RIGHT, new Material(Color.MAGENTA)));

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


//        Surface s1 = new Surface(FaceType.FRONT, TextureManager.getTexture("assets\\texture.png"), Color.PINK);
//        Rectangle rect1 = new Rectangle(new Transform(), s1);
//        rect1.getTransform().setPos(new Vec3(0, 0, 7));
//        rect1.getTransform().setSize(new Vec3(14, 2, 0));
//        rect1.getTransform().setRot(Quaternion.fromEuler(new Vec3(0, 0, 0)));
//        objects.put("bgrect1", rect1);
//
//        Rectangle rect2 = new Rectangle(new Transform(), s1);
//        rect2.getTransform().setPos(new Vec3(7, 0, 0));
//        rect2.getTransform().setSize(new Vec3(14, 2, 0));
//        rect2.getTransform().setRot(Quaternion.fromEuler(new Vec3(Math.toRadians(-90), 0, 0)));
//        objects.put("bgrect2", rect2);
//
//        Rectangle rect3 = new Rectangle(new Transform(), s1);
//        rect3.getTransform().setPos(new Vec3(0, 0, -7));
//        rect3.getTransform().setSize(new Vec3(14, 2, 0));
//        rect3.getTransform().setRot(Quaternion.fromEuler(new Vec3(Math.toRadians(180), 0, 0)));
//        objects.put("bgrect3", rect3);
//
//        Rectangle rect4 = new Rectangle(new Transform(), s1);
//        rect4.getTransform().setPos(new Vec3(-7, 0, 0));
//        rect4.getTransform().setSize(new Vec3(14, 2, 0));
//        rect4.getTransform().setRot(Quaternion.fromEuler(new Vec3(Math.toRadians(90), 0, 0)));
//        objects.put("bgrect4", rect4);
//
//        StraightLine lineS1 = new StraightLine(new Vec3(-3, 0, -2), new Vec3(-1, 0, 0), Color.CYAN);
//        objects.put("lineS1", lineS1);
//
//        CurvedLine lineC1 = new CurvedLine(
//                new Transform(new Vec3(-1, 0, 0), Quaternion.fromEuler(new Vec3(Math.toRadians(315), 0, 0)), new Vec3(2, 0, 0)),
//                new Transform(new Vec3(1, 0, 2), Quaternion.fromEuler(new Vec3(0, 0, 0)), new Vec3(2, 0, 0)),
//                0.75, Color.YELLOW);
//        objects.put("lineC1", lineC1);
    }

    @Override
    public void update(double deltaTime) {
        double rotationSpeed = 2 * Math.PI / 10; // rad/s
        double deltaRotation = rotationSpeed * deltaTime;

        //for (Cuboid cube : cuboids.values()) {
        RenderingObject obj = objects.get("cubeWithCamera");
        obj.getTransform().rotateEuler(new Vec3(deltaRotation, 0, 0));
        //}
    }

    @Override
    public List<String> getObjectIds() {
        return new ArrayList<>(objects.keySet());
    }

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
