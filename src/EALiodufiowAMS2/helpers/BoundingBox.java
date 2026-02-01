package EALiodufiowAMS2.helpers;

public final class BoundingBox {

    public final Vec3 min;
    public final Vec3 max;

    public BoundingBox(Vec3 min, Vec3 max) {
        this.min = min;
        this.max = max;
    }

    public Vec3 getCenter() {
        return new Vec3(
                (min.x + max.x) * 0.5,
                (min.y + max.y) * 0.5,
                (min.z + max.z) * 0.5
        );
    }

    public static BoundingBox empty() {
        double inf = Double.POSITIVE_INFINITY;
        return new BoundingBox(
                new Vec3( inf,  inf,  inf),
                new Vec3(-inf, -inf, -inf)
        );
    }

    public BoundingBox include(Vec3 p) {
        return new BoundingBox(
                new Vec3(
                        Math.min(min.x, p.x),
                        Math.min(min.y, p.y),
                        Math.min(min.z, p.z)
                ),
                new Vec3(
                        Math.max(max.x, p.x),
                        Math.max(max.y, p.y),
                        Math.max(max.z, p.z)
                )
        );
    }

    public static BoundingBox computeSubMeshBounds(Vertex[] vertices, Integer[] indices,
                                                   int indexOffset, int indexCount) {
        BoundingBox box = BoundingBox.empty();
        for (int i = indexOffset; i < indexOffset + indexCount; i++) {
            int vi = indices[i];
            Vec3 pos = vertices[vi].position;
            box = box.include(pos);
        }
        return box;
    }
}
