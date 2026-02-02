package EALiodufiowAMS2.general.layouts;

import EALiodufiowAMS2.engine.Camera;
import EALiodufiowAMS2.engine.Scene;
import EALiodufiowAMS2.engine.rendering.RenderingEngineListener;
import EALiodufiowAMS2.engine.rendering.RenderingMode;
import EALiodufiowAMS2.engine.uiRendering.*;
import EALiodufiowAMS2.general.GameNavigation;
import EALiodufiowAMS2.general.LayoutContext;
import EALiodufiowAMS2.general.settings.*;
import EALiodufiowAMS2.general.util.RenderLoop;
import EALiodufiowAMS2.general.util.RenderLoopListener;
import EALiodufiowAMS2.helpers.Transform;
import EALiodufiowAMS2.helpers.Units;
import EALiodufiowAMS2.helpers.Vec3;
import EALiodufiowAMS2.engine.rendering.RenderingEngine;
import EALiodufiowAMS2.engine.rendering.renderViews.RenderView;
import EALiodufiowAMS2.engine.rendering.renderingObject.RenderingObject;
import EALiodufiowAMS2.game.builders.Builder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

//public class ScenePanel extends JPanel {
//    private final World world;
//    private final List<Builder> builders = new ArrayList<>();
//    private final RenderingEngine renderingEngine;
//    private final RenderView renderView;
//    protected final Camera camera;
//    private Scene scene;
//
////    private SceneOverlayWindow overlayWindow;
////private OverlayLabelWindow fpsOverlay; private OverlayLabelWindow modeOverlay; private OverlayLabelWindow debugOverlay;
//private UiOverlayEngine uiOverlayEngine;
//private JLabel fpsLabel;
//
//    public ScenePanel(World world, Scene scene, int resWidth, int resHeight) {
//        this.world = world;
//        this.scene = scene;
//
//        this.camera = new Camera(new Vec3(resWidth / Units.M_TO_PX, resHeight / Units.M_TO_PX, 0), new Vec3(0, 0, -5), new Vec3(Math.toRadians(0), Math.toRadians(0), Math.toRadians(0)), 75);
//        //this.camera = new Camera(new Vec3(resWidth / Units.M_TO_PX, resHeight / Units.M_TO_PX, 0), new Vec3(0, 0, 0), new Vec3(Math.toRadians(0), Math.toRadians(0), Math.toRadians(0)), 75);
//
//        this.renderingEngine = new RenderingEngine(resWidth, resHeight, RenderingMode.GPU);
//
//        this.renderView = this.renderingEngine.getRenderView();
//        setLayout(new BorderLayout());
//        add(renderView.getComponent(), BorderLayout.CENTER);
//
//       // addHierarchyListener(e -> { if ((e.getChangeFlags() & HierarchyEvent.DISPLAYABILITY_CHANGED) != 0) { if (isDisplayable()) { attachOverlayWindow(); } else { detachOverlayWindow(); } } });
//        //addHierarchyListener(e -> { if ((e.getChangeFlags() & HierarchyEvent.DISPLAYABILITY_CHANGED) != 0) { if (isDisplayable()) { attachOverlays(); } else { detachOverlays(); } } });
//        //addHierarchyListener(e -> { if ((e.getChangeFlags() & HierarchyEvent.DISPLAYABILITY_CHANGED) != 0) { if (isDisplayable()) { attachUiOverlayEngine(); } else { detachUiOverlayEngine(); } } });
//
//        this.uiOverlayEngine = new UiOverlayEngine(this);
//        setUiOverlay(composeOverlay());
//
//
//        this.renderingEngine.setScene(this.scene);
//        this.renderingEngine.setCamera(this.camera);
//
//        setDoubleBuffered(false);
//
//        setBorder(BorderFactory.createLineBorder(Color.RED, 3));
//
//        addComponentListener(new ComponentAdapter() {
//            @Override
//            public void componentResized(ComponentEvent e) {
//                int w = getWidth();
//                int h = getHeight();
//                camera.setSize((double)w / Units.M_TO_PX, (double)h / Units.M_TO_PX);
//                renderingEngine.setBufferSize(w, h);
//                renderView.repaintView();
//
//                System.out.println("ScenePanel resized: " + w + "x" + h);
//
//                //if (overlayWindow != null) { overlayWindow.updateBounds(); }
//               //if (fpsOverlay != null) fpsOverlay.updateBounds(); if (modeOverlay != null) modeOverlay.updateBounds(); if (debugOverlay != null) debugOverlay.updateBounds();
//               // if (uiOverlayEngine != null) { uiOverlayEngine.updateAllBounds(); }
//            }
//        });
//
//    }
//
//    public Scene getScene() { return this.scene; }
//
//    // private void attachOverlayWindow() { if (overlayWindow != null) return; Window owner = SwingUtilities.getWindowAncestor(this); if (owner == null) return; overlayWindow = new SceneOverlayWindow(owner, this); overlayWindow.updateBounds(); overlayWindow.setVisible(true); } private void detachOverlayWindow() { if (overlayWindow != null) { overlayWindow.setVisible(false); overlayWindow.dispose(); overlayWindow = null; } } public void updateFpsOverlay(int fps) { if (overlayWindow != null) { overlayWindow.setFps(fps); } }
//    //private void attachOverlays() { Window owner = SwingUtilities.getWindowAncestor(this); if (owner == null) return; if (fpsOverlay == null) { fpsOverlay = new OverlayLabelWindow( owner, this, OverlayLabelWindow.Anchor.TOP_LEFT, 10, 10 ); fpsOverlay.setText("FPS: --"); fpsOverlay.updateBounds(); } if (modeOverlay == null) { modeOverlay = new OverlayLabelWindow( owner, this, OverlayLabelWindow.Anchor.TOP_RIGHT, 10, 10 ); modeOverlay.setText("Mode: default"); modeOverlay.updateBounds(); } if (debugOverlay == null) { debugOverlay = new OverlayLabelWindow( owner, this, OverlayLabelWindow.Anchor.BOTTOM_LEFT, 10, 10 ); debugOverlay.setText("Debug: off"); debugOverlay.updateBounds(); } } private void detachOverlays() { if (fpsOverlay != null) { fpsOverlay.setVisible(false); fpsOverlay.dispose(); fpsOverlay = null; } if (modeOverlay != null) { modeOverlay.setVisible(false); modeOverlay.dispose(); modeOverlay = null; } if (debugOverlay != null) { debugOverlay.setVisible(false); debugOverlay.dispose(); debugOverlay = null; } } public void updateFps(int fps) { if (fpsOverlay != null) { fpsOverlay.setText("FPS: " + fps); } } public void updateMode(String mode) { if (modeOverlay != null) { modeOverlay.setText("Mode: " + mode); } } public void updateDebug(String text) { if (debugOverlay != null) { debugOverlay.setText("Debug: " + text); } }
//   // private void attachUiOverlayEngine() { if (uiOverlayEngine != null) return; if (scene == null) return; Window owner = SwingUtilities.getWindowAncestor(this); if (owner == null) return; uiOverlayEngine = new UiOverlayEngine(scene, this); } private void detachUiOverlayEngine() { if (uiOverlayEngine != null) { uiOverlayEngine.disposeAll(); uiOverlayEngine = null; } }
//
//    // Pomocnicza metoda do aktualizacji UI z zewnątrz
//    //public void updateUiObject(OverlayElement uiObject) { if (uiOverlayEngine != null) { uiOverlayEngine.updateUiObject(uiObject); } }
//
//    public void setUiOverlay(UiOverlay overlay) { uiOverlayEngine.setOverlay(overlay); }
//    public void refreshOverlayComponent(JComponent component) { uiOverlayEngine.refreshOverlayComponent(component); }
//    private UiOverlay composeOverlay() {
//        UiOverlay overlay = new UiOverlay();
//
//        UiOverlayLayer fpsLayer = overlay.getOrCreateLayer("fps");
//        fpsLabel = new JLabel("FPS: --");
//        fpsLabel.setForeground(Color.WHITE);
//        fpsLabel.setOpaque(false);
//        UiOverlayConstraints fpsConstraints = new UiOverlayConstraints( UiOverlayAnchor.TOP_LEFT, 40, 40, null, null, true );
//        fpsLayer.addElement(new UiOverlayElement(fpsLabel, fpsConstraints));
//
//        UiOverlayLayer testLayer = overlay.getOrCreateLayer("example");
//        JButton centerButton = new JButton("Axample");
//        centerButton.addActionListener(e -> System.out.println("Example"));
//        UiOverlayConstraints buttonConstraints = new UiOverlayConstraints( UiOverlayAnchor.CENTER, 0, 0, null, null, true );
//        testLayer.addElement(new UiOverlayElement(centerButton, buttonConstraints));
//
//        return overlay;
//    }
//    public void updateFpsCount(int fps) {
//        this.fpsLabel.setText(String.valueOf(fps));
//    }
//
//
//
//    public void addRenderer(Builder r) { builders.add(r); }
//
//    public void attachCameraTo(Transform attachedTransform) {
//        camera.attachTo(attachedTransform);
//    }
//
//    public void setCameraFov(int angle) {
//        camera.setFov(angle);
//    }
//    public int getCameraFov() {
//        return camera.getFov();
//    }
//
//    public void stepAndRender(double deltaTime) {
//
//        for (Builder r : builders) {
//            r.update(deltaTime);
//        }
//
//        camera.update();
//
//        List<RenderingObject> objects = new ArrayList<>();
//        for (Builder r : builders) {
//            for (String id : r.getObjectIds()) {
//                boolean visible;
//                try {
//                    visible = r.isVisible(camera, id); // isVisible może być nadpisane przez renderer, ale nie musi i wtedy po prostu obliczamy czy obiekt widoczny.
//                } catch (UnsupportedOperationException ex) {
//                    Transform t = r.getObjectTransform(id);
//                    visible = camera.isInFrustum(t);
//                }
//                //System.out.printf("object \"%s\" visible: %b\n", id, visible);
//                visible = true; // test purposes !!!!
//
//                if (visible) {
//                    objects.add(r.buildRenderingObject(id));
//                }
//            }
//        }
//
//        scene.setObjects(objects);
//
//        renderingEngine.renderFrame();
//        renderView.repaintView();
//
//        //repaint();
//    }
//
////    public void setFpsOverlay() {
////        this.overlayWindow.setFps(((GamePanel) SwingUtilities.getWindowAncestor(this)).currentFps);
////    }
//
//    @Override
//    protected void paintComponent(Graphics g) {
//        super.paintComponent(g);
//
//        //g.setColor(Color.GREEN);
//        //g.drawString("FPS: " + ((GamePanel) SwingUtilities.getWindowAncestor(this)).currentFps, 10, 20);
//
////        BufferedImage img = renderingEngine.getFrameSnapshot(); // should only be used for occasional fetching
////        System.out.println(img);
////        if (img != null) { g.drawImage(img, 0, 0, getWidth(), getHeight(), null); }
//
//    }
//}

// -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

//public abstract class ScenePanel extends JPanel implements SettingsListener {
//
//    protected final SettingsManager settingsManager;
//    protected final GameNavigation navigation;
//
//    private RenderingEngine renderingEngine;
//    private RenderView renderView;
//    private UiOverlayEngine uiOverlayEngine;
//
//    protected Camera camera;
//    protected Scene sceneData;
//
//    protected boolean paused = false;
//
//    private javax.swing.Timer renderTimer;
//
//    private long lastFrameNanos = -1L;
//    private double fps = 0.0;
//
//    private JLabel fpsLabel;
//    private JLabel gpuInfoLabel;
//    private JLabel camInfoLabel;
//
//    protected ScenePanel(LayoutContext layoutContext) {
//        this.settingsManager = layoutContext.getSettingsManager();
//        this.navigation = layoutContext.getNavigation();
//
//        setLayout(new BorderLayout());
//
//        settingsManager.addListener(this);
//        initKeyBindings();
//
//
//        addComponentListener(new ComponentAdapter() {
//            @Override
//            public void componentResized(ComponentEvent e) {
//                int w = getWidth();
//                int h = getHeight();
//                camera.setSize((double)w / Units.M_TO_PX, (double)h / Units.M_TO_PX);
//                renderingEngine.resize(w, h);
//                renderView.repaintView();
//
//                System.out.println("ScenePanel resized: " + w + "x" + h);
//
//            }
//        });
//
//    }
//
//
//    public void onShown() {
//        startLoop();
//    }
//    public void onHidden() {
//        stopLoop();
//        uiOverlayEngine.dispose();
//    }
//
//    public void disposeScene() {
//        stopLoop();
//        settingsManager.removeListener(this);
//        if (renderingEngine != null) {
//            //renderingEngine.dispose(); nie ma takiej funkcji
//        }
//    }
//
//
//    private void startLoop() {
//        if (renderTimer != null && renderTimer.isRunning()) return;
//
//        lastFrameNanos = System.nanoTime();
//        int delay = 15;
//
//        renderTimer = new javax.swing.Timer(delay, e -> {
//            long now = System.nanoTime();
//            double delta = (now - lastFrameNanos) / 1_000_000_000.0;
//            if (delta <= 0) {
//                lastFrameNanos = now;
//                return;
//            }
//            lastFrameNanos = now;
//
//            stepAndRender(delta);
//            updateFps(delta);
//            updateFpsOverlay();
//            updateCamInfoOverlay();
//        });
//        renderTimer.setCoalesce(true);
//        renderTimer.start();
//    }
//
//    private void stopLoop() {
//        if (renderTimer != null) {
//            renderTimer.stop();
//            renderTimer = null;
//        }
//    }
//
//    private void stepAndRender(double deltaTime) {
//        if (!paused) {
//            updateScene(deltaTime);
//            camera.update();
//        }
//        if (renderingEngine != null) {
//            renderingEngine.renderFrame();
//        }
//        if (renderView != null) {
//            renderView.repaintView();
//        }
//    }
//
//    private void updateFps(double deltaTime) {
//        double instant = 1.0 / deltaTime;
//        fps = fps * 0.9 + instant * 0.1;
//    }
//
//    private void updateFpsOverlay() {
//        if (fpsLabel != null) {
//            fpsLabel.setText(String.format("FPS: %.1f", fps));
//        }
//    }
//    private void updateCamInfoOverlay() {
//        if (camInfoLabel != null && camera != null) {
//            camInfoLabel.setText("Pos: " + camera.getTransform().toString());
//        }
//    }
//
//
//    @Override
//    public void onSettingsChanged(Settings newSettings) {
//        GraphicsSettings g = newSettings.getGraphics();
//        applyGraphicsSettings(g);
//    }
//
//    protected void applyGraphicsSettings(GraphicsSettings g) {
//        boolean needReinit = false;
//
//        if (renderingEngine == null) {
//            needReinit = true;
//        } else if (renderingEngine.getRenderingMode() != g.getRenderingMode()) {
//            needReinit = true;
//        } else if (renderingEngine.getWidth() != g.getResolutionWidth() || renderingEngine.getHeight() != g.getResolutionHeight()) {
//            needReinit = true;
//        }
//
//        if (needReinit) {
//            reinitRenderingEngine(g);
//        }
//
//        if (camera != null) {
//            if(camera.getFov() != g.getFov()){
//                camera.setFov(g.getFov());
//            }
//            if(camera.getTransform().getSize().x != g.getResolutionWidth() / Units.M_TO_PX || camera.getTransform().getSize().y != g.getResolutionHeight() / Units.M_TO_PX) {
//                camera.setSize(g.getResolutionWidth() / Units.M_TO_PX, g.getResolutionHeight() / Units.M_TO_PX);
//            }
//        }
//
//        if (uiOverlayEngine != null && fpsLabel != null) {
//            fpsLabel.setVisible(g.isShowFps());
//        }
//    }
//
//    protected void reinitRenderingEngine(GraphicsSettings g) {
//        //if (renderingEngine != null) renderingEngine.dispose(); nie ma takiej funkcji
//        if (uiOverlayEngine != null) uiOverlayEngine.dispose();
//
//        this.uiOverlayEngine = new UiOverlayEngine(this);
//
//        this.renderingEngine = new RenderingEngine(
//                g.getResolutionWidth(),
//                g.getResolutionHeight(),
//                g.getRenderingMode()
//        );
//
//        this.renderingEngine.setListener(new RenderingEngineListener() {
//            @Override public void onBackendInitialized() {
//                SwingUtilities.invokeLater(() -> {
//                    gpuInfoLabel.setText("GPU: " + renderingEngine.getCurrentGpuInfo().renderer());
//                });
//            }
//        });
//
//        this.renderView = renderingEngine.getRenderView();
//
//        removeAll();
//        setLayout(new BorderLayout());
//        add(renderView.getComponent(), BorderLayout.CENTER);
//
//        if (sceneData != null) {
//            System.out.println(sceneData.getObjects());
//            renderingEngine.setScene(sceneData);
//        }
//        renderingEngine.setCamera(camera);
//
//        initOverlay(g);
//
//        revalidate();
//        repaint();
//
//    }
//
//    protected abstract void composeUiOverlay(UiOverlay overlay);
//
//    protected void initOverlay(GraphicsSettings g) {
//        UiOverlay overlay = new UiOverlay();
//
//        UiOverlayLayer hudLayer = overlay.getOrCreateLayer("hud");
//        fpsLabel = new JLabel("FPS: --");
//        fpsLabel.setForeground(Color.GREEN);
//        UiOverlayConstraints fpsConstraints = new UiOverlayConstraints(
//                UiOverlayAnchor.TOP_RIGHT, -10, 10, null, null, true
//        );
//        hudLayer.addElement(new UiOverlayElement(fpsLabel, fpsConstraints));
//        fpsLabel.setVisible(g.isShowFps());
//
//        UiOverlayLayer debugInfoLayer = overlay.getOrCreateLayer("debugInfoLayer");
//        if(renderingEngine != null && renderingEngine.getRenderingMode() == RenderingMode.GPU){
//            gpuInfoLabel = new JLabel("GPU: --");
//            gpuInfoLabel.setForeground(Color.WHITE);
//            UiOverlayConstraints gpuInfoLabelConstraints = new UiOverlayConstraints(
//                    UiOverlayAnchor.TOP_RIGHT, -10, 50, null, null, true
//            );
//            debugInfoLayer.addElement(new UiOverlayElement(gpuInfoLabel, gpuInfoLabelConstraints));
//            gpuInfoLabel.setVisible(g.isShowDebugInfo());
//        }
//
//        camInfoLabel = new JLabel("Pos: --");
//        camInfoLabel.setForeground(Color.WHITE);
//        camInfoLabel.setBackground(new Color(0x000000aa));
//        UiOverlayConstraints camInfoLabelConstraints = new UiOverlayConstraints(
//                UiOverlayAnchor.TOP_RIGHT, -10, 20, null, null, true
//        );
//        hudLayer.addElement(new UiOverlayElement(camInfoLabel, camInfoLabelConstraints));
//        camInfoLabel.setVisible(g.isShowDebugInfo());
//
//
//        composeUiOverlay(overlay);
//
//        uiOverlayEngine.setOverlay(overlay);
//    }
//
//
//    protected void setPaused(boolean paused) {
//        this.paused = paused;
//        onPauseStateChanged(paused);
//    }
//
//    protected void togglePause() {
//        setPaused(!paused);
//    }
//
//    protected void onPauseStateChanged(boolean paused) {
//    }
//
//    private void initKeyBindings() {
//        InputMap im = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
//        ActionMap am = getActionMap();
//
//        im.put(KeyStroke.getKeyStroke("ESCAPE"), "togglePause");
//        am.put("togglePause", new AbstractAction() {
//            @Override
//            public void actionPerformed(java.awt.event.ActionEvent e) {
//                onEscapePressed();
//            }
//        });
//    }
//
//    protected void onEscapePressed() {
//        togglePause();
//    }
//
//
//    protected abstract void updateScene(double deltaTime);
//}

//-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

 public abstract class ScenePanel extends JPanel implements SettingsListener, RenderLoopListener {

      protected final SettingsManager settingsManager;
      protected final GameNavigation navigation;

      private SettingsApplier settingsApplier;

      private RenderingEngine renderingEngine;
      private RenderView renderView;
      private UiOverlayEngine uiOverlayEngine;

      protected Camera camera;
      protected Scene sceneData;

      protected boolean paused = false;
      private RenderLoop renderLoop;

      private double ss = 1.0;

      private UiOverlay overlay;
      private UiOverlayElement fpsUiOverlayElement;
      private UiOverlayElement gpuInfoUiOverlayElement;
      private UiOverlayElement camInfoUiOverlayElement;

      private boolean isReducedMultithreading = true;

      protected ScenePanel(LayoutContext layoutContext) {
          this.settingsManager = layoutContext.getSettingsManager();
          this.navigation = layoutContext.getNavigation();

          setLayout(new BorderLayout());

          settingsManager.addListener(this);
          initKeyBindings();


          addComponentListener(new ComponentAdapter() {
              @Override
              public void componentResized(ComponentEvent e) {
                  GraphicsSettings g = settingsManager.getCurrent().getGraphics();

                  if (g.getRenderResolutionMode() == ResolutionMode.AUTO) {
                      int w = getWidth();
                      int h = getHeight();

                      renderingEngine.resize(w, h);
                      camera.setSize(w / Units.M_TO_PX, h / Units.M_TO_PX);
                  }

                  renderView.repaintView();
              }
          });

          this.renderLoop = new RenderLoop(this, 100);

          this.settingsApplier = new SettingsApplier(this, renderLoop);

      }


      public void onShown() {
          if (renderLoop != null && !renderLoop.isRunning()) renderLoop.start();
          recreateOverlay();
          this.settingsApplier.apply(settingsManager.getCurrent());
      }
      public void onHidden() {
          if (renderLoop != null && renderLoop.isRunning()) renderLoop.stop();
          disposeOverlay();
      }
      public void dispose() {
          if (renderLoop != null && renderLoop.isRunning()) renderLoop.stop();
          settingsManager.removeListener(this);
          disposeOverlay();
      }

    @Override
    public void onSettingsChanged(Settings newSettings) {
        if (settingsApplier != null) settingsApplier.apply(newSettings);
    }

      @Override public void onFrame(double deltaTimeSeconds) {
          stepAndRender(deltaTimeSeconds);
      }

      private void stepAndRender(double deltaTime) {
          //System.out.println(deltaTime);

          if (!paused) {
              updateScene(deltaTime*this.ss);
              if (camera != null) camera.update();
          }
          if (renderingEngine != null) renderingEngine.renderFrame();
          if (renderView != null){
              if(isReducedMultithreading) SwingUtilities.invokeLater(renderView::repaintView);
              else renderView.repaintView();
          }

          SwingUtilities.invokeLater(() -> {
              updateFpsOverlay();
              updateCamInfoOverlay();
          });
      }

      protected void reinitRenderingEngine(GraphicsSettings g) {

          this.renderingEngine = new RenderingEngine(
                  g.getRenderWidth(),
                  g.getRenderHeight(),
                  g.getRenderingMode()
          );

          this.renderingEngine.setListener(new RenderingEngineListener() {
              @Override public void onBackendInitialized() {
                  SwingUtilities.invokeLater(() -> {
                      if(gpuInfoUiOverlayElement == null) return;
                      ((JLabel) gpuInfoUiOverlayElement.getComponent()).setText("GPU: " + renderingEngine.getCurrentGpuInfo().renderer());
                      uiOverlayEngine.updateElement(gpuInfoUiOverlayElement);
                  });
              }
          });

          this.renderView = renderingEngine.getRenderView();

          removeAll();
          setLayout(new BorderLayout());
          add(renderView.getComponent(), BorderLayout.CENTER);

          if (sceneData != null) renderingEngine.setScene(sceneData);
          renderingEngine.setCamera(camera);



          revalidate();
          repaint();

      }

      private void recreateOverlay() {
          disposeOverlay();
          this.uiOverlayEngine = new UiOverlayEngine(this);
          GraphicsSettings g = settingsManager.getCurrent().getGraphics();
          UiSettings u = settingsManager.getCurrent().getUi();
          initOverlay(g, u);
      }
      private void disposeOverlay() {
          if (uiOverlayEngine != null) {
              uiOverlayEngine.dispose();
              uiOverlayEngine = null;
          }
      }

      protected void initOverlay(GraphicsSettings g, UiSettings u) {
          overlay = new UiOverlay();

          UiOverlayLayer hudLayer = overlay.getOrCreateLayer("hud");
          JLabel fpsLabel = new JLabel("FPS: --");
          fpsLabel.setForeground(Color.GREEN);
          UiOverlayConstraints fpsConstraints = new UiOverlayConstraints(
                  UiOverlayAnchor.TOP_RIGHT, -10, 10, null, null, true
          );
          fpsUiOverlayElement = new UiOverlayElement(fpsLabel, fpsConstraints);
          hudLayer.addElement(fpsUiOverlayElement);
          fpsLabel.setVisible(u.isShowFps());

          UiOverlayLayer debugInfoLayer = overlay.getOrCreateLayer("debugInfoLayer");
          if(renderingEngine != null && renderingEngine.getRenderingMode() == RenderingMode.GPU){
              JLabel gpuInfoLabel = new JLabel("GPU: --");
              gpuInfoLabel.setForeground(Color.WHITE);
              UiOverlayConstraints gpuInfoLabelConstraints = new UiOverlayConstraints(
                      UiOverlayAnchor.TOP_RIGHT, -10, 50, null, null, true
              );
              gpuInfoUiOverlayElement = new UiOverlayElement(gpuInfoLabel, gpuInfoLabelConstraints);
              debugInfoLayer.addElement(gpuInfoUiOverlayElement);
          }

          JLabel camInfoLabel = new JLabel("Cam: --");
          camInfoLabel.setForeground(Color.WHITE);
          UiOverlayConstraints camInfoLabelConstraints = new UiOverlayConstraints(
                  UiOverlayAnchor.TOP_RIGHT, -10, 65, null, null, true
          );
          camInfoUiOverlayElement = new UiOverlayElement(camInfoLabel, camInfoLabelConstraints);
          debugInfoLayer.addElement(camInfoUiOverlayElement);
          debugInfoLayer.setVisible(u.isShowDebugInfo());


          composeUiOverlay(overlay);

          uiOverlayEngine.setOverlay(overlay);
      }

    private void updateFpsOverlay() {
        if (uiOverlayEngine != null && fpsUiOverlayElement != null && renderLoop != null) {
            ((JLabel) fpsUiOverlayElement.getComponent()).setText(String.format("FPS: %.1f", renderLoop.getCurrentFps()));
            uiOverlayEngine.updateElement(fpsUiOverlayElement);
        }
    }
    private void updateCamInfoOverlay() {
        if (uiOverlayEngine != null && camInfoUiOverlayElement != null && camera != null) {
            ((JLabel) camInfoUiOverlayElement.getComponent()).setText("Cam: " + camera.getTransform().toString());
            uiOverlayEngine.updateElement(camInfoUiOverlayElement);
        }
    }


      protected void setPaused(boolean paused) {
          this.paused = paused;
          onPauseStateChanged(paused);
      }

      protected void togglePause() {
          setPaused(!paused);
      }

      protected void onPauseStateChanged(boolean paused) {
      }

      private void initKeyBindings() {
          InputMap im = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
          ActionMap am = getActionMap();

          im.put(KeyStroke.getKeyStroke("ESCAPE"), "togglePause");
          am.put("togglePause", new AbstractAction() {
              @Override
              public void actionPerformed(java.awt.event.ActionEvent e) {
                  onEscapePressed();
              }
          });
      }

      protected void onEscapePressed() {
          togglePause();
      }


      protected abstract void updateScene(double deltaTime);
      protected abstract void composeUiOverlay(UiOverlay overlay);


    private final class SettingsApplier {

        private final ScenePanel panel;
        private final RenderLoop renderLoop;

        public SettingsApplier(ScenePanel panel, RenderLoop renderLoop) {
            this.panel = panel;
            this.renderLoop = renderLoop;
        }

        public void apply(Settings newSettings) {
            GraphicsSettings g = newSettings.getGraphics();
            GameplaySettings ą = newSettings.getGameplay();
            UiSettings ui = newSettings.getUi();

            if (requiresReinit(g)) reinitRenderingEngine(g);

            applyResolutionMode(g);
            applyGameplaySettings(ą);
            applyCameraSettings(g);
            applyUiSettings(ui);
            applyFpsLimit(g);

            panel.isReducedMultithreading = g.getReducedMultithreading();
        }

        private boolean requiresReinit(GraphicsSettings g) {
            if (renderingEngine == null) return true;

            if (renderingEngine.getRenderingMode() != g.getRenderingMode()) {
                return true;
            }

            if (g.getRenderResolutionMode() == ResolutionMode.FIXED) {
                if (renderingEngine.getWidth() != g.getRenderWidth() || renderingEngine.getHeight() != g.getRenderHeight()) {
                    return true;
                }
            }

            return false;
        }

        private void applyGameplaySettings(GameplaySettings ą) {
            panel.ss = ą.getSimulationSpeed();
        }

        private void applyResolutionMode(GraphicsSettings g) {
            if (renderingEngine == null) return;
            if (g.getRenderResolutionMode() == ResolutionMode.AUTO) renderingEngine.resize(getWidth(), getHeight());
            else renderingEngine.resize(g.getRenderWidth(), g.getRenderHeight());
        }

        private void applyCameraSettings(GraphicsSettings g) {
            Camera cam = camera;
            if (cam == null) return;

            if(cam.getFov() != g.getFov()) cam.setFov(g.getFov());

            if (g.getRenderResolutionMode() == ResolutionMode.AUTO) {
                cam.setSize(getWidth() / Units.M_TO_PX, getHeight() / Units.M_TO_PX);
            } else {
                cam.setSize(g.getRenderWidth() / Units.M_TO_PX, g.getRenderHeight() / Units.M_TO_PX);
            }
        }

        private void applyUiSettings(UiSettings ui) {
            if (fpsUiOverlayElement != null) {
                fpsUiOverlayElement.getComponent().setVisible(ui.isShowFps());
            }
            if(overlay != null) overlay.getLayer("debugInfoLayer").setVisible(ui.isShowDebugInfo());
        }

        private void applyFpsLimit(GraphicsSettings g) {
            if (renderLoop != null) renderLoop.setMaxFps(g.getFpsLimit());
        }
    }
  }
