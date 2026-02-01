package EALiodufiowAMS2.engine.rendering;


import EALiodufiowAMS2.helpers.Mesh;
import EALiodufiowAMS2.helpers.Transform;
import EALiodufiowAMS2.engine.rendering.renderingObject.Material;

import java.util.Map;
import java.util.Objects;

public final class DrawCommand {

    private final Mesh mesh;
    private final Transform transform;
    private final Map<Integer, Material> materialsBySlot;

    public DrawCommand(Mesh mesh,
                       Transform transform,
                       Map<Integer, Material> materialsBySlot) {
        this.mesh = Objects.requireNonNull(mesh);
        this.transform = Objects.requireNonNull(transform);
        if (materialsBySlot == null || materialsBySlot.isEmpty()) {
            throw new IllegalArgumentException("materialsBySlot cannot be null or empty");
        }
        this.materialsBySlot = Map.copyOf(materialsBySlot);
    }

    public Mesh getMesh() { return mesh; }
    public Transform getTransform() { return transform; }
    public Map<Integer, Material> getMaterialsBySlot() { return materialsBySlot; }
}
