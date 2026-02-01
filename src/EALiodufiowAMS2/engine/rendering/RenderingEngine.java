package EALiodufiowAMS2.engine.rendering;

import EALiodufiowAMS2.engine.rendering.graphicsRenderers.cpu.CpuBackend;
import EALiodufiowAMS2.engine.rendering.graphicsRenderers.gpu.GpuBackend;
import EALiodufiowAMS2.engine.rendering.graphicsRenderers.RenderBackend;
import EALiodufiowAMS2.engine.rendering.objectRenderers.DefaultRenderingObjectRenderer;
import EALiodufiowAMS2.engine.rendering.objectRenderers.LineRenderingObjectRenderer;
import EALiodufiowAMS2.engine.rendering.objectRenderers.RenderingObjectRenderer;
import EALiodufiowAMS2.engine.rendering.renderViews.CpuRenderView;
import EALiodufiowAMS2.engine.rendering.renderViews.GpuRenderView;
import EALiodufiowAMS2.engine.rendering.renderViews.RenderView;
import EALiodufiowAMS2.engine.rendering.renderingObject.*;
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

    private final Map<Class<? extends RenderingObject>, RenderingObjectRenderer> objectRenderers = new HashMap<>();

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

        DefaultRenderingObjectRenderer defaultRenderer = new DefaultRenderingObjectRenderer();
        LineRenderingObjectRenderer lineRenderer = new LineRenderingObjectRenderer(defaultRenderer);

        registerRenderer(defaultRenderer);
        registerRenderer(lineRenderer);
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

    public RenderingMode getRenderingMode() {
        return this.backend.getRenderingMode();
    }

    public int getWidth() {
        return this.backend.getWidth();
    }

    public int getHeight() {
        return this.backend.getHeight();
    }

    public GpuInfo getCurrentGpuInfo() {
        if (!(this.backend instanceof GpuBackend gpuBackend))
            throw new IllegalStateException("Rendering mode has to be GPU in case to get currently used GPU Info");
        return gpuBackend.getGpuInfo();
    }

    public void registerRenderer(RenderingObjectRenderer renderer) {
        objectRenderers.put(renderer.getSupportedType(), renderer);
    }

    private RenderingObjectRenderer findRendererFor(RenderingObject obj) {
        Class<?> cls = obj.getClass();
        // najpierw próbujemy dokładny typ
        RenderingObjectRenderer r = objectRenderers.get(cls);
        if (r != null) return r;

        // fallback – szukamy rendererów dla nadtypów (np. RenderingObject.class)
        for (Map.Entry<Class<? extends RenderingObject>, RenderingObjectRenderer> e : objectRenderers.entrySet()) {
            if (e.getKey().isAssignableFrom(cls)) {
                return e.getValue();
            }
        }
        return null;
    }

    public void renderFrame() {
        if (scene == null || camera == null) return;

        List<DrawCommand> commands = new ArrayList<>();

        for (RenderingObject obj : scene.getObjects()) {
            RenderingObjectRenderer renderer = findRendererFor(obj);
            if (renderer != null) {
                renderer.buildDrawCommands(backend, obj, commands);
            }
        }

        backend.beginFrame(camera);
        for (DrawCommand cmd : commands) {
            backend.submit(cmd);
        }
        backend.endFrame();
    }

    public RenderView getRenderView() {
        return renderView;
    }

    public BufferedImage getFrameSnapshot() {
        return backend.getFrameBuffer();
    } // should only be used for occasional fetching
}
