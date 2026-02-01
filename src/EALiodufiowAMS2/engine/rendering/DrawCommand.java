package EALiodufiowAMS2.engine.rendering;


import EALiodufiowAMS2.helpers.Mesh;
import EALiodufiowAMS2.helpers.Transform;
import EALiodufiowAMS2.engine.rendering.renderingObject.objects.cuboid.FaceType;
import EALiodufiowAMS2.engine.rendering.renderingObject.Material;

import java.util.Map;

public final class DrawCommand {

    private final Mesh mesh;
    private final PrimitiveType primitiveType;
    private final Transform transform;
    private final Map<Mesh.FaceRange, Material> materialByRange;

    public DrawCommand(Mesh mesh,
                       PrimitiveType primitiveType,
                       Transform transform,
                       Map<Mesh.FaceRange, Material> materialByRange) {
        if (mesh == null) throw new IllegalArgumentException("mesh cannot be null");
        if (primitiveType == null) throw new IllegalArgumentException("primitiveType cannot be null");
        if (transform == null) throw new IllegalArgumentException("transform cannot be null");
        if (materialByRange == null || materialByRange.isEmpty()) {
            throw new IllegalArgumentException("materialByRange cannot be null or empty");
        }
        this.mesh = mesh;
        this.primitiveType = primitiveType;
        this.transform = transform;
        this.materialByRange = Map.copyOf(materialByRange);
    }

    public Mesh getMesh() {
        return mesh;
    }

    public PrimitiveType getPrimitiveType() {
        return primitiveType;
    }

    public Transform getTransform() {
        return transform;
    }

    public Map<Mesh.FaceRange, Material> getMaterialByRange() {
        return materialByRange;
    }
}
