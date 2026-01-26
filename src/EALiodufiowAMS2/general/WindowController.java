package EALiodufiowAMS2.general;

import EALiodufiowAMS2.general.settings.*;

import javax.swing.*;
import java.awt.*;

public class WindowController implements SettingsListener {

    private final JFrame frame;

    private boolean fullscreenActive = false;
    private DisplayMode previousDisplayMode;
    private Rectangle previousBounds;

    public WindowController(JFrame frame) {
        this.frame = frame;
    }

    @Override
    public void onSettingsChanged(Settings newSettings) {
        GraphicsSettings g = newSettings.getGraphics();

        WindowMode mode = g.getWindowMode();

        switch (mode) {
            case FULLSCREEN -> applyFullscreen(g);
            case BORDERLESS -> applyBorderless(g);
            case WINDOWED -> applyWindowed(g);
        }
    }

    private void applyFullscreen(GraphicsSettings g) {
        int width = g.getWindowWidth();
        int height = g.getWindowHeight();
        if (!fullscreenActive) {
            enterFullscreen(width, height);
        } else {
            changeFullscreenResolution(width, height);
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

    private void applyBorderless(GraphicsSettings g) {
        if (fullscreenActive) {
            exitFullscreen();
        }

        frame.dispose();
        frame.setUndecorated(true);
        frame.setResizable(false);

        int w = g.getWindowWidth();
        int h = g.getWindowHeight();

        frame.setSize(w, h);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void applyWindowed(GraphicsSettings g) {
        if (fullscreenActive) {
            exitFullscreen();
        }

        frame.dispose();
        frame.setUndecorated(false);

        boolean fixed = g.getWindowResolutionMode() == ResolutionMode.FIXED;
        frame.setResizable(!fixed);

        int w = g.getWindowWidth();
        int h = g.getWindowHeight();

       // frame.setSize(w, h);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }


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
