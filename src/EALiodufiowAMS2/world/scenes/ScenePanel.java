package EALiodufiowAMS2.world.scenes;

import EALiodufiowAMS2.helpers.Transform;
import EALiodufiowAMS2.helpers.Units;
import EALiodufiowAMS2.helpers.Vec3;
import EALiodufiowAMS2.rendering.RenderingEngine;
import EALiodufiowAMS2.rendering.graphicsRenderers.CpuBackend;
import EALiodufiowAMS2.rendering.renderViews.RenderView;
import EALiodufiowAMS2.rendering.renderingObject.RenderingObject;
import EALiodufiowAMS2.rendering.builders.Builder;
import EALiodufiowAMS2.uiRendering.UiObject;
import EALiodufiowAMS2.uiRendering.UiOverlayEngine;
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
    private final List<Builder> builders = new ArrayList<>();
    private final RenderingEngine renderingEngine;
    private final RenderView renderView;
    protected final Camera camera;
    private Scene scene;

//    private SceneOverlayWindow overlayWindow;
//private OverlayLabelWindow fpsOverlay; private OverlayLabelWindow modeOverlay; private OverlayLabelWindow debugOverlay;
private UiOverlayEngine uiOverlayEngine;

    public ScenePanel(World world, Scene scene, int resWidth, int resHeight) {
        this.world = world;
        this.scene = scene;

        this.camera = new Camera(new Vec3(resWidth / Units.M_TO_PX, resHeight / Units.M_TO_PX, 0), new Vec3(0, 0, -5), new Vec3(Math.toRadians(0), Math.toRadians(0), Math.toRadians(0)), 75);
        //this.camera = new Camera(new Vec3(resWidth / Units.M_TO_PX, resHeight / Units.M_TO_PX, 0), new Vec3(0, 0, 0), new Vec3(Math.toRadians(0), Math.toRadians(0), Math.toRadians(0)), 75);

        this.renderingEngine = new RenderingEngine(resWidth, resHeight, true);

        this.renderView = this.renderingEngine.getRenderView();
        setLayout(new BorderLayout());
        add(renderView.getComponent(), BorderLayout.CENTER);

       // addHierarchyListener(e -> { if ((e.getChangeFlags() & HierarchyEvent.DISPLAYABILITY_CHANGED) != 0) { if (isDisplayable()) { attachOverlayWindow(); } else { detachOverlayWindow(); } } });
        //addHierarchyListener(e -> { if ((e.getChangeFlags() & HierarchyEvent.DISPLAYABILITY_CHANGED) != 0) { if (isDisplayable()) { attachOverlays(); } else { detachOverlays(); } } });
        addHierarchyListener(e -> { if ((e.getChangeFlags() & HierarchyEvent.DISPLAYABILITY_CHANGED) != 0) { if (isDisplayable()) { attachUiOverlayEngine(); } else { detachUiOverlayEngine(); } } });

        this.renderingEngine.setScene(this.scene);
        this.renderingEngine.setCamera(this.camera);

        setDoubleBuffered(false);

        setBorder(BorderFactory.createLineBorder(Color.RED, 3));

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int w = getWidth();
                int h = getHeight();
                camera.setSize((double)w / Units.M_TO_PX, (double)h / Units.M_TO_PX);
                renderingEngine.setBufferSize(w, h);

                System.out.println("ScenePanel resized: " + w + "x" + h);

                //if (overlayWindow != null) { overlayWindow.updateBounds(); }
               //if (fpsOverlay != null) fpsOverlay.updateBounds(); if (modeOverlay != null) modeOverlay.updateBounds(); if (debugOverlay != null) debugOverlay.updateBounds();
                if (uiOverlayEngine != null) { uiOverlayEngine.updateAllBounds(); }
            }
        });

    }

    public Scene getScene() { return this.scene; }

    // private void attachOverlayWindow() { if (overlayWindow != null) return; Window owner = SwingUtilities.getWindowAncestor(this); if (owner == null) return; overlayWindow = new SceneOverlayWindow(owner, this); overlayWindow.updateBounds(); overlayWindow.setVisible(true); } private void detachOverlayWindow() { if (overlayWindow != null) { overlayWindow.setVisible(false); overlayWindow.dispose(); overlayWindow = null; } } public void updateFpsOverlay(int fps) { if (overlayWindow != null) { overlayWindow.setFps(fps); } }
    //private void attachOverlays() { Window owner = SwingUtilities.getWindowAncestor(this); if (owner == null) return; if (fpsOverlay == null) { fpsOverlay = new OverlayLabelWindow( owner, this, OverlayLabelWindow.Anchor.TOP_LEFT, 10, 10 ); fpsOverlay.setText("FPS: --"); fpsOverlay.updateBounds(); } if (modeOverlay == null) { modeOverlay = new OverlayLabelWindow( owner, this, OverlayLabelWindow.Anchor.TOP_RIGHT, 10, 10 ); modeOverlay.setText("Mode: default"); modeOverlay.updateBounds(); } if (debugOverlay == null) { debugOverlay = new OverlayLabelWindow( owner, this, OverlayLabelWindow.Anchor.BOTTOM_LEFT, 10, 10 ); debugOverlay.setText("Debug: off"); debugOverlay.updateBounds(); } } private void detachOverlays() { if (fpsOverlay != null) { fpsOverlay.setVisible(false); fpsOverlay.dispose(); fpsOverlay = null; } if (modeOverlay != null) { modeOverlay.setVisible(false); modeOverlay.dispose(); modeOverlay = null; } if (debugOverlay != null) { debugOverlay.setVisible(false); debugOverlay.dispose(); debugOverlay = null; } } public void updateFps(int fps) { if (fpsOverlay != null) { fpsOverlay.setText("FPS: " + fps); } } public void updateMode(String mode) { if (modeOverlay != null) { modeOverlay.setText("Mode: " + mode); } } public void updateDebug(String text) { if (debugOverlay != null) { debugOverlay.setText("Debug: " + text); } }
    private void attachUiOverlayEngine() { if (uiOverlayEngine != null) return; if (scene == null) return; Window owner = SwingUtilities.getWindowAncestor(this); if (owner == null) return; uiOverlayEngine = new UiOverlayEngine(scene, this); } private void detachUiOverlayEngine() { if (uiOverlayEngine != null) { uiOverlayEngine.disposeAll(); uiOverlayEngine = null; } }

    // Pomocnicza metoda do aktualizacji UI z zewnątrz
    public void updateUiObject(UiObject uiObject) { if (uiOverlayEngine != null) { uiOverlayEngine.updateUiObject(uiObject); } }



    public void addRenderer(Builder r) { builders.add(r); }

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

        for (Builder r : builders) {
            r.update(deltaTime);
        }

        camera.update();

        List<RenderingObject> objects = new ArrayList<>();
        for (Builder r : builders) {
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

        scene.setObjects(objects);

        renderingEngine.renderFrame();
        renderView.repaintView();

        //repaint();
    }

//    public void setFpsOverlay() {
//        this.overlayWindow.setFps(((GamePanel) SwingUtilities.getWindowAncestor(this)).currentFps);
//    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        //g.setColor(Color.GREEN);
        //g.drawString("FPS: " + ((GamePanel) SwingUtilities.getWindowAncestor(this)).currentFps, 10, 20);

    }
}

