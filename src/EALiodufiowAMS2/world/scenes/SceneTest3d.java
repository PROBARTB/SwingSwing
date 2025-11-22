package EALiodufiowAMS2.world.scenes;

import EALiodufiowAMS2.helpers.*;
import EALiodufiowAMS2.rendering.renderers.RendererTest3d;
import EALiodufiowAMS2.rendering.renderingObjects.Cuboid;
import EALiodufiowAMS2.world.World;

public class SceneTest3d extends ScenePanel {
    private final Cuboid staticCube;

    public SceneTest3d(World world, double viewportWidth, double viewportHeight) {
        super(world, viewportWidth, viewportHeight);

        addRenderer(new RendererTest3d());

        Transform staticTransform = new Transform();
        staticTransform.setPos(new Vec3(10, 0, 5));
        staticTransform.setSize(new Vec3(1, 1, 1));
        staticTransform.setDir(new Vec3(0.5, 0, 0));

        this.staticCube = new Cuboid(staticTransform, new java.util.ArrayList<>());

        attachCameraTo(staticTransform);
    }
}
