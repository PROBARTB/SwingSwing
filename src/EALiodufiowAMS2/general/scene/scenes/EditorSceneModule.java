package EALiodufiowAMS2.general.scene.scenes;

import EALiodufiowAMS2.general.layouts.ScenePanel;
import EALiodufiowAMS2.general.LayoutContext;
import EALiodufiowAMS2.general.scene.SceneId;
import EALiodufiowAMS2.general.scene.SceneModule;
import EALiodufiowAMS2.general.world.EditorWorld;
import EALiodufiowAMS2.general.world.EditorWorldConfig;

public class EditorSceneModule implements SceneModule<EditorSceneConfig> {

    @Override
    public SceneId getId() {
        return SceneId.EDITOR;
    }

    @Override
    public Class<EditorSceneConfig> getConfigType() {
        return EditorSceneConfig.class;
    }

    @Override
    public ScenePanel loadScene(EditorSceneConfig config, LayoutContext context) {
        EditorWorld world = loadEditorWorld(config.getEditorWorldConfig(), context);
        return new EditorScenePanel(context, world);
    }

    private EditorWorld loadEditorWorld(EditorWorldConfig config,
                                        LayoutContext context) {
        return new EditorWorld();
    }
}
