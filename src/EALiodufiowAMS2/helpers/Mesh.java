package EALiodufiowAMS2.helpers;

import EALiodufiowAMS2.engine.rendering.renderingObject.objects.cuboid.FaceType;

import java.util.Map;

public final class Mesh {
    private final Vertex[] vertices;
    private final int[] indices;

    public static final class FaceRange {
        public final FaceType faceType;
        public final int startIndex; // offset w indices
        public final int indexCount;

        public FaceRange(FaceType faceType, int startIndex, int indexCount) {
            this.faceType = faceType;
            this.startIndex = startIndex;
            this.indexCount = indexCount;
        }
    }

    private final Map<FaceType, FaceRange> faceRanges;

    public Mesh(Vertex[] vertices, int[] indices, Map<FaceType, FaceRange> faceRanges) {
        this.vertices = vertices;
        this.indices = indices;
        this.faceRanges = Map.copyOf(faceRanges);
    }

    public Vertex[] getVertices() { return vertices; }
    public int[] getIndices() { return indices; }
    public FaceRange getFaceRange(FaceType type) { return faceRanges.get(type); }
}