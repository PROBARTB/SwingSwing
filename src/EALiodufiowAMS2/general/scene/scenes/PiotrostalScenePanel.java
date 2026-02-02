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
    private UiOverlayElement scoreLabelEl;
    private UiOverlayElement changeLabelEl;
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
        UiOverlayLayer layer = overlay.getOrCreateLayer("vel");

        UiOverlayConstraints logoConstraints = new UiOverlayConstraints(
                UiOverlayAnchor.TOP_CENTER, 0, 10, null, null, true
        );
        JLabel logoLabel = new JLabel(new ImageIcon("assets\\piotrostal\\logo.png"));
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        layer.addElement(new UiOverlayElement(logoLabel, logoConstraints));

        UiOverlayConstraints titleConstraints = new UiOverlayConstraints(
                UiOverlayAnchor.TOP_CENTER, 0, 100, null, null, true
        );
        JLabel titleLabel = new JLabel("Złomuj rarytasy aby otrzymać punkty!!");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.ITALIC, 20));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        layer.addElement(new UiOverlayElement(titleLabel, titleConstraints));

        UiOverlayConstraints punktyConstraints = new UiOverlayConstraints(
                UiOverlayAnchor.TOP_CENTER, 0, 140, null, null, true
        );
        JLabel punktyLabel = new JLabel("Punkty");
        punktyLabel.setForeground(Color.WHITE);
        punktyLabel.setFont(new Font("Arial", Font.BOLD, 18));
        punktyLabel.setHorizontalAlignment(SwingConstants.CENTER);
        layer.addElement(new UiOverlayElement(punktyLabel, punktyConstraints));


        UiOverlayConstraints scoreConstraints = new UiOverlayConstraints(
                UiOverlayAnchor.TOP_CENTER, 0, 170, null, null, true
        );
        JLabel scoreLabel = new JLabel(String.valueOf(piotrostalObjectBuilder.getScore()));
        scoreLabel.setForeground(Color.WHITE);
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 50));
        scoreLabel.setHorizontalAlignment(SwingConstants.CENTER);
        scoreLabel.setPreferredSize(new Dimension(200, 50));
        scoreLabelEl = new UiOverlayElement(scoreLabel, scoreConstraints);
        layer.addElement(scoreLabelEl);

        UiOverlayConstraints changeConstraints = new UiOverlayConstraints(
                UiOverlayAnchor.TOP_CENTER, 120, 180, null, null, true
        );
        JLabel changeLabel = new JLabel("");
        changeLabel.setFont(new Font("Arial", Font.BOLD, 30));
        changeLabelEl = new UiOverlayElement(changeLabel, changeConstraints);
        layer.addElement(changeLabelEl);


        UiOverlayConstraints buttonConstraints1 = new UiOverlayConstraints(
                UiOverlayAnchor.TOP_CENTER, -60, 230, null, null, true
        );
        JButton zlomowanie = new JButton("Złomuj ratrys!");
        layer.addElement(new UiOverlayElement(zlomowanie, buttonConstraints1));

        UiOverlayConstraints buttonConstraints2 = new UiOverlayConstraints(
                UiOverlayAnchor.TOP_CENTER, 60, 230, null, null, true
        );
        JButton nieZlomuj = new JButton("Nie złomuj");
        layer.addElement(new UiOverlayElement(nieZlomuj, buttonConstraints2));

        zlomowanie.addActionListener(e -> {
            if(piotrostalObjectBuilder.isProcessing()) return;
            String lastId = piotrostalObjectBuilder.getCurrentCubeId();
            piotrostalObjectBuilder.scrapCube(lastId);
            updateScoreLabel();
            updateChangeLabel();

        });


        nieZlomuj.addActionListener(e -> {
            if(piotrostalObjectBuilder.isProcessing()) return;
            String lastId = piotrostalObjectBuilder.getCurrentCubeId();
            piotrostalObjectBuilder.evaporateCube(lastId);
            updateScoreLabel();
            updateChangeLabel();
        });

    }

    private void updateScoreLabel() {
        if (uiOverlayEngine != null && scoreLabelEl != null) {
            ((JLabel) scoreLabelEl.getComponent()).setText(String.valueOf(piotrostalObjectBuilder.getScore()));
            uiOverlayEngine.updateElement(scoreLabelEl);
        }
    }

    private void updateChangeLabel() {
        if (uiOverlayEngine != null && changeLabelEl != null) {
            int sc = piotrostalObjectBuilder.getLastScoreChange();
            if (sc == 0) {
                changeLabelEl.setConstraints(changeLabelEl.getConstraints().withVisibility(false));
            } else {
                changeLabelEl.setConstraints(changeLabelEl.getConstraints().withVisibility(true));
            }
            JLabel c = (JLabel) changeLabelEl.getComponent();
            c.setForeground(sc < 0 ? Color.RED : Color.GREEN);
            c.setText((sc < 0 ? "" : "+") + String.valueOf(sc));

            uiOverlayEngine.updateElement(changeLabelEl);
        }
    }

    @Override
    protected void updateScene(double deltaTime) {
        piotrostalObjectBuilder.update(deltaTime);
        sceneData.setObjects(piotrostalObjectBuilder.getRenderingObjects());
    }
}
