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
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Transformacja kamery
        AffineTransform camTx = cam.worldToCamera(getSize());
        g2.transform(camTx);

        // Tory
        new WorldRenderer().drawTracks(g2, world.start);

        // Człony (czerwone) + numer nad każdym
        int secIndex = 1;
        for (TramSection sec : world.tram.sections) {
            double x = sec.originXZ.x * Units.M_TO_PX;
            double z = sec.originXZ.z * Units.M_TO_PX;

            AffineTransform atSec = new AffineTransform();
            atSec.translate(x, z);
            atSec.rotate(sec.heading);

            Graphics2D gSec = (Graphics2D) g2.create();
            gSec.transform(atSec);

            double Lpx = sec.length * Units.M_TO_PX;
            double h = 1.2 * Units.M_TO_PX;
            gSec.setColor(Color.RED);
            gSec.fill(new Rectangle2D.Double(0, -h / 2, Lpx, h));

            Graphics2D test = (Graphics2D) g2.create();
            test.setColor(Color.GREEN);
            test.fill(new Line2D.Double(sec.originXZ.x - 50, sec.originXZ.z, sec.originXZ.x + 50, sec.originXZ.z));

            // Tekst numeru członu – nad środkiem prostokąta
            gSec.setColor(Color.BLACK);
            gSec.setFont(new Font("Arial", Font.BOLD, 12));
            gSec.drawString("Sekcja " + secIndex, (float)(Lpx / 2 - 20), (float)(-h));

            gSec.dispose();
            secIndex++;
        }

        // Wózki (niebieskie) + numer sekcji nad każdym wózkiem
        g2.setColor(Color.BLUE);
        secIndex = 1;
        for (TramSection sec : world.tram.sections) {
            int bogieIndex = 1;
            for (Bogie b : sec.bogies) {
                if (b.worldPosXZ == null) continue;
                double bx = b.worldPosXZ.x * Units.M_TO_PX;
                double bz = b.worldPosXZ.z * Units.M_TO_PX;
                double bw = 0.9 * Units.M_TO_PX, bh = 0.5 * Units.M_TO_PX;

                AffineTransform atB = new AffineTransform();
                atB.translate(bx, bz);
                atB.rotate(b.heading);

                Graphics2D gB = (Graphics2D) g2.create();
                gB.transform(atB);
                gB.fill(new Rectangle2D.Double(-bw / 2, -bh / 2, bw, bh));

                // Tekst numeru sekcji i wózka
                gB.setColor(Color.BLACK);
                gB.setFont(new Font("Arial", Font.PLAIN, 10));
                gB.drawString("S" + secIndex + " W" + bogieIndex, -10, (int)(-bh - 5));

                gB.dispose();
                bogieIndex++;
            }
            secIndex++;
        }

        g2.dispose();
    }

    @Override public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_W) world.tram.speed = Math.min(world.tram.speed + 0.5, 12.0);
        if (e.getKeyCode() == KeyEvent.VK_S) world.tram.speed = Math.max(world.tram.speed - 0.5, 0.0);
    }
    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}
}
