package EALiodufiowAMS2.engine.rendering.geometryRenderer.geometryRenderers.line;

import EALiodufiowAMS2.engine.rendering.DrawCommand;
import EALiodufiowAMS2.engine.rendering.objectRenderers.DefaultRenderingObjectRenderer;
import EALiodufiowAMS2.engine.rendering.renderingObject.Geometry;
import EALiodufiowAMS2.engine.rendering.renderingObject.RenderingObject;
import EALiodufiowAMS2.engine.rendering.renderingObject.objects.line.FillMode;
import EALiodufiowAMS2.engine.rendering.renderingObject.objects.line.Line;
import EALiodufiowAMS2.helpers.Matrix3;
import EALiodufiowAMS2.helpers.Quaternion;
import EALiodufiowAMS2.helpers.Transform;
import EALiodufiowAMS2.helpers.Vec3;

import java.util.List;

public final class GenericLineFillerPlacer implements LineFillerPlacer {

    @Override
    public void placeFiller(Line<?> line,
                            List<Vec3> polylineWorld,
                            double lengthWorld,
                            RenderingObject filler,
                            DefaultRenderingObjectRenderer defaultRenderer,
                            List<DrawCommand> outCommands) {
        if (filler == null) return;
        if (polylineWorld == null || polylineWorld.size() < 2) return;

        FillMode mode = line.getFillMode();

        switch (mode) {
            case START -> placeSingleAt(polylineWorld.get(0), directionAt(polylineWorld, 0), filler, defaultRenderer, outCommands);
            case END -> placeSingleAt(polylineWorld.get(polylineWorld.size() - 1),
                    directionAt(polylineWorld, polylineWorld.size() - 2),
                    filler, defaultRenderer, outCommands);
            case CENTER -> {
                Placement p = positionAtArcLength(polylineWorld, lengthWorld, lengthWorld * 0.5);
                placeSingleAt(p.position, p.direction, filler, defaultRenderer, outCommands);
            }
            case STRETCH -> placeStretched(polylineWorld, lengthWorld, filler, defaultRenderer, outCommands);
            case REPEAT -> placeRepeated(polylineWorld, lengthWorld, filler, defaultRenderer, outCommands);
        }
    }

    private static final class Placement {
        final Vec3 position;
        final Vec3 direction;

        Placement(Vec3 position, Vec3 direction) {
            this.position = position;
            this.direction = direction;
        }
    }

    private Placement positionAtArcLength(List<Vec3> pts, double totalLength, double s) {
        if (s <= 0.0) {
            return new Placement(pts.get(0), directionAt(pts, 0));
        }
        if (s >= totalLength) {
            int n = pts.size();
            return new Placement(pts.get(n - 1), directionAt(pts, n - 2));
        }

        double accumulated = 0.0;
        for (int i = 0; i < pts.size() - 1; i++) {
            Vec3 p0 = pts.get(i);
            Vec3 p1 = pts.get(i + 1);
            double segLen = p1.sub(p0).length();
            if (accumulated + segLen >= s) {
                double t = (s - accumulated) / segLen;
                Vec3 pos = p0.lerp(p1, t);
                Vec3 dir = p1.sub(p0).normalize();
                return new Placement(pos, dir);
            }
            accumulated += segLen;
        }
        int n = pts.size();
        return new Placement(pts.get(n - 1), directionAt(pts, n - 2));
    }

    private Vec3 directionAt(List<Vec3> pts, int index) {
        int i0 = Math.max(0, index);
        int i1 = Math.min(pts.size() - 1, index + 1);
        Vec3 p0 = pts.get(i0);
        Vec3 p1 = pts.get(i1);
        Vec3 d = p1.sub(p0);
        double len = d.length();
        if (len <= 1e-9) {
            return new Vec3(0, 1, 0);
        }
        return d.scale(1.0 / len);
    }

    private void placeSingleAt(Vec3 position,
                               Vec3 direction,
                               RenderingObject filler,
                               DefaultRenderingObjectRenderer defaultRenderer,
                               List<DrawCommand> outCommands) {
        Transform fillerLocal = filler.getTransform();
        Geometry fillerGeom = filler.getGeometry();

        Quaternion rot = orientationFromDirection(direction);

        // finalny transform fillera w świecie: najpierw lokalny filler, potem pozycja i orientacja na linii
        Transform placement = new Transform(position, rot, fillerLocal.getSize());
        Transform finalTransform = placement.combine(new Transform(new Vec3(0, 0, 0), fillerLocal.getRot(), new Vec3(1, 1, 1)));

        RenderingObject temp = new RenderingObject(fillerGeom, finalTransform);
        defaultRenderer.buildDrawCommands(null, temp, outCommands);
    }

    private void placeStretched(List<Vec3> pts,
                                double totalLength,
                                RenderingObject filler,
                                DefaultRenderingObjectRenderer defaultRenderer,
                                List<DrawCommand> outCommands) {
        if (totalLength <= 1e-9) {
            placeSingleAt(pts.get(0), directionAt(pts, 0), filler, defaultRenderer, outCommands);
            return;
        }

        Vec3 start = pts.get(0);
        Vec3 end = pts.get(pts.size() - 1);
        Vec3 dir = end.sub(start).normalize();

        Transform fillerLocal = filler.getTransform();
        Geometry fillerGeom = filler.getGeometry();

        Vec3 baseSize = fillerLocal.getSize();
        Vec3 stretchedSize = new Vec3(baseSize.x, totalLength, baseSize.z);

        Vec3 pos = start.lerp(end, 0.5);
        Quaternion rot = orientationFromDirection(dir);

        Transform placement = new Transform(pos, rot, stretchedSize);
        Transform finalTransform = placement.combine(new Transform(new Vec3(0, 0, 0), fillerLocal.getRot(), new Vec3(1, 1, 1)));

        RenderingObject temp = new RenderingObject(fillerGeom, finalTransform);
        defaultRenderer.buildDrawCommands(null, temp, outCommands);
    }

    private void placeRepeated(List<Vec3> pts,
                               double totalLength,
                               RenderingObject filler,
                               DefaultRenderingObjectRenderer defaultRenderer,
                               List<DrawCommand> outCommands) {
        if (totalLength <= 1e-9) {
            placeSingleAt(pts.get(0), directionAt(pts, 0), filler, defaultRenderer, outCommands);
            return;
        }

        Transform fillerLocal = filler.getTransform();
        Geometry fillerGeom = filler.getGeometry();
        Vec3 baseSize = fillerLocal.getSize();
        double segmentLength = baseSize.y; // przyjmujemy oś Y jako długość fillera

        if (segmentLength <= 1e-6) {
            placeSingleAt(pts.get(0), directionAt(pts, 0), filler, defaultRenderer, outCommands);
            return;
        }

        int count = (int) Math.floor(totalLength / segmentLength);
        if (count <= 0) {
            Placement p = positionAtArcLength(pts, totalLength, totalLength * 0.5);
            placeSingleAt(p.position, p.direction, filler, defaultRenderer, outCommands);
            return;
        }

        for (int i = 0; i < count; i++) {
            double s = (i + 0.5) * segmentLength;
            Placement p = positionAtArcLength(pts, totalLength, s);
            Vec3 pos = p.position;
            Vec3 dir = p.direction;

            Quaternion rot = orientationFromDirection(dir);
            Transform placement = new Transform(pos, rot, baseSize);
            Transform finalTransform = placement.combine(new Transform(new Vec3(0, 0, 0), fillerLocal.getRot(), new Vec3(1, 1, 1)));

            RenderingObject temp = new RenderingObject(fillerGeom, finalTransform);
            defaultRenderer.buildDrawCommands(null, temp, outCommands);
        }
    }

    private Quaternion orientationFromDirection(Vec3 dir) {
        Vec3 up = dir.normalize();
        Vec3 right = Math.abs(up.y) < 0.99 ? new Vec3(1, 0, 0) : new Vec3(0, 0, 1);
        Vec3 forward = up.cross(right).normalize();
        right = forward.cross(up).normalize();

        Matrix3 basis = Matrix3.fromBasis(right, up, forward);
        return Quaternion.fromMatrix3(basis);
    }
}
