package EALiodufiowAMS2.engine.rendering.geometryRenderer;

import EALiodufiowAMS2.engine.rendering.PrimitiveType;
import EALiodufiowAMS2.helpers.Mesh;

import java.util.List;

public final class GeometryDrawData {

    private final Mesh mesh;
    private final PrimitiveType primitiveType;
    private final List<GeometryDrawSurface> surfaces;

    public GeometryDrawData(Mesh mesh,
                            PrimitiveType primitiveType,
                            List<GeometryDrawSurface> surfaces) {
        if (mesh == null) throw new IllegalArgumentException("mesh cannot be null");
        if (primitiveType == null) throw new IllegalArgumentException("primitiveType cannot be null");
        if (surfaces == null || surfaces.isEmpty()) {
            throw new IllegalArgumentException("surfaces cannot be null or empty");
        }
        this.mesh = mesh;
        this.primitiveType = primitiveType;
        this.surfaces = List.copyOf(surfaces);
    }

    public Mesh getMesh() {
        return mesh;
    }

    public PrimitiveType getPrimitiveType() {
        return primitiveType;
    }

    public List<GeometryDrawSurface> getSurfaces() {
        return surfaces;
    }
}
