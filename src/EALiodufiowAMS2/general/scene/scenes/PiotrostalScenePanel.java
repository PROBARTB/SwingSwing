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

    private final PiotrostalObjectBuilder piotrostalObjectBuilder = new PiotrostalObjectBuilder();
    private JLabel scoreLabel;

    public PiotrostalScenePanel(LayoutContext layoutContext) {
        super(layoutContext);
        GraphicsSettings g = settingsManager.getCurrent().getGraphics();


        this.camera = new Camera(
                new Vec3(g.getWindowWidth() / Units.M_TO_PX, g.getWindowHeight() / Units.M_TO_PX, 0),
                new Vec3(0, 0, -6),
                new Vec3(Math.toRadians(0), Math.toRadians(0), Math.toRadians(0)),
                75);
        this.sceneData = new Scene(new Material(new Color(0xff888888, true), TextureManager.getTexture("assets\\Piotrostal\\zlomowanie.jpg")));

        Transform staticTransform = new Transform();
        staticTransform.setPos(new Vec3(0, 0, 0));
        staticTransform.setSize(new Vec3(1, 1, 1));
        staticTransform.setRot(Quaternion.fromEuler(new Vec3(Math.toRadians(0), 0, 0)));


        this.camera.attachTo(staticTransform);
        //this.camera.attachTo(t);


        reinitRenderingEngine(g);
    }


    @Override
    protected void onEscapePressed() {
        navigation.returnToMainMenu();
    }

    @Override
    protected void composeUiOverlay(UiOverlay overlay) {
        UiOverlayLayer layer = overlay.getOrCreateLayer("cwel");

        UiOverlayConstraints scoreConstraints = new UiOverlayConstraints(
                UiOverlayAnchor.TOP_CENTER, 0, 30, null, null, true
        );

        // JLabel z wymuszoną szerokością i HTML
        scoreLabel = new JLabel("<html>" + piotrostalObjectBuilder.getScoreText() + "</html>");
        scoreLabel.setForeground(Color.WHITE);
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 16));
        scoreLabel.setPreferredSize(new Dimension(200, 30)); // szerokość na tekst

        layer.addElement(new UiOverlayElement(scoreLabel, scoreConstraints));

        UiOverlayConstraints buttonConstraints = new UiOverlayConstraints(
                UiOverlayAnchor.TOP_CENTER, 0, 65, null, null, true
        );

        JButton złomowanie = new JButton("Złomuj ratrys!");
        layer.addElement(new UiOverlayElement(złomowanie, buttonConstraints));

        złomowanie.addActionListener(e -> {
            piotrostalObjectBuilder.spawnNextCube();
            updateScoreLabel();
        });
    }

    private void updateScoreLabel() {
        if (scoreLabel != null) {
            // HTML wymusza pełne wyświetlenie tekstu
            scoreLabel.setText("<html>" + piotrostalObjectBuilder.getScoreText() + "</html>");
        }
    }


    @Override
    protected void updateScene(double deltaTime) {
        piotrostalObjectBuilder.update(deltaTime);
        sceneData.setObjects(piotrostalObjectBuilder.getRenderingObjects());
        updateScoreLabel(); // odświeżaj label co klatkę
    }
}


