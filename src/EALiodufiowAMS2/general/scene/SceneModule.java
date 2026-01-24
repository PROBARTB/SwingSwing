package EALiodufiowAMS2.general.scene;

import EALiodufiowAMS2.general.LayoutContext;
import EALiodufiowAMS2.general.layouts.ScenePanel;

public interface SceneModule<C extends SceneConfig> {

    SceneId getId();

    Class<C> getConfigType();

    /**
     * Loads everything needed for that type of scene and returns ready ScenePanel.
     * Should be called asynchronically.
     */
    ScenePanel loadScene(C config, LayoutContext context) throws Exception;
}
