package EALiodufiowAMS2.engine.uiRendering;

import java.util.*;
import java.util.List;

public final class UiOverlay {

    private final Map<String, UiOverlayLayer> layers = new LinkedHashMap<>();

    public UiOverlay() {
    }

    public UiOverlayLayer getOrCreateLayer(String id) {
        return layers.computeIfAbsent(id, UiOverlayLayer::new);
    }

    public UiOverlayLayer getLayer(String id) {
        return layers.get(id);
    }

    public Collection<UiOverlayLayer> getLayers() {
        return layers.values();
    }

    public void clear() {
        layers.clear();
    }
}
