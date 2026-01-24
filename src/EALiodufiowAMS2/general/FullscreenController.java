package EALiodufiowAMS2.general;

import EALiodufiowAMS2.general.settings.GraphicsSettings;
import EALiodufiowAMS2.general.settings.Settings;
import EALiodufiowAMS2.general.settings.SettingsListener;

import javax.swing.*;
import java.awt.*;

public class FullscreenController implements SettingsListener {

    private final JFrame frame;

    private boolean fullscreenActive = false;
    private DisplayMode previousDisplayMode;
    private Rectangle previousBounds;

    public FullscreenController(JFrame frame) {
        this.frame = frame;
    }

    @Override
    public void onSettingsChanged(Settings newSettings) {
        GraphicsSettings g = newSettings.getGraphics();

        if (g.isFullscreen() != fullscreenActive) {
            if (g.isFullscreen()) {
                enterFullscreen(g.getResolutionWidth(), g.getResolutionHeight());
            } else {
                exitFullscreen();
            }
        } else if (fullscreenActive) {
            changeFullscreenResolution(g.getResolutionWidth(), g.getResolutionHeight());
        } else {
            frame.setSize(g.getResolutionWidth(), g.getResolutionHeight());
            frame.setLocationRelativeTo(null);
        }
    }


    public void enterFullscreen(int width, int height) {
        if (fullscreenActive) return;

        GraphicsDevice device = getBestDevice();

        previousBounds = frame.getBounds();
        previousDisplayMode = device.getDisplayMode();

        frame.dispose();
        frame.setUndecorated(true);
        frame.setResizable(false);

        device.setFullScreenWindow(frame);

        DisplayMode newMode = findBestDisplayMode(device, width, height);
        if (newMode != null) {
            try {
                device.setDisplayMode(newMode);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(
                        frame,
                        "This device does not support Fullscreen Mode in " + width + "x" + height + ":\n" + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }

        frame.setVisible(true);
        fullscreenActive = true;
    }

    public void exitFullscreen() {
        if (!fullscreenActive) return;

        GraphicsDevice device = getBestDevice();

        frame.dispose();
        frame.setUndecorated(false);
        frame.setResizable(true);

        device.setFullScreenWindow(null);

        if (previousDisplayMode != null) {
            try {
                device.setDisplayMode(previousDisplayMode);
            } catch (Exception ignored) {}
        }

        frame.setBounds(previousBounds);
        frame.setVisible(true);

        fullscreenActive = false;
    }

    public void changeFullscreenResolution(int width, int height) {
        if (!fullscreenActive) return;

        GraphicsDevice device = getBestDevice();
        DisplayMode newMode = findBestDisplayMode(device, width, height);

        if (newMode != null) {
            try {
                device.setDisplayMode(newMode);
            } catch (Exception ignored) {}
        }
    }

    // --- HELPERS ---

    private GraphicsDevice getBestDevice() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        return ge.getDefaultScreenDevice();
    }

    private DisplayMode findBestDisplayMode(GraphicsDevice device, int width, int height) {
        DisplayMode[] modes = device.getDisplayModes();
        DisplayMode best = null;

        for (DisplayMode m : modes) {
            if (m.getWidth() == width && m.getHeight() == height) {
                best = m;
                break;
            }
        }

        return best;
    }
}
