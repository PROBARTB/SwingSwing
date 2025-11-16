package EALiodufiowAMS2.rendering;

import EALiodufiowAMS2.helpers.Transform;

import java.util.List;
import java.util.Objects;

public class Cuboid implements RenderingObject {
    private Transform transform;
    private List<Surface> surfaces;

    public Cuboid(Transform transform, List<Surface> surfaces) {
        this.transform = transform;
        this.surfaces = surfaces;
    }

    @Override
    public Transform getTransform() {
        return transform;
    }

    public Surface getSurface(String type) {
        for (Surface s : surfaces) {
            if (Objects.equals(s.getType(), type)) {
                return s;
            }
        }
        return null;
    }

    public void addSurface(Surface surface) {
        removeSurface(surface.getType());
        surfaces.add(surface);
    }

    public void removeSurface(String type) {
        surfaces.removeIf(s -> Objects.equals(s.getType(), type));
    }
}
