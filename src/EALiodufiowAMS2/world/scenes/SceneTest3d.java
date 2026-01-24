//package EALiodufiowAMS2.world.scenes;
//
//import EALiodufiowAMS2.engine.Scene;
//import EALiodufiowAMS2.general.layouts.ScenePanel;
//import EALiodufiowAMS2.helpers.*;
//import EALiodufiowAMS2.game.builders.Builder;
//import EALiodufiowAMS2.game.builders.BuilderTest3D;
//import EALiodufiowAMS2.engine.rendering.renderingObject.Material;
//import EALiodufiowAMS2.world.World;
//
//import java.awt.*;
//
//public class SceneTest3d extends ScenePanel {
//
//    public SceneTest3d(World world, int resWidth, int resHeight) {
//        super(world, new Scene(new Material(new Color(0xff5c6e7d, true))), resWidth, resHeight);
//
//        Builder r = new BuilderTest3D();
//        addRenderer(r);
//
//        Transform staticTransform = new Transform();
//        staticTransform.setPos(new Vec3(0, 0, -5));
//        staticTransform.setSize(new Vec3(1, 1, 1));
//        staticTransform.setRot(Quaternion.fromEuler(new Vec3(Math.toRadians(0), 0, 0)));
//
//
//        Transform t = r.getObjectTransform("cubeWithCamera");
//
//        //attachCameraTo(staticTransform);
//        attachCameraTo(t);
//
//
//    }
//
//}
