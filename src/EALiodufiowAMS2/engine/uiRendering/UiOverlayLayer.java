package EALiodufiowAMS2.engine.uiRendering;

import java.util.*;

public final class UiOverlayLayer {
    private final String id;
    private final List<UiOverlayElement> elements = new ArrayList<>();

    public UiOverlayLayer(String id) {
        this.id = id;
    }

    public UiOverlayLayer(String id, List<UiOverlayElement> elements) {
        this(id);
        if (elements != null) this.elements.addAll(elements);
    }

    public String getId() { return id; }

    public List<UiOverlayElement> getElements() {
        return Collections.unmodifiableList(elements);
    }

    public void addElement(UiOverlayElement element) {
        elements.add(Objects.requireNonNull(element, "element"));
    }

    public void removeElement(UiOverlayElement element) {
        elements.remove(element);
    }

    public void clear() {
        elements.clear();
    }
}