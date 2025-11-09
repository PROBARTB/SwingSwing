package dasdwdjfhefhj;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

import dasdwdjfhefhj.vechicle.tram.*;
import dasdwdjfhefhj.helpers.*;

public class ScenePanel extends JPanel implements KeyListener {
    private final World world;
    private final Camera cam;

    public ScenePanel(World w, Camera c) {
        this.world = w; this.cam = c;
        setBackground(Color.WHITE);
        setFocusable(true);
        addKeyListener(this);
    }

    @Override protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();

        // Antyaliasing
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Transformacja kamery
        AffineTransform camTx = cam.worldToCamera(getSize());
        g2.transform(camTx);

        // Rysuj tory
        new WorldRenderer().drawTracks(g2, world.start);

        // Rysuj tramwaj
        for (TramSection sec : world.tram.sections) {
            // pozycja sekcji w pikselach:
            double x = sec.originXZ.x * Units.M_TO_PX;
            double z = sec.originXZ.z * Units.M_TO_PX;

            AffineTransform atSec = new AffineTransform();
            atSec.translate(x, z);
            atSec.rotate(sec.heading);
            // Sekcja attached bez pseudo-perspektywy; reszta delikatnie skaluje względem z'
            if (sec != cam.attached) {
                Point2D camSpace = camTx.transform(new Point2D.Double(x, z), null);
                double zCam = camSpace.getY();
                double sc = cam.scaleForDepth(zCam);
                atSec.scale(sc, sc);
            }
            Graphics2D gSec = (Graphics2D) g2.create();
            gSec.transform(atSec);

            // Korpus członu: prostokąt od (0, -h) do (length, h)
            double h = 1.2 * Units.M_TO_PX;
            double Lpx = sec.length * Units.M_TO_PX;
            gSec.setColor(sec.color);
            gSec.fill(new Rectangle2D.Double(0, -h/2, Lpx, h));

            // Wózki (niebieskie) w offsetach
            gSec.setColor(Color.BLUE);
            for (Bogie b : sec.bogies) {
                double bx = b.offsetInSection * Units.M_TO_PX;
                double bw = 0.9 * Units.M_TO_PX;
                double bh = 0.5 * Units.M_TO_PX;
                gSec.fill(new Rectangle2D.Double(bx - bw/2, -bh/2, bw, bh));
            }

            gSec.dispose();
        }

        g2.dispose();
    }

    // Sterowanie W/S
    @Override public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_W) world.tram.speed = Math.min(world.tram.speed + 0.5, 12.0);
        if (e.getKeyCode() == KeyEvent.VK_S) world.tram.speed = Math.max(world.tram.speed - 0.5, 0.0);
    }
    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}
}