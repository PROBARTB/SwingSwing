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
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

public class ScenePanel extends JPanel {
    private final World world;
    private final List<Renderer> renderers = new ArrayList<>();
    private final RenderingEngine renderingEngine;
    protected final Camera camera;

    private List<RenderingObject> currentFrameObjects = new ArrayList<>();

    public ScenePanel(World world, int resWidth, int resHeight) {
        this.world = world;

        this.renderingEngine = new RenderingEngine(resWidth, resHeight);

        this.camera = new Camera(new Vec3(resWidth / Units.M_TO_PX, resHeight / Units.M_TO_PX, 0), new Vec3(0, 1, -5), new Vec3(Math.toRadians(0), Math.toRadians(0), Math.toRadians(10)), 75);
        this.renderingEngine.setCamera(this.camera);
        setDoubleBuffered(true);

        setBorder(BorderFactory.createLineBorder(Color.RED, 3));

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int w = getWidth();
                int h = getHeight();
                camera.setSize((double)w / Units.M_TO_PX, (double)h / Units.M_TO_PX);
                renderingEngine.setBufferSize(w, h);

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

    public void stepAndRender(double deltaTime) {

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

        BufferedImage frame = renderingEngine.getFrameBuffer();
        g.drawImage(frame, 0, 0, getWidth(), getHeight(), null);

        g.setColor(Color.GREEN);
        g.drawString("FPS: " + ((GamePanel) SwingUtilities.getWindowAncestor(this)).currentFps, 10, 20);
    }
}

