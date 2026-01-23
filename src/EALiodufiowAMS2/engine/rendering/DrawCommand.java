package EALiodufiowAMS2.engine.rendering;


import EALiodufiowAMS2.helpers.Mesh;
import EALiodufiowAMS2.helpers.Transform;
import EALiodufiowAMS2.engine.rendering.renderingObject.FaceType;
import EALiodufiowAMS2.engine.rendering.renderingObject.Material;

public final class DrawCommand {
    private final Mesh mesh;
    private final PrimitiveType primitiveType;
    private final Transform transform;
    private final Material material;
    private final FaceType faceType;

    public DrawCommand(Mesh mesh,
                       PrimitiveType primitiveType,
                       Transform transform,
                       Material material,
                       FaceType faceType) {
        this.mesh = mesh;
        this.primitiveType = primitiveType;
        this.transform = transform;
        this.material = material;
        this.faceType = faceType;
    }

    public Mesh getMesh() { return mesh; }
    public PrimitiveType getPrimitiveType() { return primitiveType; }
    public Transform getTransform() { return transform; }
    public Material getMaterial() { return material; }
    public FaceType getFaceType() { return faceType; }
}