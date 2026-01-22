package EALiodufiowAMS2.uiRendering;

import EALiodufiowAMS2.world.scenes.ScenePanel;

import javax.swing.*;
import java.awt.*;

public class UiOverlayWindow extends JWindow {
    private final JLabel label;
    private final UiObject uiObject;
    private final ScenePanel scenePanel;
    private final Window ownerWindow;

    public UiOverlayWindow(Window ownerWindow, ScenePanel scenePanel, UiObject uiObject) {
        super(ownerWindow);
        this.ownerWindow = ownerWindow;
        this.scenePanel = scenePanel;
        this.uiObject = uiObject;

        setBackground(new Color(0, 0, 0, 0));
        setFocusableWindowState(false);

        label = new JLabel();
        label.setOpaque(true);
        label.setBackground(new Color(0, 0, 0, 55));
        label.setForeground(Color.WHITE);
        label.setFont(label.getFont().deriveFont(Font.BOLD, 13f));
        label.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));

        setContentPane(label);
        pack();

        updateFromModel();
    }

    public UiObject getUiObject() {
        return uiObject;
    }

    public void updateFromModel() {
        label.setText(uiObject.getText());
        pack();
        updateBounds();
        setVisible(uiObject.isVisible());
    }

    public void updateBounds() {
        if (!scenePanel.isShowing() || !uiObject.isVisible()) {
            setVisible(false);
            return;
        }

        try {
            Point sceneLoc = scenePanel.getLocationOnScreen();
            Dimension sceneSize = scenePanel.getSize();
            Dimension labelSize = getPreferredSize();

            int x = sceneLoc.x;
            int y = sceneLoc.y;

            UiAnchor anchor = uiObject.getAnchor();
            int marginX = uiObject.getMarginX();
            int marginY = uiObject.getMarginY();

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
            setVisible(uiObject.isVisible());
        } catch (IllegalComponentStateException ex) {
            // jeszcze nie w hierarchii – zignoruj, spróbujemy później
        }
    }
}
