package EALiodufiowAMS2.engine.rendering.geometryRenderer.geometryRenderers;


import EALiodufiowAMS2.engine.rendering.PrimitiveType;
import EALiodufiowAMS2.engine.rendering.geometryRenderer.GeometryDrawData;
import EALiodufiowAMS2.engine.rendering.geometryRenderer.GeometryDrawSurface;
import EALiodufiowAMS2.engine.rendering.geometryRenderer.GeometryRenderer;
import EALiodufiowAMS2.engine.rendering.renderingObject.objects.cuboid.CuboidGeometry;
import EALiodufiowAMS2.engine.rendering.renderingObject.objects.cuboid.FaceType;
import EALiodufiowAMS2.helpers.Mesh;
import EALiodufiowAMS2.helpers.Vec3;
import EALiodufiowAMS2.helpers.Vertex;

import java.util.*;


public final class CuboidGeometryRenderer implements GeometryRenderer<CuboidGeometry> {

    private final Mesh unitCuboidMesh;
    private final Map<FaceType, Mesh.FaceRange> faceRanges;

    public CuboidGeometryRenderer() {
        Map<FaceType, Mesh.FaceRange> ranges = new EnumMap<>(FaceType.class);
        this.unitCuboidMesh = createUnitCuboidMesh(ranges);
        this.faceRanges = Collections.unmodifiableMap(ranges);
    }

    @Override
    public Class<CuboidGeometry> getSupportedGeometry() {
        return CuboidGeometry.class;
    }

    @Override
    public GeometryDrawData buildDrawData(CuboidGeometry geometry) {
        List<GeometryDrawSurface> surfaces = new ArrayList<>();
        for (Map.Entry<FaceType, Mesh.FaceRange> e : faceRanges.entrySet()) {
            FaceType faceType = e.getKey();
            Mesh.FaceRange range = e.getValue();
            surfaces.add(new GeometryDrawSurface(range, faceType));
        }
        return new GeometryDrawData(unitCuboidMesh, PrimitiveType.TRIANGLES, surfaces);
    }

    private Mesh createUnitCuboidMesh(Map<FaceType, Mesh.FaceRange> faceRanges) {
        List<Vertex> verts = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();

        class FaceBuilder {
            void addFace(FaceType type, Vec3 p0, Vec3 p1, Vec3 p2, Vec3 p3) {
                int baseIndex = verts.size();

                verts.add(new Vertex(p0, 0.0, 0.0));
                verts.add(new Vertex(p1, 1.0, 0.0));
                verts.add(new Vertex(p2, 1.0, 1.0));
                verts.add(new Vertex(p3, 0.0, 1.0));

                int start = indices.size();
                indices.add(baseIndex);
                indices.add(baseIndex + 1);
                indices.add(baseIndex + 2);
                indices.add(baseIndex);
                indices.add(baseIndex + 2);
                indices.add(baseIndex + 3);
                int count = 6;

                faceRanges.put(type, new Mesh.FaceRange(type, start, count));
            }
        }

        FaceBuilder fb = new FaceBuilder();

        Vec3 bbl = new Vec3(-0.5, 0.0, -0.5);
        Vec3 bbr = new Vec3(0.5, 0.0, -0.5);
        Vec3 bfr = new Vec3(0.5, 0.0, 0.5);
        Vec3 bfl = new Vec3(-0.5, 0.0, 0.5);

        Vec3 tbl = new Vec3(-0.5, 1.0, -0.5);
        Vec3 tbr = new Vec3(0.5, 1.0, -0.5);
        Vec3 tfr = new Vec3(0.5, 1.0, 0.5);
        Vec3 tfl = new Vec3(-0.5, 1.0, 0.5);

        fb.addFace(FaceType.FRONT, bbl, bbr, tbr, tbl);
        fb.addFace(FaceType.BACK, bfr, bfl, tfl, tfr);
        fb.addFace(FaceType.LEFT, bbl, bfl, tfl, tbl);
        fb.addFace(FaceType.RIGHT, bfr, bbr, tbr, tfr);
        fb.addFace(FaceType.TOP, tbl, tbr, tfr, tfl);
        fb.addFace(FaceType.BOTTOM, bbl, bbr, bfr, bfl);

        Vertex[] vArr = verts.toArray(new Vertex[0]);
        int[] iArr = indices.stream().mapToInt(Integer::intValue).toArray();

        return new Mesh(vArr, iArr, faceRanges);
    }
}



