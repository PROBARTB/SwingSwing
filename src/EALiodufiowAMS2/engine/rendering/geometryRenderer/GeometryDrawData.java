package EALiodufiowAMS2.engine.rendering.geometryRenderer;

import EALiodufiowAMS2.engine.rendering.PrimitiveType;
import EALiodufiowAMS2.helpers.Mesh;

import java.util.List;

public final class GeometryDrawData {

    private final Mesh mesh;
    private final List<GeometryDrawSurface> surfaces;

    public GeometryDrawData(Mesh mesh,
                            List<GeometryDrawSurface> surfaces) {
        if (mesh == null) throw new IllegalArgumentException("mesh cannot be null");
        if (surfaces == null || surfaces.isEmpty()) {
            throw new IllegalArgumentException("surfaces cannot be null or empty");
        }
        this.mesh = mesh;
        this.surfaces = List.copyOf(surfaces);
    }

    public Mesh getMesh() {
        return mesh;
    }

    public List<GeometryDrawSurface> getSurfaces() {
        return surfaces;
    }
}
