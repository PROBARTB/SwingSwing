package EALiodufiowAMS2.engine.rendering.geometryRenderer;

import EALiodufiowAMS2.helpers.Mesh;

public final class GeometryDrawSurface {

    private final int subMeshIndex;
    private final int materialSlot;
    private final Object surfaceId;

    public GeometryDrawSurface(int subMeshIndex,
                               int materialSlot,
                               Object surfaceId) {
        if (subMeshIndex < 0) {
            throw new IllegalArgumentException("subMeshIndex must be non-negative");
        }
        if (materialSlot < 0) {
            throw new IllegalArgumentException("materialSlot must be non-negative");
        }
        this.subMeshIndex = subMeshIndex;
        this.materialSlot = materialSlot;
        this.surfaceId = surfaceId;
    }

    public int getSubMeshIndex() {
        return subMeshIndex;
    }

    public int getMaterialSlot() {
        return materialSlot;
    }

    public Object getSurfaceId() {
        return surfaceId;
    }
}
