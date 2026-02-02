package EALiodufiowAMS2.game.builders;

import EALiodufiowAMS2.engine.rendering.TextureManager;
import EALiodufiowAMS2.engine.rendering.renderingObject.objects.cuboid.CuboidFaceType;
import EALiodufiowAMS2.engine.rendering.renderingObject.Material;
import EALiodufiowAMS2.engine.rendering.renderingObject.RenderingObject;
import EALiodufiowAMS2.engine.rendering.renderingObject.objects.cuboid.CuboidSurface;
import EALiodufiowAMS2.engine.rendering.renderingObject.objects.cuboid.CuboidGeometry;
import EALiodufiowAMS2.engine.rendering.renderingObject.Geometry;
import EALiodufiowAMS2.helpers.Quaternion;
import EALiodufiowAMS2.helpers.Transform;
import EALiodufiowAMS2.helpers.Vec3;

import java.awt.*;
import java.util.*;
import java.util.List;

public class PiotrostalObjectBuilder implements Builder {

    private static class CubeTextureInfo {
        String path;
        String type;

        CubeTextureInfo(String path, String type) {
            this.path = path;
            this.type = type;
        }
    }

    private final Map<String, RenderingObject> objects = new HashMap<>();
    private final Map<String, CubeTextureInfo> cubeInfoMap = new HashMap<>();
    private final Deque<String> spawnedCubeIds = new ArrayDeque<>();

    // animacja zgniatania
    private final Map<String, Double> shrinkingCubes = new HashMap<>();

    private final Random random = new Random();
    private int cubeCounter = 0;

    private int score = 0;

    private final CubeTextureInfo[] cubeTextures = {
            new CubeTextureInfo("assets/Piotrostal/ep07_1.jpg", "normal"),
            new CubeTextureInfo("assets/Piotrostal/ep07_2.jpg", "normal"),
            new CubeTextureInfo("assets/Piotrostal/et22_1.jpg", "ratrys"),
            new CubeTextureInfo("assets/Piotrostal/et22_2.jpg", "ratrys"),
    };

    public PiotrostalObjectBuilder() {
        spawnCubeInternal();
    }

    private void spawnCubeInternal() {
        CubeTextureInfo chosen = cubeTextures[random.nextInt(cubeTextures.length)];

        List<CuboidSurface> newCuboidSurfaces = new ArrayList<>();
        for (CuboidFaceType face : CuboidFaceType.values()) {
            newCuboidSurfaces.add(
                    new CuboidSurface(face, new Material(Color.WHITE, TextureManager.getTexture(chosen.path)))
            );
        }

        Geometry geo = new CuboidGeometry(newCuboidSurfaces);

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
        cubeInfoMap.put(id, chosen);
        spawnedCubeIds.push(id);
    }

    public void spawnNextCube() {
        if (!spawnedCubeIds.isEmpty()) {
            String lastId = spawnedCubeIds.pop();
            scrapCube(lastId);
        } else {
            System.out.println("Brak kostek do złomowania!");
        }
        spawnCubeInternal();
    }

    private void scrapCube(String id) {
        RenderingObject obj = objects.get(id);
        CubeTextureInfo info = cubeInfoMap.get(id);

        if (obj != null && info != null) {
            shrinkingCubes.put(id, 1.0); // start zgniatania
        }
    }

    private void finishScrap(String id) {
        RenderingObject obj = objects.remove(id);
        CubeTextureInfo info = cubeInfoMap.remove(id);
        shrinkingCubes.remove(id);

        if (obj != null && info != null) {
            if ("ratrys".equals(info.type)) {
                score += 50;
            } else {
                score += 20;
            }
            System.out.println("Dodano punkty, aktualny wynik: " + score);
        }
    }

    public int getScore() { return score; }
    public String getScoreText() { return "Punkty: " + score; }

    @Override
    public void update(double deltaTime) {

        double rotationSpeed = 2 * Math.PI / 10;
        double deltaRotation = rotationSpeed * deltaTime;

        // obracamy tylko te, które nie są zgniatane
        for (String id : spawnedCubeIds) {
            if (!shrinkingCubes.containsKey(id)) {
                RenderingObject cube = objects.get(id);
                if (cube != null) {
                    cube.getTransform().rotateEuler(new Vec3(0, deltaRotation, 0));
                }
            }
        }

        // animacja zgniatania
        double shrinkSpeed = 1.5;

        List<String> toRemove = new ArrayList<>();

        for (Map.Entry<String, Double> entry : shrinkingCubes.entrySet()) {
            String id = entry.getKey();
            double ySize = entry.getValue();

            ySize -= shrinkSpeed * deltaTime;

            RenderingObject cube = objects.get(id);
            if (cube != null) {
                double clampedY = Math.max(ySize, 0);
                cube.getTransform().setSize(new Vec3(1, clampedY, 1)); // ZGNIATANIE TYLKO W PIONIE
            }

            if (ySize <= 0) {
                toRemove.add(id);
            } else {
                shrinkingCubes.put(id, ySize);
            }
        }

        for (String id : toRemove) {
            finishScrap(id);
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
