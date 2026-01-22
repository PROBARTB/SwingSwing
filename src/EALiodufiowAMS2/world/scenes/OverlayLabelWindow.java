package EALiodufiowAMS2.world.scenes;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.HierarchyEvent;

import static javax.swing.text.StyleConstants.setBackground;

public class OverlayLabelWindow extends JWindow {
    private final JLabel label;
    private final ScenePanel scenePanel;
    private final Window ownerWindow;

    // Pozycja labela względem sceny (w pikselach)
    public enum Anchor {
        TOP_LEFT,
        TOP_RIGHT,
        BOTTOM_LEFT,
        BOTTOM_RIGHT
    }

    private final Anchor anchor;
    private final int marginX;
    private final int marginY;

    public OverlayLabelWindow(Window owner,
                              ScenePanel scenePanel,
                              Anchor anchor,
                              int marginX,
                              int marginY) {
        super(owner);
        this.ownerWindow = owner;
        this.scenePanel = scenePanel;
        this.anchor = anchor;
        this.marginX = marginX;
        this.marginY = marginY;

        setBackground(new Color(0, 0, 0, 0)); // pełna przezroczystość okna
        setFocusableWindowState(false);       // nie kradnie focusu

        label = new JLabel();
        label.setOpaque(true);
        label.setBackground(new Color(0, 0, 0, 160)); // półprzezroczyste tło
        label.setForeground(Color.WHITE);
        label.setFont(label.getFont().deriveFont(Font.BOLD, 13f));
        label.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));

        setContentPane(label);
        pack();

        installListeners();
    }

    private void installListeners() {
        // Reagujemy na ruch/resize okna głównego
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

        // Reagujemy na zmiany sceny
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

        scenePanel.addHierarchyListener(e -> {
            if ((e.getChangeFlags() & HierarchyEvent.DISPLAYABILITY_CHANGED) != 0) {
                if (!scenePanel.isDisplayable()) {
                    setVisible(false);
                }
            }
        });
    }

    public void updateBounds() {
        if (!scenePanel.isShowing()) {
            setVisible(false);
            return;
        }

        try {
            Point sceneLoc = scenePanel.getLocationOnScreen();
            Dimension sceneSize = scenePanel.getSize();
            Dimension labelSize = getPreferredSize();

            int x = sceneLoc.x;
            int y = sceneLoc.y;

            switch (anchor) {
                case TOP_LEFT -> {
                    x += marginX;
                    y += marginY;
                }
                case TOP_RIGHT -> {
                    x += sceneSize.width - labelSize.width - marginX;
                    y += marginY;
                }
                case BOTTOM_LEFT -> {
                    x += marginX;
                    y += sceneSize.height - labelSize.height - marginY;
                }
                case BOTTOM_RIGHT -> {
                    x += sceneSize.width - labelSize.width - marginX;
                    y += sceneSize.height - labelSize.height - marginY;
                }
            }

            setBounds(x, y, labelSize.width, labelSize.height);
            setVisible(true);
        } catch (IllegalComponentStateException ex) {
            // jeszcze nie w hierarchii – zignoruj, spróbujemy później
        }
    }

    public void setText(String text) {
        label.setText(text);
        pack();          // dopasuj rozmiar do nowego tekstu
        updateBounds();  // przelicz pozycję
    }
}
