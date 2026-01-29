package EALiodufiowAMS2.engine.uiRendering;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyEvent;
import java.util.*;
import java.util.List;


public class UiOverlayEngine {

    private final JComponent basePanel; // panel z AWTGLCanvas
    private UiOverlay overlay;

    private final List<JWindow> managedWindows = new ArrayList<>();


    private final ComponentAdapter componentListener = new ComponentAdapter() {
        @Override
        public void componentResized(ComponentEvent e) {
            updateLayout();
        }

        @Override
        public void componentMoved(ComponentEvent e) { // doesn't work so HierarchyBoundsListener.ancestorMoved has to be used
            updateLayout();
        }

        @Override
        public void componentShown(ComponentEvent e) {
            updateLayout();
        }

        @Override
        public void componentHidden(ComponentEvent e) {
            updateLayout();
        }
    };

    public UiOverlayEngine(JComponent basePanel) {
        this.basePanel = Objects.requireNonNull(basePanel, "basePanel");
        this.basePanel.addComponentListener(componentListener);
        this.basePanel.addHierarchyBoundsListener(new HierarchyBoundsListener() {
            @Override
            public void ancestorMoved(HierarchyEvent e) {
                updateLayout();
            }

            @Override
            public void ancestorResized(HierarchyEvent e) {
                updateLayout();
            }
        });

    }

    public void setOverlay(UiOverlay overlay) {
        runOnEdt(() -> {
            disposeCurrentWindows();

            this.overlay = overlay;

            if (overlay != null) {
                for (UiOverlayLayer layer : overlay.getLayers()) {
                    for (UiOverlayElement element : layer.getElements()) {
                        ensureWindowForElement(element);
                    }
                }
                doUpdateLayout();
            }
        });
    }

    public void updateLayout() {
        runOnEdt(this::doUpdateLayout);
    }

    public void refreshOverlayComponent(JComponent component) {
        runOnEdt(() -> {
            component.revalidate();
            component.repaint();
        });
    }

    public void dispose() {
        runOnEdt(() -> {
            basePanel.removeComponentListener(componentListener);
            disposeCurrentWindows();
            overlay = null;
        });
    }

    private void disposeCurrentWindows() {
        //System.out.println("disposing windows (managedWindows size=" + managedWindows.size() + ")");

        for (JWindow window : managedWindows) {
            //System.out.println("disposing window " + window);
            window.setVisible(false);
            window.dispose();
        }
        managedWindows.clear();

        if (overlay != null) {
            for (UiOverlayLayer layer : overlay.getLayers()) {
                for (UiOverlayElement element : layer.getElements()) {
                    element.window = null;
                }
            }
        }
    }


    private void ensureWindowForElement(UiOverlayElement element) {
        if (element.window != null) return;

        Window owner = SwingUtilities.getWindowAncestor(basePanel);
        if (owner == null) return;

        JWindow window = new JWindow(owner);
        window.setFocusableWindowState(false);
        window.setBackground(new Color(0, 0, 0, 0));

        JPanel content = new JPanel(new BorderLayout());
        content.setOpaque(false);
        content.add(element.getComponent(), BorderLayout.CENTER);
        window.setContentPane(content);

        element.window = window;
        managedWindows.add(window);
    }

    private void doUpdateLayout() {
        if (overlay == null)  return;

        Window owner = SwingUtilities.getWindowAncestor(basePanel);
        if (owner == null) return;

        Point panelOnScreen;
        try {
            panelOnScreen = basePanel.getLocationOnScreen();
        } catch (IllegalComponentStateException ex) {
            return;
        }

        int panelWidth = basePanel.getWidth();
        int panelHeight = basePanel.getHeight();

        for (UiOverlayLayer layer : overlay.getLayers()) {
            if (!layer.isVisible()) {
                for (UiOverlayElement element : layer.getElements()) {
                    if (element.window != null) {
                        element.window.setVisible(false);
                        element.window.dispose();
                        managedWindows.remove(element.window);
                        element.window = null;
                    }
                }
                continue;
            }

            for (UiOverlayElement element : layer.getElements()) {
                updateElement(element, panelOnScreen, panelWidth, panelHeight);
            }
        }
    }

    public void updateElement(UiOverlayElement element) {
        UiOverlayLayer layer = element.getLayer();
        if (layer != null && !layer.isVisible()) {
            if (element.window != null) {
                element.window.setVisible(false);
                element.window.dispose();
                managedWindows.remove(element.window);
                element.window = null;
            }
            return;
        }

        Point panelOnScreen;
        try {
            panelOnScreen = basePanel.getLocationOnScreen();
        } catch (IllegalComponentStateException ex) {
            return;
        }

        int panelWidth = basePanel.getWidth();
        int panelHeight = basePanel.getHeight();

        updateElement(element, panelOnScreen, panelWidth, panelHeight);
    }
    private void updateElement(UiOverlayElement element, Point panelOnScreen, int panelWidth, int panelHeight) {
        UiOverlayConstraints c = element.getConstraints();

        ensureWindowForElement(element);
        JWindow window = element.window;
        if (window == null) {
            return;
        }

        Dimension preferred = element.getComponent().getPreferredSize();
        int w = (c.width() != null) ? c.width() : preferred.width;
        int h = (c.height() != null) ? c.height() : preferred.height;

        Point posInPanel = computePositionInPanel(c.anchor(), c.offsetX(), c.offsetY(),
                panelWidth, panelHeight, w, h);

        int xOnScreen = panelOnScreen.x + posInPanel.x;
        int yOnScreen = panelOnScreen.y + posInPanel.y;

        Rectangle newBounds = new Rectangle(xOnScreen, yOnScreen, w, h);
        Rectangle oldBounds = window.getBounds();

        boolean boundsChanged = !newBounds.equals(oldBounds);

        if (boundsChanged) {
            window.setBounds(newBounds);
            window.validate();
            window.repaint();
        }

        if (c.visible()) {
            if (!window.isVisible()) {
                window.setVisible(true);
            }
        } else {
            if (window.isVisible()) {
                window.setVisible(false);
            }
        }
    }

    private static Point computePositionInPanel(UiOverlayAnchor anchor,
                                                int offsetX,
                                                int offsetY,
                                                int panelWidth,
                                                int panelHeight,
                                                int elementWidth,
                                                int elementHeight) {
        int x = 0;
        int y = 0;
        switch (anchor) {
            case TOP_LEFT:
                x = 0;
                y = 0;
                break;
            case TOP_CENTER:
                x = (panelWidth - elementWidth) / 2;
                y = 0;
                break;
            case TOP_RIGHT:
                x = panelWidth - elementWidth;
                y = 0;
                break;
            case CENTER_LEFT:
                x = 0;
                y = (panelHeight - elementHeight) / 2;
                break;
            case CENTER:
                x = (panelWidth - elementWidth) / 2;
                y = (panelHeight - elementHeight) / 2;
                break;
            case CENTER_RIGHT:
                x = panelWidth - elementWidth;
                y = (panelHeight - elementHeight) / 2;
                break;
            case BOTTOM_LEFT:
                x = 0;
                y = panelHeight - elementHeight;
                break;
            case BOTTOM_CENTER:
                x = (panelWidth - elementWidth) / 2;
                y = panelHeight - elementHeight;
                break;
            case BOTTOM_RIGHT:
                x = panelWidth - elementWidth;
                y = panelHeight - elementHeight;
                break;
        }

        x += offsetX;
        y += offsetY;

        return new Point(x, y);
    }

    private static void runOnEdt(Runnable r) {
        if (SwingUtilities.isEventDispatchThread()) {
            r.run();
        } else {
            SwingUtilities.invokeLater(r);
        }
    }
}
