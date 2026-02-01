package EALiodufiowAMS2.general.world;

import EALiodufiowAMS2.engine.rendering.renderingObject.objects.cuboid.FaceType;
import EALiodufiowAMS2.engine.rendering.renderingObject.Material;
import EALiodufiowAMS2.engine.rendering.renderingObject.RenderingObject;
import EALiodufiowAMS2.engine.rendering.renderingObject.Surface;
import EALiodufiowAMS2.engine.rendering.renderingObject.objects.cuboid.CuboidGeometry;
import EALiodufiowAMS2.engine.rendering.renderingObject.Geometry;
import EALiodufiowAMS2.helpers.Quaternion;
import EALiodufiowAMS2.helpers.Transform;
import EALiodufiowAMS2.helpers.Vec3;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameplayWorld implements World{
    public void update(double dt) {

    };

    public List<RenderingObject> getRenderingObjects() {
        Map<String, RenderingObject> objects = new HashMap<>();
        List<Surface> surfacesColors = new ArrayList<>();
        surfacesColors.add(new Surface(FaceType.FRONT, new Material(Color.RED)));
        surfacesColors.add(new Surface(FaceType.BACK, new Material(Color.GREEN)));
        surfacesColors.add(new Surface(FaceType.TOP, new Material(Color.BLUE)));
        surfacesColors.add(new Surface(FaceType.BOTTOM, new Material(Color.YELLOW)));
        surfacesColors.add(new Surface(FaceType.LEFT, new Material(Color.CYAN)));
        surfacesColors.add(new Surface(FaceType.RIGHT, new Material(Color.MAGENTA)));
        Geometry cubeGeoColored = new CuboidGeometry(surfacesColors);
        Transform t1 = new Transform();
        t1.setPos(new Vec3(0, 0, 1));
        t1.setSize(new Vec3(1, 1, 1));
        t1.setRot(Quaternion.fromEuler(new Vec3(0, 0, 0)));
        RenderingObject cube1 = new RenderingObject(cubeGeoColored, t1);
        objects.put("cube1", cube1);

        return new ArrayList<>(objects.values());
    };
}
