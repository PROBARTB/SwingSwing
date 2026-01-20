package EALiodufiowAMS2.rendering.renderingObject;

import EALiodufiowAMS2.helpers.Transform;
import EALiodufiowAMS2.rendering.renderingObject.geometries.Geometry;

public final class RenderingObject {
    private final Geometry geometry;
    private final Transform transform;

    public RenderingObject(Geometry geometry, Transform transform) {
        this.geometry = geometry;
        this.transform = transform;
    }

    public Geometry getGeometry() { return geometry; }
    public Transform getTransform() { return transform; }
}