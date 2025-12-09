package EALiodufiowAMS2.rendering.renderingObjects;

import EALiodufiowAMS2.helpers.Transform;
import EALiodufiowAMS2.helpers.Vec3;

public class Line implements RenderingObject {
    private final Vec3 start;
    private final Vec3 end;
    private final Transform transform; // opcjonalnie, np. do przesunięcia
    private final String type = "line";

    public Line(Vec3 start, Vec3 end) {
        this.start = start;
        this.end = end;
        this.transform = new Transform(); // domyślny
    }

    public Vec3 getStart() { return start; }
    public Vec3 getEnd() { return end; }

    @Override
    public Transform getTransform() { return transform; }
    @Override
    public String getType() { return type; }
}
