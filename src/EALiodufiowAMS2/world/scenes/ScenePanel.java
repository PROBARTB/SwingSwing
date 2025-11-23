package EALiodufiowAMS2.world.scenes;

import EALiodufiowAMS2.helpers.Transform;
import EALiodufiowAMS2.helpers.Units;
import EALiodufiowAMS2.helpers.Vec3;
import EALiodufiowAMS2.rendering.RenderingEngine;
import EALiodufiowAMS2.rendering.renderingObjects.RenderingObject;
import EALiodufiowAMS2.rendering.renderers.Renderer;
import EALiodufiowAMS2.world.Camera;
import EALiodufiowAMS2.world.GamePanel;
import EALiodufiowAMS2.world.World;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class ScenePanel extends JPanel {
    private final World world;
    private final List<Renderer> renderers = new ArrayList<>();
    private final RenderingEngine renderingEngine = new RenderingEngine();
    protected final Camera camera;

    private List<RenderingObject> currentFrameObjects = new ArrayList<>();
    private long lastNanoTime = System.nanoTime();

    public ScenePanel(World world, double viewportWidth, double viewportHeight) {
        this.world = world;
        this.camera = new Camera(new Vec3(viewportWidth / Units.M_TO_PX, viewportHeight / Units.M_TO_PX, 0), new Vec3(0, 0.5, -5), new Vec3(0, 0, 0), 75);
        this.renderingEngine.setCamera(this.camera);
        setDoubleBuffered(true);

        setBorder(BorderFactory.createLineBorder(Color.RED, 3));

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                double w = (double)getWidth();
                double h = (double)getHeight();
                camera.setSize(w / Units.M_TO_PX, h / Units.M_TO_PX);

                System.out.println("ScenePanel resized: " + w + "x" + h);
            }
        });

    }

    public void addRenderer(Renderer r) { renderers.add(r); }

    public void attachCameraTo(Transform attachedTransform) {
        camera.attachTo(attachedTransform);
    }

    public void setCameraFov(int angle) {
        camera.setFov(angle);
    }
    public int getCameraFov() {
        return camera.getFov();
    }

    public void stepAndRender() {
        long now = System.nanoTime();
        double deltaTime = (now - lastNanoTime) / 1_000_000_000.0;
        lastNanoTime = now;

        for (Renderer r : renderers) {
            r.update(deltaTime);
        }

        camera.update();

        List<RenderingObject> objects = new ArrayList<>();
        for (Renderer r : renderers) {
            for (String id : r.getObjectIds()) {
                boolean visible;
                try {
                    visible = r.isVisible(camera, id); // isVisible może być nadpisane przez renderer, ale nie musi i wtedy po prostu obliczamy czy obiekt widoczny.
                } catch (UnsupportedOperationException ex) {
                    Transform t = r.getObjectTransform(id);
                    visible = camera.isInFrustum(t);
                }
                //System.out.printf("object \"%s\" visible: %b\n", id, visible);
                visible = true; // test purposes !!!!

                if (visible) {
                    objects.add(r.buildRenderingObject(id));
                }
            }
        }

        currentFrameObjects = objects;
        renderingEngine.setObjects(objects);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        renderingEngine.update((Graphics2D) g);

        g.setColor(Color.BLACK);
        g.drawString("FPS: " + ((GamePanel) SwingUtilities.getWindowAncestor(this)).currentFps, 10, 20);
    }
}

