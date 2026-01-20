package EALiodufiowAMS2.rendering.renderingObject.geometries;

import EALiodufiowAMS2.rendering.renderingObject.*;

import java.util.List;

public final class CuboidGeometry implements Geometry {
    private final List<Surface> surfaces;

    public CuboidGeometry(List<Surface> surfaces) {
        this.surfaces = List.copyOf(surfaces);
    }

    public List<Surface> getSurfaces() {
        return surfaces;
    }
}