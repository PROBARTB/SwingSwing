package EALiodufiowAMS2.general.scene;

import EALiodufiowAMS2.general.LayoutContext;
import EALiodufiowAMS2.general.layouts.ScenePanel;
import EALiodufiowAMS2.helpers.AsyncSwingExecutor;

import java.util.*;
import java.util.function.Consumer;

public class SceneManager {

    private final Map<SceneId, SceneModule<?>> modules = new HashMap<>();
    private final AsyncSwingExecutor loader;
    private final LayoutContext context;

    public SceneManager(LayoutContext context, List<SceneModule<?>> moduleList) {
        this.loader = new AsyncSwingExecutor();
        this.context = context;
        for (SceneModule<?> module : moduleList) {
            modules.put(module.getId(), module);
        }
    }

    public <C extends SceneConfig> void getSceneAsync(SceneId id,
                                                       C config,
                                                       Consumer<ScenePanel> onLoaded,
                                                       Consumer<Exception> onError) {
        SceneModule<?> rawModule = modules.get(id);
        if (rawModule == null) throw new IllegalArgumentException("No module registered for scene: " + id);
        SceneModule<C> module = castModule(rawModule, config);

        loader.loadAsync(
                () -> {
                    try {
                        return module.loadScene(config, context);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                },
                onLoaded,
                onError
        );
    }

    @SuppressWarnings("unchecked")
    private <C extends SceneConfig> SceneModule<C> castModule(SceneModule<?> rawModule, C config) {
        Class<?> expected = rawModule.getConfigType();
        if (!expected.isInstance(config)) {
            throw new IllegalArgumentException("Config type mismatch for scene " + rawModule.getId() + ": expected " + expected.getName() + " but got " + config.getClass().getName());
        }
        return (SceneModule<C>) rawModule;
    }
}
