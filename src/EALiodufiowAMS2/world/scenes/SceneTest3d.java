package EALiodufiowAMS2.world.scenes;

import EALiodufiowAMS2.helpers.*;
import EALiodufiowAMS2.rendering.renderers.Renderer;
import EALiodufiowAMS2.rendering.renderers.RendererTest3d;
import EALiodufiowAMS2.rendering.renderingObjects.Cuboid;
import EALiodufiowAMS2.world.World;

public class SceneTest3d extends ScenePanel {
    private final Cuboid staticCube;

    public SceneTest3d(World world, int resWidth, int resHeight) {
        super(world, resWidth, resHeight);

        Renderer r = new RendererTest3d();
        addRenderer(r);

        Transform staticTransform = new Transform();
        staticTransform.setPos(new Vec3(10, 0, 5));
        staticTransform.setSize(new Vec3(1, 1, 1));
        staticTransform.setRot(Quaternion.fromEuler(new Vec3(Math.toRadians(0), 0, 0)));

        this.staticCube = new Cuboid(staticTransform, new java.util.ArrayList<>());

        Transform t = r.getObjectTransform("cubeWithCamera");

        //attachCameraTo(staticTransform);
        attachCameraTo(t);
    }
}
