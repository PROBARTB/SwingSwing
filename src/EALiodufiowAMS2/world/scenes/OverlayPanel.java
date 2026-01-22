package EALiodufiowAMS2.world.scenes;

import javax.swing.*;
import java.awt.*;

public class OverlayPanel extends JPanel {
    private int fps;

    public OverlayPanel() {
        setOpaque(false); // nic nie zamalowuje tła okna
    }

    public void setFps(int fps) {
        this.fps = fps;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();

        // Półprzezroczyste tło pod tekstem
        g2.setColor(new Color(0, 0, 0, 128));
        int boxWidth = 120;
        int boxHeight = 30;
        g2.fillRoundRect(10, 10, boxWidth, boxHeight, 10, 10);

        // Tekst FPS
        g2.setColor(Color.WHITE);
        g2.setFont(getFont().deriveFont(Font.BOLD, 14f));
        g2.drawString("FPS: " + fps, 20, 30);

        g2.dispose();
    }
}
