package EALiodufiowAMS2.general.scene.scenes;

import EALiodufiowAMS2.engine.Camera;
import EALiodufiowAMS2.engine.Scene;
import EALiodufiowAMS2.engine.rendering.TextureManager;
import EALiodufiowAMS2.engine.rendering.renderingObject.Material;
import EALiodufiowAMS2.engine.rendering.renderingObject.RenderingObject;
import EALiodufiowAMS2.engine.rendering.renderingObject.TextureMode;
import EALiodufiowAMS2.engine.uiRendering.UiOverlay;
import EALiodufiowAMS2.game.builders.BuilderTest3D;
import EALiodufiowAMS2.general.LayoutContext;
import EALiodufiowAMS2.general.layouts.ScenePanel;
import EALiodufiowAMS2.general.settings.GraphicsSettings;
import EALiodufiowAMS2.helpers.Quaternion;
import EALiodufiowAMS2.helpers.Transform;
import EALiodufiowAMS2.helpers.Units;
import EALiodufiowAMS2.helpers.Vec3;

import java.awt.*;
import java.util.List;

public class DebugScenePanel extends ScenePanel {

    private final BuilderTest3D builderTest3d;

    public DebugScenePanel(LayoutContext layoutContext) {
        super(layoutContext);
        GraphicsSettings g = settingsManager.getCurrent().getGraphics();

        this.builderTest3d = new BuilderTest3D();

        this.camera = new Camera(
                new Vec3(g.getWindowWidth() / Units.M_TO_PX, g.getWindowHeight() / Units.M_TO_PX, 0),
                new Vec3(0, 0, -5),
                new Vec3(Math.toRadians(0), Math.toRadians(0), Math.toRadians(0)),
                75);
        this.sceneData = new Scene(new Material(new Color(0xff888888, true), TextureManager.getTexture("assets\\jaruzelski.jpg"), TextureMode.TILE_FIT));

        Transform staticTransform = new Transform();
        staticTransform.setPos(new Vec3(0, 0, -5));
        staticTransform.setSize(new Vec3(1, 1, 1));
        staticTransform.setRot(Quaternion.fromEuler(new Vec3(Math.toRadians(0), 0, 0)));


        Transform t = builderTest3d.getObjectTransform("cubeWithCamera");

        //this.camera.attachTo(staticTransform);
        this.camera.attachTo(t);


        reinitRenderingEngine(g);
    }

    @Override
    protected void updateScene(double deltaTime) {
        builderTest3d.update(deltaTime);
        List<RenderingObject> objects = builderTest3d.getRenderingObjects();
        sceneData.setObjects(objects);
    }

    @Override
    protected void onEscapePressed() {
        navigation.returnToMainMenu();
    }

    @Override
    protected void composeUiOverlay(UiOverlay overlay) {

    }
}
