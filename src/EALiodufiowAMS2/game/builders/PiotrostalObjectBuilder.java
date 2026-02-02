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
        String type; // "normal" albo "ratrys"

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

    // bonusy
    private final String[] bonusTextures = {
            "assets/Piotrostal/bonus/bonus1.png",
            "assets/Piotrostal/bonus/bonus2.png",
            "assets/Piotrostal/bonus/bonus3.png"
    };
    private final Map<String, Double> bonusObjects = new HashMap<>();

    private final Random random = new Random();
    private int cubeCounter = 0;

    private int score = 0;

    // flaga blokująca wielokrotne kliknięcia
    private boolean isProcessing = false;

    private final CubeTextureInfo[] cubeTextures = {
            new CubeTextureInfo("assets/Piotrostal/ep07_1.jpg", "ratrys"),
            new CubeTextureInfo("assets/Piotrostal/ep07_2.jpg", "ratrys"),
            new CubeTextureInfo("assets/Piotrostal/et22_1.jpg", "ratrys"),
            new CubeTextureInfo("assets/Piotrostal/et22_2.jpg", "ratrys"),
            new CubeTextureInfo("assets/Piotrostal/ep07_1z.jpg", "zwykly")
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
        if (isProcessing) {
            return;
        }
        spawnCubeInternal();
    }

    public void scrapCube(String id) {
        spawnedCubeIds.remove(id);

        RenderingObject obj = objects.get(id);
        CubeTextureInfo info = cubeInfoMap.get(id);

        if (obj == null || info == null) {
            System.out.println("Nie znaleziono kostki o id: " + id);
            return;
        }

        if ("ratrys".equals(info.type)) {
            score += 50;
            System.out.println("Złomowano ratrysa, +50 punktów!");
            spawnBonus();
        } else {
            score -= 20;
            System.out.println("Złomowano zwykły, -20 punktów!");
        }
        shrinkingCubes.put(id, 1.0);
        isProcessing = true;
    }

    public void evaporateCube(String id) {
        spawnedCubeIds.remove(id);

        RenderingObject obj = objects.remove(id);
        CubeTextureInfo info = cubeInfoMap.remove(id);

        if (obj == null || info == null) {
            System.out.println("Nie znaleziono kostki o id: " + id);
            isProcessing = false;
            return;
        }

        if ("ratrys".equals(info.type)) {
            score -= 20;
            System.out.println("Pozostawiono ratrysa, -20 punktów!");
        } else {
            score += 20;
            System.out.println("Pozostawiono zwykły, +20 punktów!");
        }
        isProcessing = false;
    }

    private void spawnBonus() {
        String bonusPath = bonusTextures[random.nextInt(bonusTextures.length)];
        Material bonusMat = new Material(Color.WHITE, TextureManager.getTexture(bonusPath));
        Geometry bonusGeo = new CuboidGeometry(List.of(
                new CuboidSurface(CuboidFaceType.FRONT, bonusMat)
        ));

        Vec3 pos = new Vec3(
                random.nextDouble() * 6 - 3,
                random.nextDouble() * 3,
                random.nextDouble() * 6 - 3
        );

        Transform t = new Transform();
        t.setPos(pos);
        t.setSize(new Vec3(2, 2, 0.1)); // powiększone bonusy
        t.setRot(Quaternion.fromEuler(new Vec3(0, 0, 0)));

        RenderingObject bonusObj = new RenderingObject(bonusGeo, t);
        String bonusId = "bonus_" + cubeCounter++;
        objects.put(bonusId, bonusObj);
        bonusObjects.put(bonusId, 3.0); // 3 sekundy życia
    }

    public int getScore() { return score; }
    public String getScoreText() { return "Punkty: " + score; }
    public boolean isProcessing() { return isProcessing; }

    @Override
    public void update(double deltaTime) {
        double rotationSpeed = 2 * Math.PI / 10;
        double deltaRotation = rotationSpeed * deltaTime;

        for (String id : spawnedCubeIds) {
            if (!shrinkingCubes.containsKey(id)) {
                RenderingObject cube = objects.get(id);
                if (cube != null) {
                    cube.getTransform().rotateEuler(new Vec3(0, deltaRotation, 0));
                }
            }
        }

        double shrinkSpeed = 1.5;
        List<String> toRemove = new ArrayList<>();

        for (Map.Entry<String, Double> entry : shrinkingCubes.entrySet()) {
            String id = entry.getKey();
            double ySize = entry.getValue();

            ySize -= shrinkSpeed * deltaTime;

            RenderingObject cube = objects.get(id);
            if (cube != null) {
                double clampedY = Math.max(ySize, 0);
                cube.getTransform().setSize(new Vec3(1, clampedY, 1));
            }

            if (ySize <= 0) {
                toRemove.add(id);
            } else {
                shrinkingCubes.put(id, ySize);
            }
        }

        for (String id : toRemove) {
            objects.remove(id);
            cubeInfoMap.remove(id);
            shrinkingCubes.remove(id);
        }

        if (toRemove.size() > 0) {
            isProcessing = false;
            spawnCubeInternal();
        }

        // --- bonusy ---
        List<String> expiredBonus = new ArrayList<>();
        for (Map.Entry<String, Double> entry : bonusObjects.entrySet()) {
            String id = entry.getKey();
            double timeLeft = entry.getValue() - deltaTime;
            if (timeLeft <= 0) {
                expiredBonus.add(id);
            } else {
                bonusObjects.put(id, timeLeft);
            }
        }
        for (String id : expiredBonus) {
            objects.remove(id);
            bonusObjects.remove(id);
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
