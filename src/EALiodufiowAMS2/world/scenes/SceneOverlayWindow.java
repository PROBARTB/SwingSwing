package EALiodufiowAMS2.world.scenes;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class SceneOverlayWindow extends JWindow {
    private final OverlayPanel overlayPanel;
    private final ScenePanel scenePanel;
    private final Window ownerWindow;

    public SceneOverlayWindow(Window owner, ScenePanel scenePanel) {
        super(owner);
        this.ownerWindow = owner;
        this.scenePanel = scenePanel;

        setBackground(new Color(0, 0, 0, 0)); // pełna przezroczystość okna
        setFocusableWindowState(false);       // nie kradnie focusu

        overlayPanel = new OverlayPanel();
        setContentPane(overlayPanel);

        // Na starcie jeszcze nie znamy położenia – ustawimy po dodaniu do hierarchii
        installListeners();
    }

    private void installListeners() {
        // Gdy okno główne się przesuwa/zmienia rozmiar – przesuwamy overlay
        ownerWindow.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentMoved(ComponentEvent e) {
                updateBounds();
            }

            @Override
            public void componentResized(ComponentEvent e) {
                updateBounds();
            }

            @Override
            public void componentShown(ComponentEvent e) {
                updateBounds();
            }
        });

        // Gdy scena zmienia rozmiar – aktualizujemy overlay
        scenePanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                updateBounds();
            }

            @Override
            public void componentShown(ComponentEvent e) {
                updateBounds();
            }
        });
    }

    public void updateBounds() {
        if (!scenePanel.isShowing()) {
            setVisible(false);
            return;
        }

        try {
            Point loc = scenePanel.getLocationOnScreen();
            Dimension size = scenePanel.getSize();

            setBounds(loc.x, loc.y, size.width, size.height);
            overlayPanel.setSize(size);
            setVisible(true);
        } catch (IllegalComponentStateException ex) {
            // jeszcze nie w hierarchii – zignoruj, spróbujemy później
        }
    }

    public void setFps(int fps) {
        overlayPanel.setFps(fps);
    }
}
