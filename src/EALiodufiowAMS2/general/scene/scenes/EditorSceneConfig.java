package EALiodufiowAMS2.general.scene.scenes;

import EALiodufiowAMS2.general.scene.SceneConfig;
import EALiodufiowAMS2.general.world.EditorWorldConfig;
import EALiodufiowAMS2.general.world.GameplayWorldConfig;

public class EditorSceneConfig implements SceneConfig {
    private EditorWorldConfig editorWorldConfig;

    public EditorSceneConfig(EditorWorldConfig editorWorldConfig ) {
        this.editorWorldConfig = editorWorldConfig;
    }

    public EditorWorldConfig getEditorWorldConfig() { return editorWorldConfig; }
}
