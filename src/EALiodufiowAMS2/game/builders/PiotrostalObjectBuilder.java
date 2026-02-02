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

    private final Map<String, GameObject> objects = new HashMap<>();
    private final ObjectFactory factory = new ObjectFactory();

    private String currentCubeId = null; // zawsze 1 aktywny cube (RATRYS/NORMAL)
    private int idCounter = 0;

    private int bonusCounter = 0;

    private int lastScore = 0;
    private int score = 0;
    private boolean isProcessing = false; // true tylko podczas animacji zgniatania

    private final double X_TARGET_ANIMATION = 50.0;

    public PiotrostalObjectBuilder() {
        spawnNewCube();
    }

    private String nextId(String prefix) {
        return prefix + "_" + (idCounter++);
    }

    private final Random random = new Random();

    private void spawnNewCube() {
        String id = nextId("cube");
        GameObject cube = factory.createCube(id);

        // ustaw start na x = -100
        Transform t = cube.render.getTransform();
        Vec3 pos = t.getPos();
        double targetX = pos.x;
        pos.x = -X_TARGET_ANIMATION;
        t.setPos(pos);

        cube.moving = true;
        cube.moveTargetX = targetX;

        objects.put(id, cube);
        currentCubeId = id;
    }


    private void spawnBonus() {
        String id = nextId("bonus");
        GameObject bonus = factory.createBonus(id);
        objects.put(id, bonus);
    }

    // ---------------------------------------------------------
    // PUBLIC API
    // ---------------------------------------------------------

    public void spawnNextCube() {
        if (!isProcessing && currentCubeId == null) {
            spawnNewCube();
        }
    }

    public String getCurrentCubeId() {
        return currentCubeId;
    }

    public void scrapCube(String id) {
        GameObject obj = objects.get(id);
        if (obj == null) return;
        if (obj.isBonus()) return;
        if (isProcessing) return;

        if (obj.type == CubeType.RATRYS) {
            lastScore = score;
            score += 50;

            bonusCounter++;
            if(bonusCounter >= 5) {
                spawnBonus();
                bonusCounter = 0;
            }
        } else if (obj.type == CubeType.NORMAL) {
            lastScore = score;
            score -= 20;
            bonusCounter = 0;
        }

        obj.shrinkValue = 1.0;
        obj.moving = false;
        isProcessing = true;
    }

    public void evaporateCube(String id) {
        GameObject obj = objects.get(id);
        if (obj == null) return;
        if (obj.isBonus()) return;
        if (isProcessing) return;

        if (obj.type == CubeType.RATRYS) {
            lastScore = score;
            score -= 80;
        } else {
            lastScore = score;
            score += 20;
        }

        Transform t = obj.render.getTransform();
        Vec3 pos = t.getPos();
        obj.moving = true;
        obj.moveTargetX = X_TARGET_ANIMATION;
        obj.shrinkValue = -1.0;
    }


    public int getScore() { return score; }
    public int getLastScoreChange() { return score - lastScore; }
    public boolean isProcessing() { return isProcessing; }

    // ---------------------------------------------------------
    // UPDATE LOOP
    // ---------------------------------------------------------

    @Override
    public void update(double dt) {
        updateRotation(dt);
        updateShrinking(dt);
        updateBonuses(dt);
        updateMovement(dt);
    }

    private void updateRotation(double dt) {
        if (currentCubeId == null) return;

        GameObject cube = objects.get(currentCubeId);
        if (cube == null || cube.isShrinking()) return;

        double rot = (2 * Math.PI / 10) * dt;
        cube.render.getTransform().rotateEuler(new Vec3(rot, 0, 0));
    }

    private void updateShrinking(double dt) {
        if (currentCubeId == null) return;

        GameObject cube = objects.get(currentCubeId);
        if (cube == null || !cube.isShrinking()) return;

        cube.shrinkValue -= 1.5 * dt;
        double y = Math.max(cube.shrinkValue, 0);
        cube.render.getTransform().setSize(new Vec3(1, y, 1));

        if (cube.shrinkValue <= 0) {
            // koniec animacji – cube znika
            objects.remove(currentCubeId);
            currentCubeId = null;
            isProcessing = false;

            // po zakończeniu animacji pojawia się nowy cube
            spawnNewCube();
        }
    }

    private void updateBonuses(double dt) {
        List<String> expired = new ArrayList<>();

        for (GameObject obj : objects.values()) {
            if (!obj.isBonus()) continue;

            obj.lifetime -= dt;
            if (obj.lifetime <= 0) {
                expired.add(obj.id);
            }
        }

        for (String id : expired) {
            objects.remove(id);
        }
    }

    private void updateMovement(double dt) {
        List<String> finished = new ArrayList<>();

        for (GameObject obj : objects.values()) {
            if (!obj.isMoving()) continue;

            Transform t = obj.render.getTransform();
            Vec3 pos = t.getPos();

            double dx = obj.moveTargetX - pos.x;
            double dir = Math.signum(dx);
            double step = obj.moveSpeed * dt;
            if (Math.abs(dx) <= step) {
                pos.x = obj.moveTargetX;
                t.setPos(pos);
                finished.add(obj.id);
            } else {
                pos.x += dir * step;
                t.setPos(pos);
            }
        }

        for (String id : finished) {
            GameObject obj = objects.get(id);
            if (obj == null) continue;
            obj.moving = false;

            if (id.equals(currentCubeId) && Math.abs(obj.moveTargetX - X_TARGET_ANIMATION) < 0.001) {
                objects.remove(id);
                currentCubeId = null;
                spawnNewCube();
            }
        }
    }


    // ---------------------------------------------------------
    // BUILDER INTERFACE
    // ---------------------------------------------------------

    @Override
    public List<String> getObjectIds() {
        return new ArrayList<>(objects.keySet());
    }

    @Override
    public List<RenderingObject> getRenderingObjects() {
        return objects.values().stream()
                .map(o -> o.render)
                .toList();
    }

    @Override
    public RenderingObject buildRenderingObject(String id) {
        GameObject obj = objects.get(id);
        return obj != null ? obj.render : null;
    }

    @Override
    public Transform getObjectTransform(String id) {
        GameObject obj = objects.get(id);
        return obj != null ? obj.render.getTransform() : null;
    }



    private enum CubeType {
        RATRYS,
        NORMAL,
        BONUS
    }

    private class CubeTextureInfo {
        public final String path;
        public final CubeType type;

        public CubeTextureInfo(String path, CubeType type) {
            this.path = path;
            this.type = type;
        }
    }


    public class GameObject {
        public final String id;
        public final CubeType type;
        public final RenderingObject render;

        // animacja zgniatania (scrap)
        public double shrinkValue = -1.0; // < 0 = brak animacji

        // animacja ruchu w osi X (spawn / evaporate)
        public boolean moving = false;
        public double moveTargetX = 0.0;
        public double moveSpeed = 40.0; // jednostki na sekundę

        // bonus lifetime
        public double lifetime = -1.0; // tylko dla BONUS

        public GameObject(String id, CubeType type, RenderingObject render) {
            this.id = id;
            this.type = type;
            this.render = render;
        }

        public boolean isShrinking() {
            return shrinkValue >= 0.0;
        }

        public boolean isMoving() {
            return moving;
        }

        public boolean isBonus() {
            return type == CubeType.BONUS;
        }
    }


    public class ObjectFactory {

        private final Random random = new Random();

        private final CubeTextureInfo[] cubeTextures = {
                new CubeTextureInfo("assets/Piotrostal/ep07_1.jpg", CubeType.RATRYS),
                new CubeTextureInfo("assets/Piotrostal/ep07_2.jpg", CubeType.RATRYS),
                new CubeTextureInfo("assets/Piotrostal/et22_1.jpg", CubeType.RATRYS),
                new CubeTextureInfo("assets/Piotrostal/et22_2.jpg", CubeType.RATRYS),
                new CubeTextureInfo("assets/Piotrostal/ep07_1z.jpg", CubeType.NORMAL),
                new CubeTextureInfo("assets/Piotrostal/ew60_1.jpg", CubeType.RATRYS),
                new CubeTextureInfo("assets/Piotrostal/eu160_1z.jpg", CubeType.NORMAL),
                new CubeTextureInfo("assets/Piotrostal/eu200_1z.jpg", CubeType.NORMAL),
                new CubeTextureInfo("assets/Piotrostal/29we_1z.jpg", CubeType.NORMAL),
                new CubeTextureInfo("assets/Piotrostal/su160_1z.jpg", CubeType.NORMAL),
                new CubeTextureInfo("assets/Piotrostal/ep09_1z.jpg", CubeType.NORMAL),
                new CubeTextureInfo("assets/Piotrostal/ep09_1.jpg", CubeType.RATRYS),
                new CubeTextureInfo("assets/Piotrostal/ed160_1z.jpg", CubeType.NORMAL)
        };

        private final String[] bonusTextures = {
                "assets/Piotrostal/bonus/bonus1.png",
                "assets/Piotrostal/bonus/bonus2.png",
                "assets/Piotrostal/bonus/bonus3.png"
        };

        public GameObject createCube(String id) {
            CubeTextureInfo tex = cubeTextures[random.nextInt(cubeTextures.length)];

            List<CuboidSurface> surfaces = new ArrayList<>();
            for (CuboidFaceType face : CuboidFaceType.values()) {
                surfaces.add(new CuboidSurface(face,
                        new Material(Color.WHITE, TextureManager.getTexture(tex.path))));
            }
            Geometry geo = new CuboidGeometry(surfaces);

            Transform t = new Transform();
            // docelowa pozycja (X losowy, Y/Z losowe)
            Vec3 pos = new Vec3(
                    random.nextDouble() * 6 - 3,
                    random.nextDouble() * 3,
                    random.nextDouble() * 6 - 3
            );
            t.setPos(pos);
            t.setSize(new Vec3(1, 1, 1));
            t.setRot(Quaternion.fromEuler(new Vec3(0, 0, 0)));

            RenderingObject ro = new RenderingObject(geo, t);
            GameObject obj = new GameObject(id, tex.type, ro);

            return obj;
        }

        public GameObject createBonus(String id) {
            String texPath = bonusTextures[random.nextInt(bonusTextures.length)];
            Material mat = new Material(Color.WHITE, TextureManager.getTexture(texPath));
            Geometry geo = new CuboidGeometry(List.of(
                    new CuboidSurface(CuboidFaceType.FRONT, mat)
            ));

            Transform t = new Transform();
            Vec3 pos = new Vec3(
                    random.nextDouble() * 6 - 3,
                    random.nextDouble() * 3,
                    random.nextDouble() * 6 - 3
            );
            t.setPos(pos);
            t.setSize(new Vec3(2, 2, 0.1));
            t.setRot(Quaternion.fromEuler(new Vec3(0, Math.toRadians(10), 0)));

            RenderingObject ro = new RenderingObject(geo, t);
            GameObject obj = new GameObject(id, CubeType.BONUS, ro);
            obj.lifetime = 3.0;

            return obj;
        }

        public double randomTargetX() {
            return random.nextDouble() * 6 - 3;
        }
    }


}

