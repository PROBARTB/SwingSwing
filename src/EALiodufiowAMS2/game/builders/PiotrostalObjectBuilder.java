package EALiodufiowAMS2.game.builders;

import EALiodufiowAMS2.engine.rendering.TextureManager;
import EALiodufiowAMS2.engine.rendering.renderingObject.FaceType;
import EALiodufiowAMS2.engine.rendering.renderingObject.Material;
import EALiodufiowAMS2.engine.rendering.renderingObject.RenderingObject;
import EALiodufiowAMS2.engine.rendering.renderingObject.Surface;
import EALiodufiowAMS2.engine.rendering.renderingObject.geometries.CuboidGeometry;
import EALiodufiowAMS2.engine.rendering.renderingObject.geometries.Geometry;
import EALiodufiowAMS2.helpers.Quaternion;
import EALiodufiowAMS2.helpers.Transform;
import EALiodufiowAMS2.helpers.Vec3;

import java.awt.*;
import java.util.*;
import java.util.List;

public class PiotrostalObjectBuilder implements Builder {

    private final Map<String, RenderingObject> objects = new HashMap<>();
    private final Deque<String> spawnedCubeIds = new ArrayDeque<>();

    private final Random random = new Random();
    private int cubeCounter = 0;

    public PiotrostalObjectBuilder() {

        // ---------------------------------------------------------
        // 1. TWORZYMY TYLKO cubeWithCamera — STAŁY OBIEKT
        // ---------------------------------------------------------

        Geometry cubeGeo = new CuboidGeometry(new ArrayList<>());

        Transform t1 = new Transform();
        t1.setPos(new Vec3(0, 0, 0));
        t1.setSize(new Vec3(1, 1, 1));
        t1.setRot(Quaternion.fromEuler(new Vec3(0, 0, 0)));

        RenderingObject cubeWithCamera = new RenderingObject(cubeGeo, t1);
        objects.put("cubeWithCamera", cubeWithCamera);

        // ---------------------------------------------------------
        // 2. TWORZYMY PIERWSZĄ NOWĄ KOSTKĘ
        // ---------------------------------------------------------
        spawnCubeInternal();
    }

    // ---------------------------------------------------------
    // TWORZENIE NOWEJ KOSTKI (używane wewnętrznie)
    // ---------------------------------------------------------

    private void spawnCubeInternal() {

        List<Surface> newSurfaces = new ArrayList<>();
        newSurfaces.add(new Surface(FaceType.FRONT, new Material(Color.WHITE, TextureManager.getTexture("assets\\Piotrostal\\ep07_1.jpg"))));
        newSurfaces.add(new Surface(FaceType.BACK, new Material(Color.WHITE, TextureManager.getTexture("assets\\Piotrostal\\ep07_1.jpg"))));
        newSurfaces.add(new Surface(FaceType.TOP, new Material(Color.WHITE, TextureManager.getTexture("assets\\Piotrostal\\ep07_1.jpg"))));
        newSurfaces.add(new Surface(FaceType.BOTTOM, new Material(Color.WHITE, TextureManager.getTexture("assets\\Piotrostal\\ep07_1.jpg"))));
        newSurfaces.add(new Surface(FaceType.LEFT, new Material(Color.WHITE, TextureManager.getTexture("assets\\Piotrostal\\ep07_1.jpg"))));
        newSurfaces.add(new Surface(FaceType.RIGHT, new Material(Color.WHITE, TextureManager.getTexture("assets\\Piotrostal\\ep07_1.jpg"))));

        Geometry geo = new CuboidGeometry(newSurfaces);

        Vec3 pos = new Vec3(
                random.nextDouble() * 6 - 3,
                random.nextDouble() * 3,
                random.nextDouble() * 6 - 3
        );

        Transform t = new Transform();
        t.setPos(pos);
        t.setSize(new Vec3(1, 1, 1));
        t.setRot(Quaternion.fromEuler(new Vec3(0, 0, 0)));

        RenderingObject newCube = new RenderingObject(geo, t);

        String id = "spawnedCube_" + cubeCounter++;
        objects.put(id, newCube);
        spawnedCubeIds.push(id); // LIFO
    }

    // ---------------------------------------------------------
    // FUNKCJA WYWOŁYWANA PO KLIKNIĘCIU PRZYCISKU
    // ---------------------------------------------------------

    public void spawnNextCube() {

        // 1. Usuń ostatnio dodaną kostkę (ale nigdy cubeWithCamera)
        if (!spawnedCubeIds.isEmpty()) {
            String lastId = spawnedCubeIds.pop();
            objects.remove(lastId);
        }

        // 2. Stwórz nową kostkę
        spawnCubeInternal();
    }

    // ---------------------------------------------------------
    // UPDATE – obracanie nowych kostek
    // ---------------------------------------------------------

    @Override
    public void update(double deltaTime) {
        double rotationSpeed = 2 * Math.PI / 10;
        double deltaRotation = rotationSpeed * deltaTime;

        for (String id : spawnedCubeIds) {
            RenderingObject cube = objects.get(id);
            if (cube != null) {
                cube.getTransform().rotateEuler(new Vec3(0, deltaRotation, 0));
            }
        }
    }

    @Override
    public List<String> getObjectIds() {
        return new ArrayList<>(objects.keySet());
    }

    @Override
    public List<RenderingObject> getRenderingObjects() {
        return new ArrayList<>(objects.values());
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
