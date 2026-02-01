package EALiodufiowAMS2.general;

import EALiodufiowAMS2.general.scene.scenes.EditorSceneConfig;
import EALiodufiowAMS2.general.scene.scenes.GameplaySceneConfig;

public interface GameNavigation {
    void openSettings();
    void quitGame();

    void startNewGameFlow();
    void startEditorDefault();
    void startGameWithConfig(GameplaySceneConfig config);
    void startEditorWithConfig(EditorSceneConfig config);
    void loadDebugScene();
    void loadPiotrostalScene();
    void returnToMainMenu();
}
