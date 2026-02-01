package EALiodufiowAMS2.engine.rendering.renderingObject;

import EALiodufiowAMS2.helpers.Transform;

public class RenderingObject {
    private final Geometry geometry;
    private final Transform transform;

    public RenderingObject(Geometry geometry, Transform transform) {
        this.geometry = geometry;
        this.transform = transform;
    }

    public Geometry getGeometry() { return geometry; }
    public Transform getTransform() { return transform; }
}