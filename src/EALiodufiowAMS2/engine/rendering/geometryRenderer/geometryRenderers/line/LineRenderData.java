package EALiodufiowAMS2.engine.rendering.geometryRenderer.geometryRenderers.line;


import EALiodufiowAMS2.helpers.Mesh;
import EALiodufiowAMS2.helpers.Vec3;

import java.util.List;

public final class LineRenderData {

    private final Mesh lineMesh;
    private final List<Vec3> polylineLocal;
    private final double length;

    public LineRenderData(Mesh lineMesh,
                          List<Vec3> polylineLocal,
                          double length) {
        if (polylineLocal == null || polylineLocal.size() < 2) {
            throw new IllegalArgumentException("polylineLocal must have at least 2 points");
        }
        this.lineMesh = lineMesh;
        this.polylineLocal = List.copyOf(polylineLocal);
        this.length = length;
    }

    public Mesh getLineMesh() {
        return lineMesh;
    }

    public List<Vec3> getPolylineLocal() {
        return polylineLocal;
    }

    public double getLength() {
        return length;
    }
}
