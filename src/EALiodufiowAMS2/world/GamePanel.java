package EALiodufiowAMS2.world;

import EALiodufiowAMS2.world.scenes.ScenePanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class GamePanel extends JFrame {
    private ScenePanel scene;
    private Timer timer;
    private boolean paused = false;

    private int frames = 0;
    private long lastFpsTime = System.nanoTime();
    public int currentFps = 0;

    public GamePanel() {
        super("Swing Swing");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);

        JMenuBar menuBar = new JMenuBar();
        JButton pauseButton = new JButton("Pause");
        JButton settingsButton = new JButton("Settings");
        menuBar.add(pauseButton);
        menuBar.add(settingsButton);
        setJMenuBar(menuBar);

        //enterFullscreen(1920, 1080); // niger staś

        // 30 FPS → 33 ms
        timer = new Timer(15, e -> {
            //System.out.println("Timer update");
            if (scene != null && !paused) {
                //System.out.println("not null & not paused");
                scene.stepAndRender();

                frames++;
                long now = System.nanoTime();
                if (now - lastFpsTime >= 1_000_000_000L) { // minęła 1 sekunda
                    currentFps = frames;
                    frames = 0;
                    lastFpsTime = now;
                }
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
