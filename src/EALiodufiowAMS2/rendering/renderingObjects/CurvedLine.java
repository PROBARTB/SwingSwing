package EALiodufiowAMS2.rendering.renderingObjects;

import EALiodufiowAMS2.helpers.*;

import java.awt.*;

public class CurvedLine implements Line {
    private final Vec3 startPosition;
    private final Quaternion startRotation;
    private final double startHandleLength;

    private final Vec3 endPosition;
    private final Quaternion endRotation;
    private final double endHandleLength;

    private final double curvatureFactor;
    private final Color color;

    public CurvedLine(Transform startPoint, Transform endPoint, double curvatureFactor, Color color) {
        this.startPosition = startPoint.getPos();
        this.startRotation = startPoint.getRot();
        this.startHandleLength = startPoint.getSize().x;
        this.endPosition = endPoint.getPos();
        this.endRotation = endPoint.getRot();
        this.endHandleLength = endPoint.getSize().x;

        this.curvatureFactor = curvatureFactor;
        this.color = color;
    }

    public Vec3 getStartPosition() { return startPosition; }
    public Quaternion getStartRotation() { return startRotation; }
    public double getStartHandleLength() { return startHandleLength; }
    public Vec3 getEndPosition() { return endPosition; }
    public Quaternion getEndRotation() { return endRotation; }
    public double getEndHandleLength() { return endHandleLength; }
    public double getCurvatureFactor() { return curvatureFactor; }

    @Override
    public Color getColor() { return color; }
    @Override
    public Transform getTransform() { return new Transform(startPosition, startRotation, new Vec3(startHandleLength, 0, 0)); } // start point as lines transform
    @Override
    public String getType() { return "CurvedLine"; }
}

