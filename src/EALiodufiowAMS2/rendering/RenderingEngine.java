package EALiodufiowAMS2.rendering;

import EALiodufiowAMS2.rendering.graphicsRenderers.CpuBackend;
import EALiodufiowAMS2.rendering.graphicsRenderers.GpuBackend;
import EALiodufiowAMS2.rendering.graphicsRenderers.RenderBackend;
import EALiodufiowAMS2.rendering.renderers.CuboidRenderer;
import EALiodufiowAMS2.rendering.renderers.GeometryRenderer;
import EALiodufiowAMS2.rendering.renderingObject.*;
import EALiodufiowAMS2.rendering.renderingObject.geometries.Geometry;
import EALiodufiowAMS2.world.Camera;
import EALiodufiowAMS2.world.scenes.Scene;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;


// TODO: trzeba zrobić renderowanie linii dla torów. Copilot mówi, że z bezierem jest łatwo, bo po prostu rzutuje się punkty kontrolne. <-- LEGIT 100% potwierdzone XDD

    /* TODO globalne:
        ✅ ! naprawianie RenderingEngine
        ✅ !!!!!!!!!!!!!!!!! zmainić w transform aby nie używać eulerów tylko Matrix3 wszędzie dla obrotu. Ustawianie i toString może być eulerami ale dane matrix !!!!!!!!! To powinno naprawić odbijanie się lustrzane kostki przy obrocie https://copilot.microsoft.com/shares/eQ9T6bP2rMjyRVkj8Sf5V
        ✅ Vec3 metody nadpisują, a Matrix3 zwracają nowe i niespójne jest -> zmienić Vec3 i zaktualizować gdzie używane
        przepisać cały system torów: segmenty + renderer,
        VehicleRenderer,
        ✅ Game,
        ✅ Scene,
        ✅ World,
        zaimplementować actions do testów + PlayerVechicleController,
        T E S T O W A Ć,
        przygotować tekstury 120Na,
        zaimplementować SwitchSegment z działającym przełączaniem,
        drzwi w 120Na,
        różne kontrolery w Tram i co za tym idzie w 120NA (czuwak, drzwi, dzwonek, stacyjka),
        przystanki,
        skrzyżowania,
        poruszające się tło + inne obiekty dekoracyjne przy torach,

    */

public final class RenderingEngine {
    private Scene scene;
    private Camera camera;
    private final RenderBackend backend;
    private final Map<Class<? extends Geometry>, GeometryRenderer> renderers = new HashMap<>();

    public RenderingEngine(int resWidth, int resHeight) {
        //this.backend = new CpuBackend(resWidth, resHeight);
        this.backend = new GpuBackend(resWidth, resHeight);
        registerRenderer(new CuboidRenderer());
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }
    public void setScene(Scene scene) {
        this.scene = scene;
        this.backend.setScene(scene);
    }
    public void setBufferSize(int w, int h) { this.backend.resize(w, h);}

    public void registerRenderer(GeometryRenderer renderer) {
        renderers.put(renderer.getSupportedGeometry(), renderer);
    }

    public void renderFrame(Graphics2D g) {
        backend.beginFrame(camera);
        for (RenderingObject obj : scene.getObjects()) {
            Geometry geom = obj.getGeometry();
            GeometryRenderer r = renderers.get(geom.getClass());
            if (r != null) {
                r.render(backend, obj);
            }
        }
        backend.endFrame();
    }

    public BufferedImage getFrameBuffer() {
        return backend.getFrameBuffer();
    }
}
