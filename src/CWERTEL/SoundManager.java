package CWERTEL;

import javax.sound.sampled.*;
import java.io.*;
import java.util.*;

public class SoundManager {
    private static class Sample {
        final byte[] data;
        final AudioFormat format;
        final long frameLength;
        Sample(byte[] data, AudioFormat format, long frameLength) {
            this.data = data; this.format = format; this.frameLength = frameLength;
        }
    }

    private final Map<String, Sample> samples = new HashMap<>();
    // przechowujemy aktywne clipy tylko dla trybu "no overlap"
    private final Map<String, Clip> activeClips = new HashMap<>();

    /**
     * Wczytuje plik audio (np. WAV PCM) do pamięci pod kluczem id.
     */
    public synchronized void load(String id, String filePath) throws Exception {
        try (AudioInputStream ais = AudioSystem.getAudioInputStream(new File(filePath));
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            AudioFormat format = ais.getFormat();
            byte[] buffer = new byte[4096];
            int read;
            while ((read = ais.read(buffer)) != -1) baos.write(buffer, 0, read);
            byte[] data = baos.toByteArray();
            samples.put(id, new Sample(data, format, ais.getFrameLength()));
        }
    }

    /**
     * Odtwarza sygnał o danym id.
     * @param id klucz sygnału
     * @param allowOverlap true = pozwól na nakładanie się wielu odtworzeń tego samego sygnału
     */
    public void play(String id, boolean allowOverlap) {
        Sample s;
        synchronized (this) {
            s = samples.get(id);
        }
        if (s == null) {
            System.err.println("Sound not found: " + id);
            return;
        }

        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(s.data);
            AudioInputStream ais = new AudioInputStream(bais, s.format, s.frameLength);
            Clip clip = AudioSystem.getClip();
            clip.open(ais);

            clip.addLineListener(e -> {
                if (e.getType() == LineEvent.Type.STOP) {
                    clip.close();
                    synchronized (SoundManager.this) {
                        // usuń clip z mapy aktywnych (jeśli tam był)
                        activeClips.values().removeIf(c -> c == clip);
                    }
                }
            });

            if (!allowOverlap) {
                synchronized (this) {
                    Clip existing = activeClips.get(id);
                    if (existing != null && existing.isRunning()) {
                        // restartuj istniejący clip
                        existing.stop();
                        existing.setFramePosition(0);
                        existing.start();
                        clip.close(); // nowy clip nie jest potrzebny
                        return;
                    } else {
                        activeClips.put(id, clip);
                    }
                }
            }

            clip.start();
        } catch (LineUnavailableException | IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Zatrzymuje aktywny sygnał (tylko dla trybu no-overlap).
     */
    public synchronized void stop(String id) {
        Clip c = activeClips.remove(id);
        if (c != null) {
            c.stop();
            c.close();
        }
    }
}

