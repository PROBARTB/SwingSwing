package EALiodufiowAMS2.general.settings;

import EALiodufiowAMS2.engine.rendering.RenderingMode;

public final class GraphicsSettings {

    private final int fov;
    private final RenderingMode renderingMode;

    // Window resolution
    private final int windowWidth;
    private final int windowHeight;
    private final ResolutionMode windowResolutionMode;

    // Render resolution
    private final int renderWidth;
    private final int renderHeight;
    private final ResolutionMode renderResolutionMode;

    private final WindowMode windowMode;

    private final int fpsLimit;
    private final double curveQuality;
    private final double secondarySceneQuality;

    public GraphicsSettings(
            int fov,
            RenderingMode renderingMode,
            int windowWidth,
            int windowHeight,
            ResolutionMode windowResolutionMode,
            int renderWidth,
            int renderHeight,
            ResolutionMode renderResolutionMode,
            WindowMode windowMode,
            int fpsLimit,
            double curveQuality,
            double secondarySceneQuality
    ) {
        this.fov = fov;
        this.renderingMode = renderingMode;
        this.windowWidth = windowWidth;
        this.windowHeight = windowHeight;
        this.windowResolutionMode = windowResolutionMode;
        this.renderWidth = renderWidth;
        this.renderHeight = renderHeight;
        this.renderResolutionMode = renderResolutionMode;
        this.windowMode = windowMode;
        this.fpsLimit = fpsLimit;
        this.curveQuality = curveQuality;
        this.secondarySceneQuality = secondarySceneQuality;
    }

    public int getFov() { return fov; }
    public RenderingMode getRenderingMode() { return renderingMode; }

    public int getWindowWidth() { return windowWidth; }
    public int getWindowHeight() { return windowHeight; }
    public ResolutionMode getWindowResolutionMode() { return windowResolutionMode; }

    public int getRenderWidth() { return renderWidth; }
    public int getRenderHeight() { return renderHeight; }
    public ResolutionMode getRenderResolutionMode() { return renderResolutionMode; }

    public WindowMode getWindowMode() { return windowMode; }

    public int getFpsLimit() { return fpsLimit; }
    public double getCurveQuality() { return curveQuality; }
    public double getSecondarySceneQuality() { return secondarySceneQuality; }


    public GraphicsSettings withFov(int fov) {
        return new GraphicsSettings(fov, renderingMode,
                windowWidth, windowHeight, windowResolutionMode,
                renderWidth, renderHeight, renderResolutionMode,
                windowMode, fpsLimit, curveQuality, secondarySceneQuality);
    }

    public GraphicsSettings withRenderingMode(RenderingMode mode) {
        return new GraphicsSettings(fov, mode,
                windowWidth, windowHeight, windowResolutionMode,
                renderWidth, renderHeight, renderResolutionMode,
                windowMode, fpsLimit, curveQuality, secondarySceneQuality);
    }

    public GraphicsSettings withWindowResolution(int width, int height, ResolutionMode mode) {
        return new GraphicsSettings(fov, renderingMode,
                width, height, mode,
                renderWidth, renderHeight, renderResolutionMode,
                windowMode, fpsLimit, curveQuality, secondarySceneQuality);
    }

    public GraphicsSettings withRenderResolution(int width, int height, ResolutionMode mode) {
        return new GraphicsSettings(fov, renderingMode,
                windowWidth, windowHeight, windowResolutionMode,
                width, height, mode,
                windowMode, fpsLimit, curveQuality, secondarySceneQuality);
    }

    public GraphicsSettings withWindowMode(WindowMode mode) {
        return new GraphicsSettings(fov, renderingMode,
                windowWidth, windowHeight, windowResolutionMode,
                renderWidth, renderHeight, renderResolutionMode,
                mode, fpsLimit, curveQuality, secondarySceneQuality);
    }

    public GraphicsSettings withFpsLimit(int limit) {
        return new GraphicsSettings(fov, renderingMode,
                windowWidth, windowHeight, windowResolutionMode,
                renderWidth, renderHeight, renderResolutionMode,
                windowMode, limit, curveQuality, secondarySceneQuality);
    }

    public GraphicsSettings withCurveQuality(int quality) {
        return new GraphicsSettings(fov, renderingMode,
                windowWidth, windowHeight, windowResolutionMode,
                renderWidth, renderHeight, renderResolutionMode,
                windowMode, fpsLimit, quality, secondarySceneQuality);
    }

    public GraphicsSettings withSecondarySceneQuality(int quality) {
        return new GraphicsSettings(fov, renderingMode,
                windowWidth, windowHeight, windowResolutionMode,
                renderWidth, renderHeight, renderResolutionMode,
                windowMode, fpsLimit, curveQuality, quality);
    }
}

