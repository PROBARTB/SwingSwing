package EALiodufiowAMS2.general;

import EALiodufiowAMS2.world.scenes.OverlayPanel;

import javax.swing.*;
import java.awt.*;

public class GamePanel extends JFrame {
    private ScenePanel scene;
    private Timer timer;
    private boolean paused = false;

    private long lastNanoTime = System.nanoTime();

    private int frames = 0;
    private long lastFpsTime = System.nanoTime();
    public int currentFps = 0;

    public GamePanel(int resWidth, int resHeight) {
        super("Swing Swing");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(resWidth, resHeight);

        JMenuBar menuBar = new JMenuBar();
        JButton pauseButton = new JButton("Pause");
        JButton settingsButton = new JButton("Settings");
        menuBar.add(pauseButton);
        menuBar.add(settingsButton);
        setJMenuBar(menuBar);

        //enterFullscreen(1920, 1080); // niger staś

        // 30 FPS → 33 ms
        timer = new Timer(5, e -> {
            long now = System.nanoTime();

            if (scene != null && !paused) {
                double deltaTime = (now - lastNanoTime) / 1_000_000_000.0;
                lastNanoTime = now;

                scene.stepAndRender(deltaTime);

                //scene.updateFps(currentFps);
                //scene.updateMode("Play");
               // scene.updateDebug("Time: " + lastNanoTime);

                frames++;
                if (now - lastFpsTime >= 1_000_000_000L) {
                    currentFps = frames;
                    frames = 0;
                    lastFpsTime = now;

                    scene.updateFpsCount(currentFps);

                }
            } else {
                lastNanoTime = now;
            }
        });


        timer.start();

        pauseButton.addActionListener(e -> {
            paused = !paused;
            pauseButton.setText(paused ? "Play" : "Pause");
        });

        settingsButton.addActionListener(e -> {
            paused = true; // zatrzymujemy grę
            showSettingsPanel();
        });
    }

    public void setScene(ScenePanel scene) {
        this.scene = scene;


        add(new OverlayPanel());

        add(scene);
    }

    private void showSettingsPanel() {
        // Tworzymy prosty dialog z suwakiem FOV
        JDialog dialog = new JDialog(this, "Settings", true);
        dialog.setLayout(new BorderLayout());

        JSlider fovSlider = new JSlider(30, 120, scene.getCameraFov());
        fovSlider.setMajorTickSpacing(10);
        fovSlider.setPaintTicks(true);
        fovSlider.setPaintLabels(true);

        fovSlider.addChangeListener(ev -> {
            if (scene != null) {
                scene.setCameraFov(fovSlider.getValue());
            }
        });

        dialog.add(new JLabel("FOV:"), BorderLayout.NORTH);
        dialog.add(fovSlider, BorderLayout.CENTER);

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(ev -> {
            dialog.dispose();
            paused = false;
        });
        dialog.add(closeButton, BorderLayout.SOUTH);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    //funkcja ruchająca monitor swing by stasiocudak
    private void enterFullscreen(int width, int height) {
        GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        if (device.isFullScreenSupported()) {
            device.setFullScreenWindow(this);
            DisplayMode target = new DisplayMode(width, height, 32, DisplayMode.REFRESH_RATE_UNKNOWN);
            try {
                device.setDisplayMode(target);
            } catch (Exception e) {
                System.out.println("Changing resolution failed. Monitor may not support 1280x720.");
            }
        } else {
            System.out.println("Fullscreen not supported — falling back to windowed mode.");
            setSize(width, height);
            setLocationRelativeTo(null);
        }
    }

}
