package EALiodufiowAMS2.rendering.renderers;

import EALiodufiowAMS2.helpers.*;
import EALiodufiowAMS2.rendering.FaceType;
import EALiodufiowAMS2.rendering.Surface;
import EALiodufiowAMS2.rendering.TextureManager;
import EALiodufiowAMS2.rendering.renderingObjects.*;

import java.awt.Color;
import java.util.*;

public class RendererTest3d implements Renderer {
    private final Map<String, RenderingObject> objects = new HashMap<>();

    public RendererTest3d() {
        // wspólne tekstury dla przykładu
        List<Surface> surfaces = new ArrayList<>();
        surfaces.add(new Surface(FaceType.FRONT, TextureManager.getTexture("assets\\vehicles\\trams\\PesaSwing\\120Na-section-1.png"), Color.RED));
        surfaces.add(new Surface(FaceType.BACK, TextureManager.getTexture("assets\\vehicles\\trams\\PesaSwing\\120Na-section-1.png"), Color.GREEN));
        surfaces.add(new Surface(FaceType.TOP, TextureManager.getTexture("assets\\vehicles\\trams\\PesaSwing\\120Na-section-1.png"), Color.BLUE));
        surfaces.add(new Surface(FaceType.BOTTOM, TextureManager.getTexture("assets\\vehicles\\trams\\PesaSwing\\120Na-section-1.png"), Color.YELLOW));
        surfaces.add(new Surface(FaceType.LEFT, TextureManager.getTexture("assets\\vehicles\\trams\\PesaSwing\\120Na-section-1.png"), Color.CYAN));
        surfaces.add(new Surface(FaceType.RIGHT, TextureManager.getTexture("assets\\vehicles\\trams\\PesaSwing\\120Na-section-1.png"), Color.MAGENTA));

        List<Surface> surfacesColors = new ArrayList<>();
        surfacesColors.add(new Surface(FaceType.FRONT, null, Color.RED));
        surfacesColors.add(new Surface(FaceType.BACK, null, Color.GREEN));
        surfacesColors.add(new Surface(FaceType.TOP, null, Color.BLUE));
        surfacesColors.add(new Surface(FaceType.BOTTOM, null, Color.YELLOW));
        surfacesColors.add(new Surface(FaceType.LEFT, null, Color.CYAN));
        surfacesColors.add(new Surface(FaceType.RIGHT, null, Color.MAGENTA));


        Cuboid cube2 = new Cuboid(new Transform(), surfaces);
        cube2.getTransform().setPos(new Vec3(0, 0, 0));
        cube2.getTransform().setSize(new Vec3(1, 1, 1));
        cube2.getTransform().setRot(Quaternion.fromEuler(new Vec3(Math.toRadians(0), 0, 0)));
        objects.put("cubeWithCamera", cube2);

        Cuboid cube1 = new Cuboid(new Transform(), surfacesColors);
        cube1.getTransform().setPos(new Vec3(1.5, 0, 0));
        cube1.getTransform().setSize(new Vec3(1, 1, 1));
        cube1.getTransform().setRot(Quaternion.fromEuler(new Vec3(0, 0, 0)));
        objects.put("rotatingCube1", cube1);

        Surface s1 = new Surface(FaceType.FRONT, TextureManager.getTexture("assets\\texture.png"), Color.PINK);
        Rectangle rect1 = new Rectangle(new Transform(), s1);
        rect1.getTransform().setPos(new Vec3(0, 0, 7));
        rect1.getTransform().setSize(new Vec3(14, 2, 0));
        rect1.getTransform().setRot(Quaternion.fromEuler(new Vec3(0, 0, 0)));
        objects.put("bgrect1", rect1);

        Rectangle rect2 = new Rectangle(new Transform(), s1);
        rect2.getTransform().setPos(new Vec3(7, 0, 0));
        rect2.getTransform().setSize(new Vec3(14, 2, 0));
        rect2.getTransform().setRot(Quaternion.fromEuler(new Vec3(Math.toRadians(-90), 0, 0)));
        objects.put("bgrect2", rect2);

        Rectangle rect3 = new Rectangle(new Transform(), s1);
        rect3.getTransform().setPos(new Vec3(0, 0, -7));
        rect3.getTransform().setSize(new Vec3(14, 2, 0));
        rect3.getTransform().setRot(Quaternion.fromEuler(new Vec3(Math.toRadians(180), 0, 0)));
        objects.put("bgrect3", rect3);

        Rectangle rect4 = new Rectangle(new Transform(), s1);
        rect4.getTransform().setPos(new Vec3(-7, 0, 0));
        rect4.getTransform().setSize(new Vec3(14, 2, 0));
        rect4.getTransform().setRot(Quaternion.fromEuler(new Vec3(Math.toRadians(90), 0, 0)));
        objects.put("bgrect4", rect4);

        StraightLine lineS1 = new StraightLine(new Vec3(-3, 0, -2), new Vec3(-1, 0, 0), Color.CYAN);
        objects.put("lineS1", lineS1);

        CurvedLine lineC1 = new CurvedLine(
                new Transform(new Vec3(-1, 0, 0), Quaternion.fromEuler(new Vec3(Math.toRadians(315), 0, 0)), new Vec3(2, 0, 0)),
                new Transform(new Vec3(1, 0, 2), Quaternion.fromEuler(new Vec3(0, 0, 0)), new Vec3(2, 0, 0)),
                0.75, Color.YELLOW);
        objects.put("lineC1", lineC1);
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
