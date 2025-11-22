package EALiodufiowAMS2.rendering.renderers;

import EALiodufiowAMS2.helpers.*;
import EALiodufiowAMS2.rendering.Surface;
import EALiodufiowAMS2.rendering.renderingObjects.Cuboid;
import EALiodufiowAMS2.rendering.renderingObjects.RenderingObject;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RendererTest3d implements Renderer {
    private final Cuboid rotatingCube;
    private final Transform transform;

    public RendererTest3d() {
        List<Surface> surfaces = new ArrayList<>();
        surfaces.add(new Surface("front", null, Color.RED));
        surfaces.add(new Surface("back", null, Color.GREEN));
        surfaces.add(new Surface("left", null, Color.BLUE));
        surfaces.add(new Surface("right", null, Color.YELLOW));
        surfaces.add(new Surface("top", null, Color.CYAN));
        surfaces.add(new Surface("bottom", null, Color.MAGENTA));

        this.rotatingCube = new Cuboid(new Transform(), surfaces);
        this.transform = this.rotatingCube.getTransform();

        this.transform.setPos(new Vec3(10, 0, 5));
        this.transform.setSize(new Vec3(1, 1, 1));
        this.transform.setDir(new Vec3(1, 0, 0));
    }

    @Override
    public void update(double deltaTime) {
        System.out.printf("RendererTest3d update %f\n", deltaTime);
        this.transform.getDir().rotate(new Vec3(0, 0, 1 * deltaTime)); // 1 st. / s
        this.transform.getDir().normalize();
    }

    @Override
    public List<String> getObjectIds() {
        return Collections.singletonList("rotatingCube");
    }

    @Override
    public RenderingObject buildRenderingObject(String id) {
        if ("rotatingCube".equals(id)) {
            return rotatingCube;
        }
        return null;
    }

    @Override
    public Transform getObjectTransform(String id) {
        return transform;
    }
}
