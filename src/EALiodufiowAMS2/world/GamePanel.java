package EALiodufiowAMS2.world;

import EALiodufiowAMS2.world.scenes.ScenePanel;

import javax.swing.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class GamePanel extends JFrame {
    private ScenePanel scene;
    private Timer timer;
    private boolean paused = false;

    public GamePanel() {
        super("Swing Swing");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);

        JMenuBar menuBar = new JMenuBar();
        JButton pauseButton = new JButton("Pause");
        menuBar.add(pauseButton);
        setJMenuBar(menuBar);

        // 30 FPS → 33 ms
        timer = new Timer(33, e -> {
            //System.out.println("Timer update");
            if (scene != null && !paused) {
                //System.out.println("not null & not paused");
                scene.stepAndRender();
            }
        });
        timer.start();

        pauseButton.addActionListener(e -> {
            paused = !paused;
            pauseButton.setText(paused ? "Play" : "Pause");
        });
    }

    public void setScene(ScenePanel scene) {
        this.scene = scene;
        add(scene);
    }
}
