package EALiodufiowAMS2.engine;

import EALiodufiowAMS2.engine.rendering.renderingObject.Material;
import EALiodufiowAMS2.engine.rendering.renderingObject.RenderingObject;
import EALiodufiowAMS2.engine.uiRendering.UiOverlayElement;

import java.util.*;

public class Scene {
    private List<RenderingObject> objects = new ArrayList<>();
    private final Material voidMaterial;

    private final List<UiOverlayElement> uiObjects = new ArrayList<>();

    public Scene(Material voidMaterial) {
        this.voidMaterial = voidMaterial;
    }

    public void addObject(RenderingObject obj) { objects.add(obj); }
    public void setObjects(List<RenderingObject> objects) { this.objects = objects; }

    public List<RenderingObject> getObjects() { return objects; }
    public Material getVoidMAterial() { return voidMaterial; }

    public void addUiObject(UiOverlayElement uiObject) { uiObjects.add(uiObject); }
    public List<UiOverlayElement> getUiObjects() { return uiObjects; }
}
