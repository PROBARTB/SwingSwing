package CWERTEL;

import javax.swing.*;
import java.awt.*;

public class TramControlDemo {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SoundManager sm = new SoundManager();
            try {
                // Wczytaj pliki WAV (przykładowe nazwy; umieść pliki w katalogu "sounds")
                sm.load("horn", "C:/Users/uczen/IdeaProjects/SwingSwing/sounds/stonka.wav");
                sm.load("bell", "C:/Users/uczen/IdeaProjects/SwingSwing/sounds/stonka.wav");
            } catch (Exception ex) {
                ex.printStackTrace();
                // kontynuujemy bez dźwięków jeśli wczytanie nie powiodło się
            }

            MyTram tram = new MyTram(80.0, 60, sm, 400, 300);
            tram.setHeading(0);
            tram.setSpeed(30); // piksele/s

            JFrame frame = new JFrame("Tram control demo");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            TramPanel panel = new TramPanel(tram);
            frame.add(panel, BorderLayout.CENTER);

            JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JButton hornBtn = new JButton("Horn");
            hornBtn.addActionListener(e -> tram.playHorn()); // obsługa przyciskiem
            controls.add(hornBtn);

            JButton bellBtn = new JButton("Bell");
            bellBtn.addActionListener(e -> tram.playBell());
            controls.add(bellBtn);

            // przycisk toggle no-overlap (dla demonstracji)
            JCheckBox noOverlap = new JCheckBox("No overlap (horn)");
            noOverlap.addActionListener(e -> {
                // zmiana zachowania: jeśli zaznaczone, odtwarzaj horn bez nakładania
                // implementacja: playSignal przyjmuje allowOverlap; tutaj demonstrujemy
            });
            controls.add(noOverlap);

            frame.add(controls, BorderLayout.SOUTH);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            // Timer animacji (~60 FPS)
            new Timer(16, e -> {
                tram.update(0.016);
                panel.repaint();
            }).start();
        });
    }
}

