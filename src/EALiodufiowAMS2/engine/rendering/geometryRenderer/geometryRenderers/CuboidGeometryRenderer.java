package EALiodufiowAMS2.engine.rendering.geometryRenderer.geometryRenderers;


import EALiodufiowAMS2.engine.rendering.PrimitiveType;
import EALiodufiowAMS2.engine.rendering.geometryRenderer.GeometryDrawData;
import EALiodufiowAMS2.engine.rendering.geometryRenderer.GeometryDrawSurface;
import EALiodufiowAMS2.engine.rendering.geometryRenderer.GeometryRenderer;
import EALiodufiowAMS2.engine.rendering.renderingObject.objects.cuboid.CuboidFaceType;
import EALiodufiowAMS2.engine.rendering.renderingObject.objects.cuboid.CuboidGeometry;
import EALiodufiowAMS2.engine.rendering.renderingObject.FaceType;
import EALiodufiowAMS2.helpers.BoundingBox;
import EALiodufiowAMS2.helpers.Mesh;
import EALiodufiowAMS2.helpers.Vec3;
import EALiodufiowAMS2.helpers.Vertex;

import java.util.*;


public final class CuboidGeometryRenderer implements GeometryRenderer<CuboidGeometry> {

    private final Mesh unitCuboidMesh;
    private final List<GeometryDrawSurface> surfacesTemplate;

    public CuboidGeometryRenderer() {
        List<GeometryDrawSurface> surfacesOut = new ArrayList<>();
        this.unitCuboidMesh = createUnitCuboidMesh(surfacesOut);
        this.surfacesTemplate = List.copyOf(surfacesOut);
    }

    @Override
    public Class<CuboidGeometry> getSupportedGeometry() {
        return CuboidGeometry.class;
    }

    @Override
    public GeometryDrawData buildDrawData(CuboidGeometry geometry) {
        return new GeometryDrawData(unitCuboidMesh, surfacesTemplate);
    }

    private Mesh createUnitCuboidMesh(List<GeometryDrawSurface> surfacesOut) {
        List<Vertex> verts = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();
        List<Mesh.SubMesh> subMeshes = new ArrayList<>();

        class FaceBuilder {
            int currentMaterialSlot = 0;
            int currentSubMeshIndex = 0;

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

                BoundingBox bounds = BoundingBox.computeSubMeshBounds(verts.toArray(new Vertex[0]), indices.toArray(new Integer[0]), start, count);

                int materialSlot = currentMaterialSlot++;
                Mesh.SubMesh sm = new Mesh.SubMesh(
                        start,
                        count,
                        PrimitiveType.TRIANGLES,
                        materialSlot,
                        bounds
                );
                subMeshes.add(sm);

                int subMeshIndex = currentSubMeshIndex++;
                surfacesOut.add(new GeometryDrawSurface(subMeshIndex, materialSlot, type));
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

        fb.addFace(CuboidFaceType.FRONT, bbl, bbr, tbr, tbl);
        fb.addFace(CuboidFaceType.BACK, bfr, bfl, tfl, tfr);
        fb.addFace(CuboidFaceType.LEFT, bbl, bfl, tfl, tbl);
        fb.addFace(CuboidFaceType.RIGHT, bfr, bbr, tbr, tfr);
        fb.addFace(CuboidFaceType.TOP, tbl, tbr, tfr, tfl);
        fb.addFace(CuboidFaceType.BOTTOM, bbl, bbr, bfr, bfl);

        Vertex[] vArr = verts.toArray(new Vertex[0]);
        int[] iArr = indices.stream().mapToInt(Integer::intValue).toArray();

        return new Mesh(vArr, iArr, subMeshes);
    }
}




