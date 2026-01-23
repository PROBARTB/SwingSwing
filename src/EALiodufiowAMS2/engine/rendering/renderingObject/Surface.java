package EALiodufiowAMS2.engine.rendering.renderingObject;

public class Surface {
    private final FaceType type; // e.g. for cuboid: "front", "back", "left", "right", "top", "bottom"
    private final Material material;

    public Surface(FaceType type, Material material) {
        this.type = type;
        this.material = material;
    }

    public FaceType getType() { return type; }
    public Material getMaterial() { return material; }
}
