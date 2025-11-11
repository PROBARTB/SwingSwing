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
            // Budowa toru: prosty -> łuk Béziera -> prosty -> łuk kołowy -> rozjazd -> prosty -> final prosty
            Vec2 start = new Vec2(50, 200);
            double heading0 = 0.0;

            StraightSegment seg1 = new StraightSegment(start, heading0, 100.0);

            BezierArc curve1 = new BezierArc(seg1.posAt(seg1.getLength()), seg1.exitHeading(),
                    60.0, 40.0, Math.toRadians(30));
            StraightSegment seg2 = new StraightSegment(curve1.getEnd(), curve1.exitHeading(), 80.0);

            BezierArc curve2 = new BezierArc(seg2.posAt(seg2.getLength()), seg2.exitHeading(),
                    120.0, +80.0);

            SwitchSegment sw = new SwitchSegment(curve2.getEnd(), curve2.exitHeading());
            // segment po rozjeździe
            StraightSegment segAfterSwitch = new StraightSegment(
                    sw.getLeftBranch() instanceof BezierArc b ? b.getEnd()
                            : ((StraightSegment) sw.getRightBranch()).posAt(((StraightSegment) sw.getRightBranch()).getLength()),
                    sw.getLeftBranch().exitHeading(), 120.0
            );
            sw.setNext(segAfterSwitch);

            StraightSegment finalStraight = new StraightSegment(segAfterSwitch.posAt(segAfterSwitch.getLength()),
                    segAfterSwitch.exitHeading(), 150.0);

            // Linkowanie
            seg1.setNext(curve1);
            curve1.setNext(seg2);
            seg2.setNext(curve2);
            curve2.setNext(sw);
            segAfterSwitch.setNext(finalStraight);

            // Tramwaj Pesa Swing — dokładnie jak podałeś
            PesaSwing tram = new PesaSwing();
            tram.speed = 0.0;
            tram.sLead = 30.0;

            World world = new World();
            world.start = seg1;
            world.tram = tram;

            // Inicjalizacja pozycji, żeby nie było NPE przy pierwszym renderze
            world.update(0.0);

            Camera cam = new Camera();
            cam.attached = tram.sections.get(2); // kamera na 3. członie (ma jeden wózek)

            JFrame frame = new JFrame("Pesa Swing Tram Demo");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            ScenePanel panel = new ScenePanel(world, cam);
            frame.add(panel);
            frame.setSize(1000, 700);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            new javax.swing.Timer(30, e -> {
                world.update(0.03);
                panel.repaint();
            }).start();
        });
    }
}

