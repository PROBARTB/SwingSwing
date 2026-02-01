package EALiodufiowAMS2.engine.rendering.renderingObject.objects.line;

import EALiodufiowAMS2.helpers.*;

public final class LineGeometrySample {

    private final Transform transform; // pos+rot+size
    private final Vec3 tangent;        // unit
    private final Vec3 normal;         // unit
    private final Vec3 binormal;       // unit
    private final double curvature;    // scalar curvature

    public LineGeometrySample(Transform transform,
                              Vec3 tangent,
                              Vec3 normal,
                              Vec3 binormal,
                              double curvature) {
        this.transform = transform;
        this.tangent = tangent;
        this.normal = normal;
        this.binormal = binormal;
        this.curvature = curvature;
    }

    public Transform getTransform() {
        return transform;
    }

    public Vec3 getTangent() {
        return tangent;
    }

    public Vec3 getNormal() {
        return normal;
    }

    public Vec3 getBinormal() {
        return binormal;
    }

    public double getCurvature() {
        return curvature;
    }
}
