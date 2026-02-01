package EALiodufiowAMS2.general.scene.scenes;

import EALiodufiowAMS2.engine.Camera;
import EALiodufiowAMS2.engine.Scene;
import EALiodufiowAMS2.engine.rendering.TextureManager;
import EALiodufiowAMS2.engine.rendering.renderingObject.Material;
import EALiodufiowAMS2.engine.rendering.renderingObject.RenderingObject;
import EALiodufiowAMS2.engine.uiRendering.*;
import EALiodufiowAMS2.game.builders.BuilderTest3D;
import EALiodufiowAMS2.game.builders.PiotrostalObjectBuilder;
import EALiodufiowAMS2.general.LayoutContext;
import EALiodufiowAMS2.general.layouts.ScenePanel;
import EALiodufiowAMS2.general.settings.GraphicsSettings;
import EALiodufiowAMS2.helpers.Quaternion;
import EALiodufiowAMS2.helpers.Transform;
import EALiodufiowAMS2.helpers.Units;
import EALiodufiowAMS2.helpers.Vec3;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class PiotrostalScenePanel extends ScenePanel {

    private final PiotrostalObjectBuilder piotrostalObjectBuilder;

    public PiotrostalScenePanel(LayoutContext layoutContext) {
        super(layoutContext);
        GraphicsSettings g = settingsManager.getCurrent().getGraphics();

        this.piotrostalObjectBuilder = new PiotrostalObjectBuilder();

        this.camera = new Camera(
                new Vec3(g.getWindowWidth() / Units.M_TO_PX, g.getWindowHeight() / Units.M_TO_PX, 0),
                new Vec3(0, 0, -5),
                new Vec3(Math.toRadians(0), Math.toRadians(0), Math.toRadians(0)),
                75);
        this.sceneData = new Scene(new Material(new Color(0xff888888, true), TextureManager.getTexture("assets\\Piotrostal\\zlomowanie.jpg")));

        Transform staticTransform = new Transform();
        staticTransform.setPos(new Vec3(0, 0, -5));
        staticTransform.setSize(new Vec3(1, 1, 1));
        staticTransform.setRot(Quaternion.fromEuler(new Vec3(Math.toRadians(3), 0, 0)));


        Transform t = piotrostalObjectBuilder.getObjectTransform("cubeWithCamera");

        //this.camera.attachTo(staticTransform);
        this.camera.attachTo(t);


        reinitRenderingEngine(g);
    }

    @Override
    protected void updateScene(double deltaTime) {
        piotrostalObjectBuilder.update(deltaTime);
        List<RenderingObject> objects = piotrostalObjectBuilder.getRenderingObjects();
        sceneData.setObjects(objects);
    }

    @Override
    protected void onEscapePressed() {
        navigation.returnToMainMenu();
    }

    @Override
    protected void composeUiOverlay(UiOverlay overlay) {
        UiOverlayLayer tymoteuszKolodziejczyk = overlay.getOrCreateLayer("cwel");
        UiOverlayConstraints camInfoLabelConstraints = new UiOverlayConstraints(
                UiOverlayAnchor.TOP_CENTER, -10, 65, null, null, true
        );
        JButton złomowanie = new JButton("Złomuj ratrys!");
        UiOverlayElement cwertyk = new UiOverlayElement(złomowanie, camInfoLabelConstraints);
        tymoteuszKolodziejczyk.addElement(cwertyk);

        złomowanie.addActionListener(e -> {
            piotrostalObjectBuilder.spawnNextCube();});
    }
}
