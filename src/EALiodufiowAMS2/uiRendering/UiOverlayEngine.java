package EALiodufiowAMS2.uiRendering;

import EALiodufiowAMS2.world.scenes.Scene;
import EALiodufiowAMS2.world.scenes.ScenePanel;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.HierarchyEvent;
import java.util.*;

public class UiOverlayEngine {
    private final Scene scene;
    private final ScenePanel scenePanel;
    private final Window ownerWindow;

    private final Map<String, UiOverlayWindow> windows = new HashMap<>();

    public UiOverlayEngine(Scene scene, ScenePanel scenePanel) {
        this.scene = scene;
        this.scenePanel = scenePanel;
        this.ownerWindow = SwingUtilities.getWindowAncestor(scenePanel);

        if (ownerWindow == null) {
            throw new IllegalStateException("ScenePanel must be attached to a Window before creating UiOverlayEngine");
        }

        installListeners();
        syncWithScene();
    }

    private void installListeners() {
        ownerWindow.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentMoved(ComponentEvent e) {
                updateAllBounds();
            }

            @Override
            public void componentResized(ComponentEvent e) {
                updateAllBounds();
            }

            @Override
            public void componentShown(ComponentEvent e) {
                updateAllBounds();
            }
        });

        scenePanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                updateAllBounds();
            }

            @Override
            public void componentShown(ComponentEvent e) {
                updateAllBounds();
            }
        });

        scenePanel.addHierarchyListener(e -> {
            if ((e.getChangeFlags() & HierarchyEvent.DISPLAYABILITY_CHANGED) != 0) {
                if (!scenePanel.isDisplayable()) {
                    disposeAll();
                }
            }
        });
    }

    /**
     * Synchronizuje listę okien z listą UiObjectów w scenie.
     * Wywołuj, gdy zmienia się lista obiektów UI (dodanie/usunięcie).
     */
    public void syncWithScene() {
        Set<String> existingIds = new HashSet<>(windows.keySet());

        for (UiObject ui : scene.getUiObjects()) {
            String id = ui.getId();
            if (!windows.containsKey(id)) {
                UiOverlayWindow w = new UiOverlayWindow(ownerWindow, scenePanel, ui);
                windows.put(id, w);
                w.updateFromModel();
            } else {
                // istniejące okno – zaktualizuj dane
                UiOverlayWindow w = windows.get(id);
                w.updateFromModel();
            }
            existingIds.remove(id);
        }

        // Usuń okna dla obiektów, których już nie ma w scenie
        for (String removedId : existingIds) {
            UiOverlayWindow w = windows.remove(removedId);
            if (w != null) {
                w.setVisible(false);
                w.dispose();
            }
        }

        updateAllBounds();
    }

    /**
     * Aktualizuje dane jednego obiektu UI (np. zmiana tekstu).
     * Wywołuj, gdy zmienisz stan UiObject (tekst, widoczność).
     */
    public void updateUiObject(UiObject uiObject) {
        UiOverlayWindow w = windows.get(uiObject.getId());
        if (w != null) {
            w.updateFromModel();
        }
    }

    /**
     * Aktualizuje pozycje wszystkich okien (np. przy ruchu okna/sceny).
     */
    public void updateAllBounds() {
        for (UiOverlayWindow w : windows.values()) {
            w.updateBounds();
        }
    }

    public void disposeAll() {
        for (UiOverlayWindow w : windows.values()) {
            w.setVisible(false);
            w.dispose();
        }
        windows.clear();
    }
}
