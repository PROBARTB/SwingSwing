package EALiodufiowAMS2.general.settings;

import EALiodufiowAMS2.engine.rendering.RenderingMode;

public final class GraphicsSettings {

    private final boolean showFps;
    private final boolean showDebugInfo;
    private final int fov;
    private final RenderingMode renderingMode;
    private final int resolutionWidth;
    private final int resolutionHeight;
    private final boolean fullscreen;

    public GraphicsSettings(
            boolean showFps,
            boolean showDebugInfo,
            int fov,
            RenderingMode renderingMode,
            int resolutionWidth,
            int resolutionHeight,
            boolean fullscreen
    ) {
        this.showFps = showFps;
        this.showDebugInfo = showDebugInfo;
        this.fov = fov;
        this.renderingMode = renderingMode;
        this.resolutionWidth = resolutionWidth;
        this.resolutionHeight = resolutionHeight;
        this.fullscreen = fullscreen;
    }

    public boolean isShowFps() {
        return showFps;
    }
    public boolean isShowDebugInfo() { return showDebugInfo; }

    public int getFov() {
        return fov;
    }

    public RenderingMode getRenderingMode() {
        return renderingMode;
    }

    public int getResolutionWidth() {
        return resolutionWidth;
    }

    public int getResolutionHeight() {
        return resolutionHeight;
    }

    public boolean isFullscreen() {
        return fullscreen;
    }

    public GraphicsSettings withShowFps(boolean showFps) {
        return new GraphicsSettings(showFps, showDebugInfo, fov, renderingMode, resolutionWidth, resolutionHeight, fullscreen);
    }

    public GraphicsSettings withFov(int fov) {
        return new GraphicsSettings(showFps, showDebugInfo, fov, renderingMode, resolutionWidth, resolutionHeight, fullscreen);
    }

    public GraphicsSettings withRenderingMode(RenderingMode mode) {
        return new GraphicsSettings(showFps, showDebugInfo, fov, mode, resolutionWidth, resolutionHeight, fullscreen);
    }

    public GraphicsSettings withResolution(int width, int height) {
        return new GraphicsSettings(showFps, showDebugInfo, fov, renderingMode, width, height, fullscreen);
    }

    public GraphicsSettings withFullscreen(boolean fullscreen) {
        return new GraphicsSettings(showFps, showDebugInfo, fov, renderingMode, resolutionWidth, resolutionHeight, fullscreen);
    }
}
