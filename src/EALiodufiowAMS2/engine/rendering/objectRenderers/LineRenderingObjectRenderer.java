package EALiodufiowAMS2.engine.rendering.objectRenderers;

// Renderer obiektów typu Line – rysuje nitkę i filler

import EALiodufiowAMS2.engine.rendering.DrawCommand;
import EALiodufiowAMS2.engine.rendering.PrimitiveType;
import EALiodufiowAMS2.engine.rendering.geometryRenderer.geometryRenderers.line.*;
import EALiodufiowAMS2.engine.rendering.graphicsRenderers.RenderBackend;
import EALiodufiowAMS2.engine.rendering.renderingObject.Material;
import EALiodufiowAMS2.engine.rendering.renderingObject.RenderingObject;
import EALiodufiowAMS2.engine.rendering.renderingObject.objects.line.Line;
import EALiodufiowAMS2.engine.rendering.renderingObject.objects.line.LineGeometry;
import EALiodufiowAMS2.engine.rendering.renderingObject.objects.line.lines.StraightLineGeometry;
import EALiodufiowAMS2.helpers.Matrix4;
import EALiodufiowAMS2.helpers.Mesh;
import EALiodufiowAMS2.helpers.Transform;
import EALiodufiowAMS2.helpers.Vec3;

import java.awt.*;
import java.util.*;
import java.util.List;

public final class LineRenderingObjectRenderer implements RenderingObjectRenderer {

    private final DefaultRenderingObjectRenderer defaultRenderer;
    private final Map<Class<? extends LineGeometry>, LineGeometryRenderer<?>> lineGeometryRenderers = new HashMap<>();
    private final LineFillerPlacer fillerPlacer;

    public LineRenderingObjectRenderer(DefaultRenderingObjectRenderer defaultRenderer) {
        this.defaultRenderer = defaultRenderer;
        this.fillerPlacer = new GenericLineFillerPlacer();

        registerLineGeometryRenderer(new StraightLineGeometryRenderer());
    }

    @Override
    public Class<? extends RenderingObject> getSupportedType() {
        return Line.class;
    }

    public void registerLineGeometryRenderer(LineGeometryRenderer<?> renderer) {
        lineGeometryRenderers.put(renderer.getSupportedGeometry(), renderer);
    }

    @SuppressWarnings("unchecked")
    private <G extends LineGeometry> LineGeometryRenderer<G> findLineGeometryRenderer(G geometry) {
        Class<?> cls = geometry.getClass();
        LineGeometryRenderer<?> r = lineGeometryRenderers.get(cls);
        if (r != null) {
            return (LineGeometryRenderer<G>) r;
        }
        for (Map.Entry<Class<? extends LineGeometry>, LineGeometryRenderer<?>> e : lineGeometryRenderers.entrySet()) {
            if (e.getKey().isAssignableFrom(cls)) {
                return (LineGeometryRenderer<G>) e.getValue();
            }
        }
        return null;
    }

    @Override
    public void buildDrawCommands(RenderBackend backend,
                                  RenderingObject object,
                                  List<DrawCommand> outCommands) {
        if (!(object instanceof Line<?> line)) {
            return;
        }

        LineGeometry lineGeometry = line.getLineGeometry();
        if (lineGeometry == null) {
            return;
        }

        LineGeometryRenderer<LineGeometry> renderer = findLineGeometryRenderer(lineGeometry);
        if (renderer == null) {
            return;
        }

        LineRenderData data = renderer.buildRenderData(lineGeometry);

        java.awt.Color lineColor = line.getLineColor();
        if (lineColor != null && data.getLineMesh() != null) {
            Mesh mesh = data.getLineMesh();
            Material lineMaterial = new Material(lineColor);

            int maxSlot = 0;
            for (Mesh.SubMesh sm : mesh.getSubMeshes()) {
                maxSlot = Math.max(maxSlot, sm.materialSlot);
            }

            Map<Integer, Material> materialsBySlot = new HashMap<>();
            for (int i = 0; i <= maxSlot; i++) {
                materialsBySlot.put(i, lineMaterial);
            }

            DrawCommand lineCmd = new DrawCommand(
                    mesh,
                    line.getTransform(),
                    materialsBySlot
            );
            outCommands.add(lineCmd);
        }

        RenderingObject filler = line.getFiller();
        if (filler != null) {
            List<Vec3> polylineLocal = data.getPolylineLocal();
            Transform lineTransform = line.getTransform();

            List<Vec3> polylineWorld = new ArrayList<>(polylineLocal.size());
            for (Vec3 p : polylineLocal) {
                polylineWorld.add(lineTransform.toModelMatrix().transformPoint(p));
            }

            double lengthWorld = 0.0;
            for (int i = 0; i < polylineWorld.size() - 1; i++) {
                lengthWorld += polylineWorld.get(i + 1).sub(polylineWorld.get(i)).length();
            }

            fillerPlacer.placeFiller(line, polylineWorld, lengthWorld, filler, defaultRenderer, outCommands);
        }
    }
}
