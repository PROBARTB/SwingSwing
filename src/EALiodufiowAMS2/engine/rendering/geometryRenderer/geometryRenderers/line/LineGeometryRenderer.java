package EALiodufiowAMS2.engine.rendering.geometryRenderer.geometryRenderers.line;


import EALiodufiowAMS2.engine.rendering.renderingObject.objects.line.LineGeometry;

public interface LineGeometryRenderer<G extends LineGeometry> {

    Class<G> getSupportedGeometry();

    LineRenderData buildRenderData(G geometry);
}
