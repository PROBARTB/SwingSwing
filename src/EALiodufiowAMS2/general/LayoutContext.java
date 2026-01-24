package EALiodufiowAMS2.general;

import EALiodufiowAMS2.general.settings.SettingsManager;

public class LayoutContext {

    private final SettingsManager settingsManager;
    private final GameNavigation navigation;

    public LayoutContext(SettingsManager settingsManager,
                         GameNavigation navigation) {
        this.settingsManager = settingsManager;
        this.navigation = navigation;
    }

    public SettingsManager getSettingsManager() {
        return settingsManager;
    }

    public GameNavigation getNavigation() {
        return navigation;
    }
}
