package EALiodufiowAMS2.engine.rendering.renderingObject.objects.cuboid;

import EALiodufiowAMS2.engine.rendering.renderingObject.*;
import EALiodufiowAMS2.engine.rendering.renderingObject.Geometry;
import EALiodufiowAMS2.engine.rendering.renderingObject.objects.SurfaceGeometry;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public final class CuboidGeometry implements Geometry, SurfaceGeometry {

    private final List<CuboidSurface> surfaces;
    private final Map<CuboidFaceType, Material> materialByFace;

    public CuboidGeometry(List<CuboidSurface> surfaces) {
        if (surfaces == null || surfaces.isEmpty()) {
            throw new IllegalArgumentException("CuboidGeometry requires at least one surface");
        }
        this.surfaces = List.copyOf(surfaces);

        Map<CuboidFaceType, Material> map = new EnumMap<>(CuboidFaceType.class);
        for (CuboidSurface s : surfaces) {
            map.put(s.getType(), s.getMaterial());
        }
        this.materialByFace = Map.copyOf(map);
    }

    public List<CuboidSurface> getSurfaces() {
        return surfaces;
    }

    @Override
    public Material getMaterialForSurface(Object surfaceId) {
        if (surfaceId instanceof CuboidFaceType faceType) {
            return materialByFace.get(faceType);
        }
        return null;
    }
}
