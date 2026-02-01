package EALiodufiowAMS2.helpers;

import EALiodufiowAMS2.engine.rendering.PrimitiveType;

import java.util.List;
import java.util.Objects;

public final class Mesh {

    public static final class SubMesh {
        public final int indexOffset;     // offset w indices (w indeksach, nie bajtach)
        public final int indexCount;      // ile indeksów należy do tego submesha
        public final PrimitiveType primitiveType;
        public final int materialSlot;    // indeks slotu materiału
        public final BoundingBox bounds;  // opcjonalnie: bounding box submesha

        public SubMesh(int indexOffset,
                       int indexCount,
                       PrimitiveType primitiveType,
                       int materialSlot,
                       BoundingBox bounds) {
            this.indexOffset   = indexOffset;
            this.indexCount    = indexCount;
            this.primitiveType = primitiveType;
            this.materialSlot  = materialSlot;
            this.bounds        = Objects.requireNonNull(bounds);
        }
    }

    private final Vertex[] vertices;
    private final int[] indices;
    private final List<SubMesh> subMeshes;

    public Mesh(Vertex[] vertices, int[] indices, List<SubMesh> subMeshes) {
        this.vertices = vertices;
        this.indices = indices;
        this.subMeshes = List.copyOf(subMeshes);
    }

    public Vertex[] getVertices() { return vertices; }
    public int[] getIndices() { return indices; }
    public List<SubMesh> getSubMeshes() { return subMeshes; }

}