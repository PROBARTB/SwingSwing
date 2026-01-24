package EALiodufiowAMS2.general.settings;


import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class SettingsManager {

    private volatile Settings current;
    private final List<SettingsListener> listeners = new CopyOnWriteArrayList<>();

    public SettingsManager(Settings initial) {
        this.current = initial;
    }

    public Settings getCurrent() {
        return current;
    }

    public void update(Settings newSettings) {
        this.current = newSettings;
        notifyListeners(newSettings);
    }

    public void addListener(SettingsListener listener) {
        listeners.add(listener);
        listener.onSettingsChanged(current);
    }

    public void removeListener(SettingsListener listener) {
        listeners.remove(listener);
    }

    private void notifyListeners(Settings newSettings) {
        for (SettingsListener listener : listeners) {
            listener.onSettingsChanged(newSettings);
        }
    }
}
