import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

class GamePanel extends JPanel implements ActionListener, KeyListener {
    private Tram tram;
    private World world;
    private Timer timer;
    private int worldOffset = 0;

    public GamePanel() {
        tram = new PesaSwing();
        world = new World();
        world.addStop(new TramStop(400, 220));
        world.addIntersection(new Intersection(700, 240));

        timer = new Timer(16, this); // ~60 FPS
        timer.start();
        setFocusable(true);
        addKeyListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        tram.update(0.016); // 16 ms
        worldOffset += tram.getSpeed() * 0.016;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        world.draw(g2, worldOffset);
        tram.draw(g2);
    }

    // Key controls
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_UP) tram.setAcceleration(50);   // accelerate
        if (e.getKeyCode() == KeyEvent.VK_DOWN) tram.setAcceleration(-80); // brake
    }

    @Override
    public void keyReleased(KeyEvent e) {
        tram.setAcceleration(0); // coast
    }

    @Override public void keyTyped(KeyEvent e) {}
}