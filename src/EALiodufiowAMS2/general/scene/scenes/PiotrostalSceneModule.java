package EALiodufiowAMS2.general.scene.scenes;

import EALiodufiowAMS2.general.LayoutContext;
import EALiodufiowAMS2.general.layouts.ScenePanel;
import EALiodufiowAMS2.general.scene.SceneId;
import EALiodufiowAMS2.general.scene.SceneModule;

public class PiotrostalSceneModule implements SceneModule<EmptyConfig> {

    @Override
    public SceneId getId() {
        return SceneId.PIOTROSTAL;
    }

    @Override
    public Class<EmptyConfig> getConfigType() {
        return EmptyConfig.class;
    }

    @Override
    public ScenePanel loadScene(EmptyConfig config, LayoutContext context) {
        return new PiotrostalScenePanel(context);
    }
}
