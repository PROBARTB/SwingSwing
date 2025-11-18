package CWERTEL;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;

/**
 * Prosty panel z przyciskiem odtwarzającym plik WAV.
 * Plik jest wczytywany raz do pamięci, a przy każdym odtworzeniu
 * tworzymy nowy Clip z ByteArrayInputStream (umożliwia overlap).
 */
public class StonkaPlayer extends JPanel {
    private final byte[] audioData;
    private final AudioFormat audioFormat;
    private final long frameLength;
    private final JButton playButton;

    /**
     * Konstruktor ładuje plik WAV do pamięci.
     * @throws Exception jeśli wczytanie pliku się nie powiedzie
     */
    public StonkaPlayer() throws Exception {
        setLayout(new FlowLayout(FlowLayout.LEFT));
        playButton = new JButton("Stonka");
        add(playButton);

        // Ścieżkę dostosuj jeśli plik jest w innym miejscu
        String path = "C:/Users/uczen/IdeaProjects/SwingSwing/sounds/stonka.wav";

        // Wczytaj plik do pamięci
        try (AudioInputStream ais = AudioSystem.getAudioInputStream(new File(path));
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            AudioFormat format = ais.getFormat();
            byte[] buffer = new byte[4096];
            int read;
            while ((read = ais.read(buffer)) != -1) {
                baos.write(buffer, 0, read);
            }
            this.audioData = baos.toByteArray();
            this.audioFormat = format;
            this.frameLength = ais.getFrameLength();
        }

        // Akcja przycisku: odtwórz dźwięk (nie blokuje EDT)
        playButton.addActionListener((ActionEvent e) -> {
            // uruchamiamy w osobnym wątku, żeby nie blokować EDT przy ewentualnych opóźnieniach
            new Thread(() -> playSound()).start();
        });
    }

    /**
     * Tworzy nowy Clip z wczytanych bajtów i odtwarza go.
     * Pozwala na nakładanie się odtworzeń.
     */
    private void playSound() {
        if (audioData == null) return;
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(audioData);
            try (AudioInputStream ais = new AudioInputStream(bais, audioFormat, frameLength)) {
                Clip clip = AudioSystem.getClip();
                clip.open(ais);
                clip.addLineListener(event -> {
                    if (event.getType() == LineEvent.Type.STOP) {
                        clip.close();
                    }
                });
                clip.start();
            }
        } catch (IOException | LineUnavailableException ex) {
            ex.printStackTrace();
            SwingUtilities.invokeLater(() ->
                    JOptionPane.showMessageDialog(this, "Błąd odtwarzania dźwięku: " + ex.getMessage(),
                            "Błąd", JOptionPane.ERROR_MESSAGE)
            );
        }
    }

    /**
     * Prosty main do testowania klasy.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Stonka Player");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            try {
                StonkaPlayer panel = new StonkaPlayer();
                frame.getContentPane().add(panel, BorderLayout.CENTER);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null,
                        "Nie udało się wczytać pliku dźwiękowego:\n" + ex.getMessage(),
                        "Błąd", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        });
    }
}

