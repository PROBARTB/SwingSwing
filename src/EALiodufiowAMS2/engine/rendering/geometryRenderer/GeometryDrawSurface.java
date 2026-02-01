package EALiodufiowAMS2.engine.rendering.geometryRenderer;

import EALiodufiowAMS2.helpers.Mesh;

public final class GeometryDrawSurface {

    private final Mesh.FaceRange faceRange;
    private final Object surfaceId; // np. FaceType dla cuboida

    public GeometryDrawSurface(Mesh.FaceRange faceRange, Object surfaceId) {
        if (faceRange == null) throw new IllegalArgumentException("faceRange cannot be null");
        this.faceRange = faceRange;
        this.surfaceId = surfaceId;
    }

    public Mesh.FaceRange getFaceRange() {
        return faceRange;
    }

    public Object getSurfaceId() {
        return surfaceId;
    }
}
