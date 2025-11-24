package EALiodufiowAMS2.rendering.renderingObjects;

import EALiodufiowAMS2.helpers.Transform;
import EALiodufiowAMS2.rendering.FaceType;
import EALiodufiowAMS2.rendering.Surface;

import java.util.List;
import java.util.Objects;

public class Cuboid implements RenderingObject {
    private Transform transform;
    private List<Surface> surfaces;

    public Cuboid(Transform transform, List<Surface> surfaces) {
        this.transform = transform;
        this.surfaces = surfaces;
    }
    public Cuboid(Cuboid cuboid, Transform transform){
        this(transform, cuboid.getSurfaces());
    }

    @Override
    public Transform getTransform() {
        return transform;
    }

    @Override
    public String getType() {
        return "cuboid";
    }

    public Surface getSurface(FaceType type) {
        for (Surface s : surfaces) {
            if (Objects.equals(s.getType(), type)) {
                return s;
            }
        }
        return null;
    }
    public List<Surface> getSurfaces() {
        return surfaces;
    }

    public void addSurface(Surface surface) {
        removeSurface(surface.getType());
        surfaces.add(surface);
    }

    public void removeSurface(FaceType type) {
        surfaces.removeIf(s -> Objects.equals(s.getType(), type));
    }
}
