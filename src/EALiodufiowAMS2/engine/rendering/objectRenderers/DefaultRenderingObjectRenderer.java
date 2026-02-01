package EALiodufiowAMS2.engine.rendering.objectRenderers;


import EALiodufiowAMS2.engine.rendering.DrawCommand;
import EALiodufiowAMS2.engine.rendering.geometryRenderer.geometryRenderers.CuboidGeometryRenderer;
import EALiodufiowAMS2.engine.rendering.geometryRenderer.GeometryDrawData;
import EALiodufiowAMS2.engine.rendering.geometryRenderer.GeometryDrawSurface;
import EALiodufiowAMS2.engine.rendering.geometryRenderer.GeometryRenderer;
import EALiodufiowAMS2.engine.rendering.graphicsRenderers.RenderBackend;
import EALiodufiowAMS2.engine.rendering.renderingObject.Geometry;
import EALiodufiowAMS2.engine.rendering.renderingObject.Material;
import EALiodufiowAMS2.engine.rendering.renderingObject.RenderingObject;
import EALiodufiowAMS2.engine.rendering.renderingObject.objects.SurfaceGeometry;
import EALiodufiowAMS2.helpers.Mesh;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public final class DefaultRenderingObjectRenderer implements RenderingObjectRenderer {

    private final Map<Class<? extends Geometry>, GeometryRenderer<?>> geometryRenderers = new HashMap<>();

    public DefaultRenderingObjectRenderer() {
        // rejestracja geometry rendererów
        registerGeometryRenderer(new CuboidGeometryRenderer());
        // w przyszłości: registerGeometryRenderer(new SphereGeometryRenderer()); itd.
    }

    @Override
    public Class<? extends RenderingObject> getSupportedType() {
        return RenderingObject.class;
    }

    public void registerGeometryRenderer(GeometryRenderer<?> renderer) {
        geometryRenderers.put(renderer.getSupportedGeometry(), renderer);
    }

    @SuppressWarnings("unchecked")
    private <G extends Geometry> GeometryRenderer<G> findGeometryRenderer(G geometry) {
        Class<?> cls = geometry.getClass();
        GeometryRenderer<?> r = geometryRenderers.get(cls);
        if (r != null) {
            return (GeometryRenderer<G>) r;
        }
        for (Map.Entry<Class<? extends Geometry>, GeometryRenderer<?>> e : geometryRenderers.entrySet()) {
            if (e.getKey().isAssignableFrom(cls)) {
                return (GeometryRenderer<G>) e.getValue();
            }
        }
        return null;
    }

    @Override
    public void buildDrawCommands(RenderBackend backend,
                                  RenderingObject object,
                                  List<DrawCommand> outCommands) {
        Geometry geom = object.getGeometry();
        if (geom == null) return;

        GeometryRenderer<Geometry> renderer = findGeometryRenderer(geom);
        if (renderer == null) return;

        GeometryDrawData data = renderer.buildDrawData(geom);

        Map<Mesh.FaceRange, Material> materialByRange = new HashMap<>();

        // mapowanie surfaceId -> Material z geometrii
        // zakładamy, że Geometry ma metodę getSurfaces() zwracającą Surface z identyfikatorem (np. FaceType)
        if (geom instanceof SurfaceGeometry surfaceGeometry) {
            // SurfaceGeometry to opcjonalny interfejs, który może mieć CuboidGeometry
            for (GeometryDrawSurface s : data.getSurfaces()) {
                Object id = s.getSurfaceId();
                Material mat = surfaceGeometry.getMaterialForSurface(id);
                if (mat != null) {
                    materialByRange.put(s.getFaceRange(), mat);
                }
            }
        } else {
            // fallback: jeśli geometra nie implementuje SurfaceGeometry,
            // można przypisać jeden domyślny materiał (np. z samej geometrii)
            Material defaultMaterial = new Material(new Color(0x88ffffff, true));
            for (GeometryDrawSurface s : data.getSurfaces()) {
                materialByRange.put(s.getFaceRange(), defaultMaterial);
            }
        }

        if (materialByRange.isEmpty()) {
            return;
        }

        DrawCommand cmd = new DrawCommand(
                data.getMesh(),
                data.getPrimitiveType(),
                object.getTransform(),
                materialByRange
        );
        outCommands.add(cmd);
    }
}


