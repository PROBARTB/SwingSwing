package EALiodufiowAMS2.world.scenes;

import EALiodufiowAMS2.helpers.Quaternion;
import EALiodufiowAMS2.helpers.Transform;
import EALiodufiowAMS2.helpers.Vec3;
import EALiodufiowAMS2.rendering.renderingObjects.Cuboid;
import EALiodufiowAMS2.world.World;
import EALiodufiowAMS2.rendering.renderers.*;

public class Scene1 extends ScenePanel {

    public Scene1(World world, int resWidth, int resHeight) {
        super(world, resWidth, resHeight);

        addRenderer(new TracksRenderer(world));
        //addRenderer(new VehiclesRenderer(world));


        // test static point to attach camera
        Transform staticTransform = new Transform();
        staticTransform.setPos(new Vec3(0, 0, 0));
        staticTransform.setSize(new Vec3(1, 1, 1));
        staticTransform.setRot(Quaternion.fromEuler(new Vec3(Math.toRadians(0), 0, 0)));
        attachCameraTo(staticTransform);
    }
}
