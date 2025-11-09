package dasdwdjfhefhj;

import dasdwdjfhefhj.tracks.*;
import dasdwdjfhefhj.vechicleTypes.tram.*;
import dasdwdjfhefhj.helpers.*;

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
import javax.swing.Timer;

public class TramDemo {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Startowy punkt i prosty odcinek
            Vec2 start = new Vec2(50, 200);
            double heading0 = 0.0; // w prawo
            StraightSegment seg1 = new StraightSegment(start, heading0, 100.0);

            // Łuk Béziera: offsetX=60, offsetZ=40, exitAngle = 30° (0.5236 rad)
            BezierArc curve1 = new BezierArc(seg1.posAt(seg1.getLength()), seg1.exitHeading(),
                    60.0, 40.0, Math.toRadians(30));

            // Następny prosty dziedziczy kąt wyjścia łuku
            StraightSegment seg2 = new StraightSegment(curve1.getEnd(), curve1.exitHeading(), 80.0);

            // Łuk kołowy: długość 120 m, promień +80 m (lewo)
            BezierArc curve2 = new BezierArc(seg2.posAt(seg2.getLength()), seg2.exitHeading(),
                    120.0, +80.0);

            // Rozjazd
            SwitchSegment sw = new SwitchSegment(curve2.getEnd(), curve2.exitHeading());
            // Po rozjeździe łączymy dalej w jeden odcinek (dla obu gałęzi)
            StraightSegment segAfterSwitch = new StraightSegment(
                    sw.getLeftBranch() instanceof BezierArc b ? b.getEnd()
                            : ((StraightSegment) sw.getRightBranch()).posAt(((StraightSegment) sw.getRightBranch()).getLength()),
                    // heading dziedziczony z gałęzi (przyjmujemy z lewej dla inicjalizacji)
                    sw.getLeftBranch().exitHeading(), 120.0
            );
            sw.setNext(segAfterSwitch);

            // Linkujemy kolejne segmenty
            seg1.setNext(curve1);
            curve1.setNext(seg2);
            seg2.setNext(curve2);
            curve2.setNext(sw);
            // segAfterSwitch może mieć następny segment (opcjonalnie)
            // e.g. final straight
            StraightSegment finalStraight = new StraightSegment(segAfterSwitch.posAt(segAfterSwitch.getLength()),
                    segAfterSwitch.exitHeading(), 150.0);
            segAfterSwitch.setNext(finalStraight);

            // Tramwaj: Pesa Swing
            PesaSwing tram = new PesaSwing();
            tram.speed = 2.0;  // m/s startowo
            tram.sLead = 0.0;

            // Świat
            World world = new World();
            world.start = seg1;
            world.tram = tram;

            // Kamera przyczepiona do środkowego członu (np. s3)
            Camera cam = new Camera();
            cam.attached = tram.sections.get(2); // środkowy człon

            // UI
            JFrame frame = new JFrame("Pesa Swing Tram Demo");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            ScenePanel panel = new ScenePanel(world, cam);
            frame.add(panel);
            frame.setSize(1000, 700);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            // Timer aktualizacji
            new Timer(30, e -> {
                world.update(0.03);
                panel.repaint();
            }).start();
        });
    }
}

