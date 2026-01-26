package EALiodufiowAMS2.general.scene.scenes;


import EALiodufiowAMS2.engine.Camera;
import EALiodufiowAMS2.engine.Scene;
import EALiodufiowAMS2.engine.rendering.renderingObject.Material;
import EALiodufiowAMS2.engine.rendering.renderingObject.RenderingObject;
import EALiodufiowAMS2.engine.uiRendering.*;
import EALiodufiowAMS2.general.GameNavigation;
import EALiodufiowAMS2.general.LayoutContext;
import EALiodufiowAMS2.general.components.GameplayPauseMenu;
import EALiodufiowAMS2.general.layouts.ScenePanel;
import EALiodufiowAMS2.general.settings.GraphicsSettings;
import EALiodufiowAMS2.general.settings.Settings;
import EALiodufiowAMS2.general.settings.SettingsManager;
import EALiodufiowAMS2.general.world.GameplayWorld;
import EALiodufiowAMS2.helpers.Units;
import EALiodufiowAMS2.helpers.Vec3;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class GameplayScenePanel extends ScenePanel {

    private final GameplayWorld world;

    private UiOverlayLayer pauseLayer;
    private GameplayPauseMenu pauseMenu;

    public GameplayScenePanel(LayoutContext layoutContext, GameplayWorld world) {
        super(layoutContext);
        this.world = world;

        GraphicsSettings g = settingsManager.getCurrent().getGraphics();

        this.camera = new Camera(
                new Vec3(g.getRenderWidth() / Units.M_TO_PX, g.getRenderHeight() / Units.M_TO_PX, 0),
                new Vec3(0, 0, -5),
                new Vec3(Math.toRadians(0), Math.toRadians(0), Math.toRadians(0)),
                75);
        this.sceneData = new Scene(new Material(new Color(0xff5c6e7d, true)));


        reinitRenderingEngine(g);
    }

    @Override
    protected void updateScene(double deltaTime) {
        if (!paused) {
            world.update(deltaTime);
        }
        List<RenderingObject> objects = world.getRenderingObjects();
        sceneData.setObjects(objects);
    }

    @Override
    protected void composeUiOverlay(UiOverlay overlay){
        composeUiOverlayPauseLayer(overlay);
    }

    private void composeUiOverlayPauseLayer(UiOverlay overlay) {
        pauseLayer = overlay.getOrCreateLayer("pause");

        pauseMenu = new GameplayPauseMenu(
                () -> setPaused(false),
                navigation::openSettings,
                navigation::returnToMainMenu
        );

        UiOverlayConstraints constraints = new UiOverlayConstraints( UiOverlayAnchor.CENTER, 0, 0, null, null, true );
        pauseLayer.addElement(new UiOverlayElement(pauseMenu, constraints));
        pauseMenu.setVisible(false);
    }

    @Override
    protected void onPauseStateChanged(boolean paused) {
        if (pauseMenu != null) {
            pauseMenu.setVisible(paused);
        }
    }

    @Override
    protected void onEscapePressed() {
        togglePause();
    }
}
