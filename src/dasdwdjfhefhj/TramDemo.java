package dasdwdjfhefhj;

import javax.swing.*;

//Oto pełny, uruchamialny przykład w Javie – możesz go skopiować do pliku `TramDemo.java` i uruchomić. Zawiera:
//
//        - **Świat** z trzema segmentami torów: prosty → łuk → prosty.
//        - **Tramwaj** z trzema członami: pierwszy i trzeci mają po dwa wózki, środkowy ma jeden.
//        - **Panel Swing** renderujący tory i tramwaj.
//        - **Sterowanie prędkością** klawiszami `W` (przyspiesz) i `S` (hamuj).
//        - **Kamera** przyczepiona do środkowego członu – on jest zawsze w centrum, reszta się skaluje.
//
//        ---
//
//        ## 📄 Kod przykładowy
//
//        ```java
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;
import javax.swing.Timer;

public class TramDemo {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Budujemy świat
            Vec2 start = new Vec2(50, 200);
            StraightSegment seg1 = new StraightSegment(start, 0, 200);
            ArcSegment seg2 = new ArcSegment(seg1.posAt(seg1.getLength()), 0, 150, 100); // łuk w lewo
            StraightSegment seg3 = new StraightSegment(seg2.posAt(seg2.getLength()), Math.PI/2, 200);
            seg1.setNext(seg2);
            seg2.setNext(seg3);

            // Tramwaj
            Tram tram = new Tram();
            TramSection s1 = new TramSection(30);
            s1.bogies.add(new Bogie(5));
            s1.bogies.add(new Bogie(25));
            TramSection s2 = new TramSection(20);
            s2.bogies.add(new Bogie(10));
            TramSection s3 = new TramSection(30);
            s3.bogies.add(new Bogie(5));
            s3.bogies.add(new Bogie(25));
            tram.sections.add(s1);
            tram.sections.add(s2);
            tram.sections.add(s3);
            tram.speed = 2.0;
            tram.sLead = 0.0;

            World world = new World();
            world.start = seg1;
            world.tram = tram;

            Camera cam = new Camera();
            cam.attached = s2; // kamera przyczepiona do środkowego członu

            JFrame frame = new JFrame("Tram Demo");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            ScenePanel panel = new ScenePanel(world, cam);
            frame.add(panel);
            frame.setSize(800, 600);
            frame.setVisible(true);

            Timer timer = new Timer(40, e -> {
                world.update(0.04);
                panel.repaint();
            });
            timer.start();
        });
    }
}

