package EALiodufiowAMS2.general.scene.scenes;

import EALiodufiowAMS2.general.scene.SceneConfig;
import EALiodufiowAMS2.general.world.GameplayWorldConfig;

public class GameplaySceneConfig implements SceneConfig {
    private GameplayWorldConfig gameplayWorldConfig;

    public GameplaySceneConfig(GameplayWorldConfig gameplayWorldConfig ) {
        this.gameplayWorldConfig = gameplayWorldConfig;
    }

    public GameplayWorldConfig getGameplayWorldConfig() { return gameplayWorldConfig; }
}
