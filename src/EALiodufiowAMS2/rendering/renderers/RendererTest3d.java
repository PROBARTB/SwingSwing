package EALiodufiowAMS2.rendering.renderers;

import EALiodufiowAMS2.helpers.*;
import EALiodufiowAMS2.rendering.FaceType;
import EALiodufiowAMS2.rendering.Surface;
import EALiodufiowAMS2.rendering.TextureManager;
import EALiodufiowAMS2.rendering.renderingObjects.Cuboid;
import EALiodufiowAMS2.rendering.renderingObjects.RenderingObject;

import java.awt.Color;
import java.util.*;

public class RendererTest3d implements Renderer {
    private final Map<String, Cuboid> cuboids = new HashMap<>();

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


        // drugi cube
        Cuboid cube2 = new Cuboid(new Transform(), surfaces);
        cube2.getTransform().setPos(new Vec3(0, 0, 0));
        cube2.getTransform().setSize(new Vec3(1, 1, 1));
        cube2.getTransform().setDir(new Vec3(Math.toRadians(91), 0, 0));
        cuboids.put("rotatingCube2", cube2);

        // pierwszy cube
        Cuboid cube1 = new Cuboid(new Transform(), surfacesColors);
        cube1.getTransform().setPos(new Vec3(1, 0, 0));
        cube1.getTransform().setSize(new Vec3(1, 1, 1));
        cube1.getTransform().setDir(new Vec3(0, 0, 0));
        cuboids.put("rotatingCube1", cube1);

//
//        // trzeci cube
//        Cuboid cube3 = new Cuboid(new Transform(), surfaces);
//        cube3.getTransform().setPos(new Vec3(-3, -1, 4));
//        cube3.getTransform().setSize(new Vec3(1.5, 1.5, 1.5));
//        cube3.getTransform().setDir(new Vec3(0, 0, 0));
//        cuboids.put("rotatingCube3", cube3);
    }

    @Override
    public void update(double deltaTime) {
        double rotationSpeed = 2 * Math.PI / 10; // rad/s
        double deltaRotation = rotationSpeed * deltaTime;

        //for (Cuboid cube : cuboids.values()) {
        Cuboid cube = cuboids.get("rotatingCube2");
            Vec3 dir = cube.getTransform().getDir();
            dir = new Vec3(dir.x + deltaRotation, dir.y, dir.z);

            if (dir.y >= 360.0) {
                dir = new Vec3(dir.x, dir.y - 360.0, dir.z);
            }

            cube.getTransform().setDir(dir);
        //}
    }

    @Override
    public List<String> getObjectIds() {
        return new ArrayList<>(cuboids.keySet());
    }

    @Override
    public RenderingObject buildRenderingObject(String id) {
        return cuboids.get(id);
    }

    @Override
    public Transform getObjectTransform(String id) {
        Cuboid cube = cuboids.get(id);
        return cube != null ? cube.getTransform() : null;
    }
}
