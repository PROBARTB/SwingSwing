package dasdwdjfhefhj;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

public class ScenePanel extends JPanel implements KeyListener {
    private final World world;
    private final Camera cam;

    public ScenePanel(World w, Camera c) { this.world = w; this.cam = c; addKeyListener(this); setFocusable(true); }

    @Override protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        AffineTransform base = cam.worldToCamera(cam.attached, getSize());
        g2.transform(base);

        // 1) tory
        new WorldRenderer().drawTracks(g2, world.start);

        // 2) tramwaj
        for (TramSection sec : world.tram.sections) {
            // przelicz anchor w kamerze
            // attached rysujemy bez skalowania; pozostałe z pseudo-perspektywą
            AffineTransform atSec = new AffineTransform();
            // translate sekcji do jej originXZ
            atSec.translate(sec.originXZ.x, sec.originXZ.z);
            atSec.rotate(sec.heading);
            if (sec != cam.attached) {
                // „głębia” jako z' po worldToCamera: wyekstrahuj drugą współrzędną
                Point2D camPt = base.transform(new Point2D.Double(sec.originXZ.x, sec.originXZ.z), null);
                double zCam = camPt.getY(); // po obrocie osi kamery: Y = „głębia” względem ref
                double sc = cam.scaleForDepth(zCam);
                atSec.scale(sc, sc);
            }
            Graphics2D gSec = (Graphics2D) g2.create();
            gSec.transform(atSec);
            drawSection(gSec, sec); // prostokąt + wózki
            gSec.dispose();
        }

        g2.dispose();
    }

    private void drawSection(Graphics2D g2, TramSection sec) {
        // prosty rysunek: korpus sekcji jako prostokąt: od (0,0) do (length, height)
        double height = 2.8; // przykładowa wysokość
        g2.draw(new java.awt.geom.Rectangle2D.Double(0, -height, sec.length, height));
        for (Bogie b : sec.bogies) {
            // wózek jako mały prostokąt przy offsetInSection
            g2.fill(new java.awt.geom.Rectangle2D.Double(b.offsetInSection - 0.5, -0.3, 1.0, 0.6));
        }
    }

    // Sterowanie prędkością
    @Override public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_W) world.tram.speed += 0.5;
        if (e.getKeyCode() == KeyEvent.VK_S) world.tram.speed -= 0.5;
        world.tram.speed = Math.max(0, Math.min(world.tram.speed, 15.0)); // clamp
    }
    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}
}