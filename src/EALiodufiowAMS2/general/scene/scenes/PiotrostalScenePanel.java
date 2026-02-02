package EALiodufiowAMS2.general.scene.scenes;

import EALiodufiowAMS2.engine.Camera;
import EALiodufiowAMS2.engine.Scene;
import EALiodufiowAMS2.engine.rendering.TextureManager;
import EALiodufiowAMS2.engine.rendering.renderingObject.Material;
import EALiodufiowAMS2.engine.uiRendering.*;
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
import java.net.URL;
import java.util.List;
import java.util.Random;

public class PiotrostalScenePanel extends ScenePanel {

    private final PiotrostalObjectBuilder piotrostalObjectBuilder = new PiotrostalObjectBuilder();
    private JLabel scoreLabel;
    private int lastScore = -1;

    public PiotrostalScenePanel(LayoutContext layoutContext) {
        super(layoutContext);
        GraphicsSettings g = settingsManager.getCurrent().getGraphics();

        this.camera = new Camera(
                new Vec3(g.getWindowWidth() / Units.M_TO_PX, g.getWindowHeight() / Units.M_TO_PX, 0),
                new Vec3(0, 0, -6),
                new Vec3(Math.toRadians(0), Math.toRadians(0), Math.toRadians(0)),
                75);
        this.sceneData = new Scene(new Material(new Color(0xff888888, true),
                TextureManager.getTexture("assets/Piotrostal/zlomowanie.jpg")));

        Transform staticTransform = new Transform();
        staticTransform.setPos(new Vec3(0, 0, 0));
        staticTransform.setSize(new Vec3(1, 1, 1));
        staticTransform.setRot(Quaternion.fromEuler(new Vec3(Math.toRadians(0), 0, 0)));

        this.camera.attachTo(staticTransform);
        reinitRenderingEngine(g);
    }

    @Override
    protected void onEscapePressed() {
        navigation.returnToMainMenu();
    }

    @Override
    protected void composeUiOverlay(UiOverlay overlay) {
        UiOverlayLayer layer = overlay.getOrCreateLayer("cwel");

        // --- Label "Punkty" ---
        UiOverlayConstraints titleConstraints = new UiOverlayConstraints(
                UiOverlayAnchor.TOP_CENTER, 0, 20, null, null, true
        );
        JLabel punktyLabel = new JLabel("Punkty");
        punktyLabel.setForeground(Color.WHITE);
        punktyLabel.setFont(new Font("Arial", Font.BOLD, 18));
        punktyLabel.setHorizontalAlignment(SwingConstants.CENTER);
        layer.addElement(new UiOverlayElement(punktyLabel, titleConstraints));

        // --- Label wynik ---
        UiOverlayConstraints scoreConstraints = new UiOverlayConstraints(
                UiOverlayAnchor.TOP_CENTER, 0, 50, null, null, true
        );
        scoreLabel = new JLabel(String.valueOf(piotrostalObjectBuilder.getScore()));
        scoreLabel.setForeground(Color.WHITE);
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 22));
        scoreLabel.setHorizontalAlignment(SwingConstants.CENTER);
        scoreLabel.setPreferredSize(new Dimension(200, 30)); // stała szerokość
        layer.addElement(new UiOverlayElement(scoreLabel, scoreConstraints));

        // --- przycisk Złomuj ---
        UiOverlayConstraints buttonConstraints1 = new UiOverlayConstraints(
                UiOverlayAnchor.TOP_CENTER, -60, 90, null, null, true
        );
        JButton zlomowanie = new JButton("Złomuj ratrys!");
        layer.addElement(new UiOverlayElement(zlomowanie, buttonConstraints1));

        // --- przycisk Nie złomuj ---
        UiOverlayConstraints buttonConstraints2 = new UiOverlayConstraints(
                UiOverlayAnchor.TOP_CENTER, 60, 90, null, null, true
        );
        JButton nieZlomuj = new JButton("Nie złomuj");
        layer.addElement(new UiOverlayElement(nieZlomuj, buttonConstraints2));

        zlomowanie.addActionListener(e -> {
            List<String> ids = piotrostalObjectBuilder.getObjectIds();
            if (!ids.isEmpty()) {
                String lastId = ids.get(ids.size() - 1);
                piotrostalObjectBuilder.scrapCube(lastId);
                updateScoreLabel();
                // ❌ NIE wołamy spawnNextCube tutaj
            }
        });


        nieZlomuj.addActionListener(e -> {
            List<String> ids = piotrostalObjectBuilder.getObjectIds();
            if (!ids.isEmpty()) {
                String lastId = ids.get(ids.size() - 1);
                piotrostalObjectBuilder.evaporateCube(lastId);
                updateScoreLabel();
                piotrostalObjectBuilder.spawnNextCube(); // tu zostaje
            }
        });



    }


    private void updateScoreLabel() {
        if (scoreLabel != null) {
            scoreLabel.setText(String.valueOf(piotrostalObjectBuilder.getScore()));
        }
    }

    @Override
    protected void updateScene(double deltaTime) {
        piotrostalObjectBuilder.update(deltaTime);
        sceneData.setObjects(piotrostalObjectBuilder.getRenderingObjects());

        int currentScore = piotrostalObjectBuilder.getScore();
        if (currentScore != lastScore) {
            updateScoreLabel();
            lastScore = currentScore;
        }
    }
}
