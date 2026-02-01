package EALiodufiowAMS2.general;

import EALiodufiowAMS2.general.scene.SceneId;
import EALiodufiowAMS2.general.scene.SceneManager;
import EALiodufiowAMS2.general.scene.SceneModule;
import EALiodufiowAMS2.general.scene.scenes.*;
import EALiodufiowAMS2.general.settings.SettingsManager;
import EALiodufiowAMS2.general.world.EditorWorldConfig;
import EALiodufiowAMS2.general.world.GameplayWorldConfig;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class GameController implements GameNavigation {

    private final LayoutContext layoutContext;
    private final GamePanel gamePanel;
    private final SceneManager sceneManager;

    public GameController(SettingsManager settingsManager) {

        this.layoutContext = new LayoutContext(settingsManager, this);

        this.gamePanel = new GamePanel(layoutContext);
        this.sceneManager = new SceneManager(
                layoutContext,
                List.of(
                        new GameplaySceneModule(),
                        new EditorSceneModule(),
                        new DebugSceneModule(),
                        new PiotrostalSceneModule()
                    )
            );
    }

    public GamePanel getPanel() { return this.gamePanel; }

    @Override
    public void startNewGameFlow() {
        gamePanel.showStartGame();
    }

    @Override
    public void startEditorDefault() {
        startEditorWithConfig(new EditorSceneConfig(new EditorWorldConfig()));
    }

    @Override
    public void openSettings() {
        gamePanel.showSettings();
    }

    @Override
    public void quitGame() {
        Window window = SwingUtilities.getWindowAncestor(gamePanel);
        if (window != null) {
            window.dispose();
        } else {
            System.exit(0);
        }
    }

    @Override
    public void returnToMainMenu() {
        gamePanel.showMainMenu();
    }

    @Override
    public void startGameWithConfig(GameplaySceneConfig config) {
        gamePanel.showLoading("Loading gameplay world...");

        sceneManager.getSceneAsync(SceneId.GAMEPLAY, config,
                gamePanel::showScene,
                this::onException
            );
    }

    @Override
    public void startEditorWithConfig(EditorSceneConfig config) {
        gamePanel.showLoading("Loading editor world...");

        sceneManager.getSceneAsync(SceneId.EDITOR, config,
                gamePanel::showScene,
                this::onException
        );
    }

    @Override
    public void loadDebugScene() {
        sceneManager.getSceneAsync(SceneId.DEBUG, EmptyConfig.INSTANCE,
                gamePanel::showScene,
                this::onException
        );
    }

    @Override
    public void loadPiotrostalScene() {
        sceneManager.getSceneAsync(SceneId.PIOTROSTAL, EmptyConfig.INSTANCE,
                gamePanel::showScene,
                this::onException
        );
    }

    private void onException(Exception ex) {
        ex.printStackTrace();
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(
                    gamePanel,
                    "Failed to load scene:\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
            gamePanel.showMainMenu();
        });
    }
}

