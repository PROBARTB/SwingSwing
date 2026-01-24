package EALiodufiowAMS2.general.scene.scenes;

import EALiodufiowAMS2.general.layouts.ScenePanel;
import EALiodufiowAMS2.general.LayoutContext;
import EALiodufiowAMS2.general.scene.SceneId;
import EALiodufiowAMS2.general.scene.SceneModule;
import EALiodufiowAMS2.general.world.GameplayWorld;
import EALiodufiowAMS2.general.world.GameplayWorldConfig;

public class GameplaySceneModule implements SceneModule<GameplaySceneConfig> {

    @Override
    public SceneId getId() {
        return SceneId.GAMEPLAY;
    }

    @Override
    public Class<GameplaySceneConfig> getConfigType() {
        return GameplaySceneConfig.class;
    }

    @Override
    public ScenePanel loadScene(GameplaySceneConfig config, LayoutContext context) {
        GameplayWorld world = loadGameplayWorld(config.getGameplayWorldConfig(), context);
        return new GameplayScenePanel(context, world);
    }

    private GameplayWorld loadGameplayWorld(GameplayWorldConfig config, LayoutContext context) {
        return new GameplayWorld(); // PLACEHOLDER
    }
}
