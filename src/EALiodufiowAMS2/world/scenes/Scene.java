package EALiodufiowAMS2.world.scenes;

import EALiodufiowAMS2.rendering.renderingObject.Material;
import EALiodufiowAMS2.rendering.renderingObject.RenderingObject;
import EALiodufiowAMS2.uiRendering.UiObject;

import java.util.*;

public class Scene {
    private List<RenderingObject> objects = new ArrayList<>();
    private final Material voidMaterial;

    private final List<UiObject> uiObjects = new ArrayList<>();

    public Scene(Material voidMaterial) {
        this.voidMaterial = voidMaterial;
    }

    public void addObject(RenderingObject obj) { objects.add(obj); }
    public void setObjects(List<RenderingObject> objects) { this.objects = objects; }

    public List<RenderingObject> getObjects() { return objects; }
    public Material getVoidMAterial() { return voidMaterial; }

    public void addUiObject(UiObject uiObject) { uiObjects.add(uiObject); }
    public List<UiObject> getUiObjects() { return uiObjects; }
}
