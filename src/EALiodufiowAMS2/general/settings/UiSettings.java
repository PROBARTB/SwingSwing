package EALiodufiowAMS2.general.settings;

public final class UiSettings {

    private final boolean showFps;
    private final boolean showDebugInfo;

    public UiSettings(boolean showFps, boolean showDebugInfo) {
        this.showFps = showFps;
        this.showDebugInfo = showDebugInfo;
    }

    public boolean isShowFps() {
        return showFps;
    }

    public boolean isShowDebugInfo() {
        return showDebugInfo;
    }

    public UiSettings withShowFps(boolean showFps) {
        return new UiSettings(showFps, showDebugInfo);
    }

    public UiSettings withShowDebugInfo(boolean showDebugInfo) {
        return new UiSettings(showFps, showDebugInfo);
    }
}
