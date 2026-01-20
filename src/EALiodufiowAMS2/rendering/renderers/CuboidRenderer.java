package EALiodufiowAMS2.rendering.renderers;


import EALiodufiowAMS2.helpers.Mesh;
import EALiodufiowAMS2.helpers.Transform;
import EALiodufiowAMS2.helpers.Vec3;
import EALiodufiowAMS2.helpers.Vertex;
import EALiodufiowAMS2.rendering.DrawCommand;
import EALiodufiowAMS2.rendering.PrimitiveType;
import EALiodufiowAMS2.rendering.graphicsRenderers.RenderBackend;
import EALiodufiowAMS2.rendering.renderingObject.*;
import EALiodufiowAMS2.rendering.renderingObject.geometries.CuboidGeometry;
import EALiodufiowAMS2.rendering.renderingObject.geometries.Geometry;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public final class CuboidRenderer implements GeometryRenderer {

    // Cache unit-cuboid mesh – współdzielony dla wszystkich instancji
    // Wymiary: x,z w [-0.5, 0.5], y w [0, 1] (bottom at y=0, top at y=1)
    private final Mesh unitCuboidMesh;

    public CuboidRenderer() {
        this.unitCuboidMesh = createUnitCuboidMesh();
    }

    @Override
    public Class<? extends Geometry> getSupportedGeometry() {
        return CuboidGeometry.class;
    }

    @Override
    public void render(RenderBackend backend, RenderingObject object) {
        CuboidGeometry geom = (CuboidGeometry) object.getGeometry();
        Transform transform = object.getTransform();

        // Dla każdej surface (może być tylko część ścian)
        for (Surface s : geom.getSurfaces()) {
            FaceType faceType = s.getType();
            Material material = s.getMaterial();

            DrawCommand cmd = new DrawCommand(
                    unitCuboidMesh,
                    PrimitiveType.TRIANGLES,
                    transform,
                    material,
                    faceType
            );
            backend.submit(cmd);
        }
    }

    private Mesh createUnitCuboidMesh() {
        List<Vertex> verts = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();
        Map<FaceType, Mesh.FaceRange> faceRanges = new EnumMap<>(FaceType.class);

        class FaceBuilder {
            int addFace(FaceType type, Vec3 p0, Vec3 p1, Vec3 p2, Vec3 p3) {
                int baseIndex = verts.size();

                // UV: (0,0), (1,0), (1,1), (0,1)
                verts.add(new Vertex(p0, 0.0, 0.0));
                verts.add(new Vertex(p1, 1.0, 0.0));
                verts.add(new Vertex(p2, 1.0, 1.0));
                verts.add(new Vertex(p3, 0.0, 1.0));

                int start = indices.size();
                indices.add(baseIndex    );
                indices.add(baseIndex + 1);
                indices.add(baseIndex + 2);
                indices.add(baseIndex    );
                indices.add(baseIndex + 2);
                indices.add(baseIndex + 3);
                int count = 6;

                faceRanges.put(type, new Mesh.FaceRange(type, start, count));
                return count;
            }
        }

        FaceBuilder fb = new FaceBuilder();

        // lokalne pozycje unit-cuboidu: x,z w [-0.5,0.5], y w [0,1]
        Vec3 bbl = new Vec3(-0.5, 0.0, -0.5); // bottom-back-left
        Vec3 bbr = new Vec3( 0.5, 0.0, -0.5);
        Vec3 bfr = new Vec3( 0.5, 0.0,  0.5);
        Vec3 bfl = new Vec3(-0.5, 0.0,  0.5);

        Vec3 tbl = new Vec3(-0.5, 1.0, -0.5);
        Vec3 tbr = new Vec3( 0.5, 1.0, -0.5);
        Vec3 tfr = new Vec3( 0.5, 1.0,  0.5);
        Vec3 tfl = new Vec3(-0.5, 1.0,  0.5);

//        // FRONT (z > 0): bfl, bfr, tfr, tfl
//        fb.addFace(FaceType.FRONT, bfl, bfr, tfr, tfl);
//
//        // BACK (z < 0): bbr, bbl, tbl, tbr
//        fb.addFace(FaceType.BACK, bbr, bbl, tbl, tbr);
//
//        // LEFT (x < 0): bbl, bfl, tfl, tbl
//        fb.addFace(FaceType.LEFT, bbl, bfl, tfl, tbl);
//
//        // RIGHT (x > 0): bfr, bbr, tbr, tfr
//        fb.addFace(FaceType.RIGHT, bfr, bbr, tbr, tfr);
//
//        // TOP (y = 1): tbl, tbr, tfr, tfl
//        fb.addFace(FaceType.TOP, tbl, tbr, tfr, tfl);
//
//        // BOTTOM (y = 0): bbl, bbr, bfr, bfl
//        fb.addFace(FaceType.BOTTOM, bbl, bbr, bfr, bfl);
        // FRONT (z < 0): bbl, bbr, tbr, tbl
        fb.addFace(FaceType.FRONT, bbl, bbr, tbr, tbl);

// BACK (z > 0): bfr, bfl, tfl, tfr
        fb.addFace(FaceType.BACK, bfr, bfl, tfl, tfr);

// LEFT (x < 0): bbl, bfl, tfl, tbl
        fb.addFace(FaceType.LEFT, bbl, bfl, tfl, tbl);

// RIGHT (x > 0): bfr, bbr, tbr, tfr
        fb.addFace(FaceType.RIGHT, bfr, bbr, tbr, tfr);

// TOP (y = 1): tbl, tbr, tfr, tfl
        fb.addFace(FaceType.TOP, tbl, tbr, tfr, tfl);

// BOTTOM (y = 0): bbl, bbr, bfr, bfl
        fb.addFace(FaceType.BOTTOM, bbl, bbr, bfr, bfl);


        Vertex[] vArr = verts.toArray(new Vertex[0]);
        int[] iArr = indices.stream().mapToInt(Integer::intValue).toArray();

        return new Mesh(vArr, iArr, faceRanges);
    }

}
