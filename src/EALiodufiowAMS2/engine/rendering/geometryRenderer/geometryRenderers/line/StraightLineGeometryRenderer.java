package EALiodufiowAMS2.engine.rendering.geometryRenderer.geometryRenderers.line;


import EALiodufiowAMS2.engine.rendering.renderingObject.objects.cuboid.FaceType;
import EALiodufiowAMS2.engine.rendering.renderingObject.objects.line.lines.StraightLineGeometry;
import EALiodufiowAMS2.helpers.Mesh;
import EALiodufiowAMS2.helpers.Transform;
import EALiodufiowAMS2.helpers.Vec3;
import EALiodufiowAMS2.helpers.Vertex;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class StraightLineGeometryRenderer implements LineGeometryRenderer<StraightLineGeometry> {

    @Override
    public Class<StraightLineGeometry> getSupportedGeometry() {
        return StraightLineGeometry.class;
    }

    @Override
    public LineRenderData buildRenderData(StraightLineGeometry geometry) {
        Transform s = geometry.getStart();
        Transform e = geometry.getEnd();

        Vec3 p0 = s.getPos();
        Vec3 p1 = e.getPos();

        Vertex v0 = new Vertex(p0, 0.0, 0.0);
        Vertex v1 = new Vertex(p1, 1.0, 0.0);
        Vertex[] vertices = new Vertex[]{v0, v1};
        int[] indices = new int[]{0, 1};

        Map<FaceType, Mesh.FaceRange> emptyRanges = Collections.emptyMap();
        Mesh lineMesh = new Mesh(vertices, indices, emptyRanges);

        List<Vec3> polyline = List.of(p0, p1);
        double length = p1.sub(p0).length();

        return new LineRenderData(lineMesh, polyline, length);
    }
}
