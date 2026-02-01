package EALiodufiowAMS2.engine.rendering.renderingObject.objects.cuboid;

import EALiodufiowAMS2.engine.rendering.renderingObject.FaceType;
import EALiodufiowAMS2.engine.rendering.renderingObject.Material;
import EALiodufiowAMS2.engine.rendering.renderingObject.Surface;

public class CuboidSurface implements Surface {
    private final CuboidFaceType type; // e.g. for cuboid: "front", "back", "left", "right", "top", "bottom"
    private final Material material;

    public CuboidSurface(CuboidFaceType type, Material material) {
        this.type = type;
        this.material = material;
    }

    @Override
    public CuboidFaceType getType() {
        return type;
    }

    @Override
    public Material getMaterial() {
        return material;
    }
}
