package EALiodufiowAMS2.engine.rendering.objectRenderers;


import EALiodufiowAMS2.engine.rendering.DrawCommand;
import EALiodufiowAMS2.engine.rendering.geometryRenderer.geometryRenderers.CuboidGeometryRenderer;
import EALiodufiowAMS2.engine.rendering.geometryRenderer.GeometryDrawData;
import EALiodufiowAMS2.engine.rendering.geometryRenderer.GeometryDrawSurface;
import EALiodufiowAMS2.engine.rendering.geometryRenderer.GeometryRenderer;
import EALiodufiowAMS2.engine.rendering.graphicsRenderers.RenderBackend;
import EALiodufiowAMS2.engine.rendering.renderingObject.Geometry;
import EALiodufiowAMS2.engine.rendering.renderingObject.Material;
import EALiodufiowAMS2.engine.rendering.renderingObject.MaterialBlendMode;
import EALiodufiowAMS2.engine.rendering.renderingObject.RenderingObject;
import EALiodufiowAMS2.engine.rendering.renderingObject.objects.SurfaceGeometry;
import EALiodufiowAMS2.engine.rendering.renderingObject.objects.cuboid.CuboidGeometry;
import EALiodufiowAMS2.helpers.Mesh;

import java.awt.*;
import java.util.*;
import java.util.List;


public final class DefaultRenderingObjectRenderer implements RenderingObjectRenderer {

    private final Map<Class<? extends Geometry>, GeometryRenderer<?>> geometryRenderers = new HashMap<>();

    public DefaultRenderingObjectRenderer() {
        registerGeometryRenderer(new CuboidGeometryRenderer());
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
        List<GeometryDrawSurface> surfaces = data.getSurfaces();
        if (surfaces.isEmpty()) return;

        int maxSlot = -1;
        for (GeometryDrawSurface s : surfaces) {
            maxSlot = Math.max(maxSlot, s.getMaterialSlot());
        }
        if (maxSlot < 0) return;

        Map<Integer, Material> materialsBySlot = new HashMap<>();

        if (geom instanceof SurfaceGeometry sg) {

            for (GeometryDrawSurface s : surfaces) {
                int slot = s.getMaterialSlot();
                Object surfaceId = s.getSurfaceId();
                Material mat = sg.getMaterialForSurface(surfaceId);

                if (mat != null) {
                    materialsBySlot.put(slot, mat);
                }
            }

        } else {
            Material defaultMaterial = new Material(new Color(0x88ffffff, true), MaterialBlendMode.TRANSPARENT);

            for (GeometryDrawSurface s : surfaces) {
                int slot = s.getMaterialSlot();
                materialsBySlot.put(slot, defaultMaterial);
            }
        }

        boolean anyMaterial = materialsBySlot.values().stream().anyMatch(Objects::nonNull);
        if (!anyMaterial) return;

        Material fallback = new Material(new Color(0x88ffffff, true), MaterialBlendMode.TRANSPARENT);

        for (int i = 0; i <= maxSlot; i++) {
            materialsBySlot.putIfAbsent(i, fallback);
        }

        DrawCommand cmd = new DrawCommand(
                data.getMesh(),
                object.getTransform(),
                materialsBySlot
        );

        outCommands.add(cmd);
    }

}

