package EALiodufiowAMS2.world.scenes;

import EALiodufiowAMS2.rendering.renderingObject.Material;
import EALiodufiowAMS2.rendering.renderingObject.RenderingObject;

import java.util.*;

public class Scene {
    private List<RenderingObject> objects = new ArrayList<>();
    private final Material voidMaterial;

    public Scene(Material voidMaterial) {
        this.voidMaterial = voidMaterial;
    }

    public void addObject(RenderingObject obj) { objects.add(obj); }
    public void setObjects(List<RenderingObject> objects) { this.objects = objects; }

    public List<RenderingObject> getObjects() { return objects; }
    public Material getVoidMAterial() { return voidMaterial; }
}
