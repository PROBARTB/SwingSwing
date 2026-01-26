package EALiodufiowAMS2.engine.rendering;

import EALiodufiowAMS2.engine.rendering.graphicsRenderers.CpuBackend;
import EALiodufiowAMS2.engine.rendering.graphicsRenderers.GpuBackend;
import EALiodufiowAMS2.engine.rendering.graphicsRenderers.RenderBackend;
import EALiodufiowAMS2.engine.rendering.renderViews.CpuRenderView;
import EALiodufiowAMS2.engine.rendering.renderViews.GpuRenderView;
import EALiodufiowAMS2.engine.rendering.renderViews.RenderView;
import EALiodufiowAMS2.engine.rendering.renderers.CuboidRenderer;
import EALiodufiowAMS2.engine.rendering.renderers.GeometryRenderer;
import EALiodufiowAMS2.engine.rendering.renderingObject.*;
import EALiodufiowAMS2.engine.rendering.renderingObject.geometries.Geometry;
import EALiodufiowAMS2.engine.Camera;
import EALiodufiowAMS2.engine.Scene;

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
    private RenderingEngineListener listener;

    private Scene scene;
    private Camera camera;
    private final RenderBackend backend;
    private final RenderView renderView;
    private final Map<Class<? extends Geometry>, GeometryRenderer> renderers = new HashMap<>();

    public RenderingEngine(int resWidth, int resHeight, RenderingMode renderingMode) {
        if (renderingMode == RenderingMode.GPU) {
            GpuBackend gpu = new GpuBackend(resWidth, resHeight);
            this.backend = gpu;
            this.renderView = new GpuRenderView(gpu);
        } else {
            CpuBackend cpu = new CpuBackend(resWidth, resHeight);
            this.backend = cpu;
            this.renderView = new CpuRenderView(cpu);
        }
        registerRenderer(new CuboidRenderer());

    }

    public void setListener(RenderingEngineListener listener) {
        this.listener = listener;
        this.backend.setListener(listener);
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public void setScene(Scene scene) {
        this.scene = scene;
        this.backend.setScene(scene);
    }

    public void resize(int w, int h) {
        this.backend.resize(w, h);
    }

    public RenderingMode getRenderingMode(){ return this.backend.getRenderingMode(); }
    public int getWidth(){ return this.backend.getWidth(); }
    public int getHeight(){ return this.backend.getHeight(); }

    public GpuInfo getCurrentGpuInfo() {
        if (!(this.backend instanceof GpuBackend gpuBackend)) throw new IllegalStateException("Rendering mode has to be GPU in case to get currently used GPU Info");
        return gpuBackend.getGpuInfo();
    }

    public void registerRenderer(GeometryRenderer renderer) {
        renderers.put(renderer.getSupportedGeometry(), renderer);
    }

    public void renderFrame() {
        if (scene == null || camera == null) return;
//        System.out.println("AA" + scene.getObjects());

        backend.beginFrame(camera);
        for (RenderingObject obj : scene.getObjects()) {
//            System.out.println(obj.getTransform().toString());
            Geometry geom = obj.getGeometry();
            GeometryRenderer r = renderers.get(geom.getClass());
            if (r != null) {
                r.render(backend, obj);
            }
        }
        backend.endFrame();
    }

    public RenderView getRenderView() {
        return renderView;
    }

    public BufferedImage getFrameSnapshot() { return backend.getFrameBuffer(); } // should only be used for occasional fetching
}
