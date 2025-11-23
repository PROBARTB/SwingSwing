package EALiodufiowAMS2.rendering.renderers;

import EALiodufiowAMS2.helpers.*;
import EALiodufiowAMS2.rendering.FaceType;
import EALiodufiowAMS2.rendering.Surface;
import EALiodufiowAMS2.rendering.TextureManager;
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
        surfaces.add(new Surface(FaceType.FRONT, TextureManager.getTexture("assets\\vehicles\\trams\\PesaSwing\\120Na-section-1.png"), Color.RED));
        //surfaces.add(new Surface(FaceType.BACK, null, Color.GREEN));
        surfaces.add(new Surface(FaceType.TOP, null, Color.BLUE));
        surfaces.add(new Surface(FaceType.BOTTOM, null, Color.YELLOW));
        surfaces.add(new Surface(FaceType.LEFT, TextureManager.getTexture("assets\\vehicles\\trams\\PesaSwing\\120Na-section-1.png"), Color.CYAN));
        surfaces.add(new Surface(FaceType.RIGHT, null, Color.MAGENTA));

        this.rotatingCube = new Cuboid(new Transform(), surfaces);
        this.transform = this.rotatingCube.getTransform();

        this.transform.setPos(new Vec3(10, 0, 5));
        this.transform.setSize(new Vec3(1, 1, 1));
        this.transform.setDir(new Vec3(0, 0, 0));
    }

    @Override
    public void update(double deltaTime) {
        //System.out.printf("RendererTest3d update %f\n", deltaTime);
        // deltaTime w sekundach
        double rotationSpeed = 2 * Math.PI / 10; // radianów na sekundę
        double deltaRotation = rotationSpeed * deltaTime;

        // pobierz aktualny kierunek
        Vec3 dir = transform.getDir();

        // obracamy np. wokół osi Y (yaw)
        dir = new Vec3(dir.x + deltaRotation, dir.y, dir.z);

        // żeby nie rosło w nieskończoność, modulo 360
        if (dir.y >= 360.0) {
            dir = new Vec3(dir.x, dir.y - 360.0, dir.z);
        }

        transform.setDir(dir);
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
