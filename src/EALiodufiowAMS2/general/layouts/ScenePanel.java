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


 public abstract class ScenePanel extends JPanel implements SettingsListener, RenderLoopListener {

      protected final SettingsManager settingsManager;
      protected final GameNavigation navigation;

      private SettingsApplier settingsApplier;

      private RenderingEngine renderingEngine;
      private RenderView renderView;
      protected UiOverlayEngine uiOverlayEngine;

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
