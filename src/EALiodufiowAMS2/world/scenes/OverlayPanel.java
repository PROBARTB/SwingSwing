package EALiodufiowAMS2.world.scenes;

import javax.swing.*;
import java.awt.*;

public final class OverlayPanel extends JComponent {

    private int currentFps;
    public void setFps(int fps) {
        this.currentFps = fps;
        System.out.println(fps);
        repaint();
    }

    public OverlayPanel() {
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.GREEN);
        g.drawString("FPS: " + currentFps, 10, 20);
    }
}
