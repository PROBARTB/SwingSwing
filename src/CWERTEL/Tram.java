package CWERTEL;

import java.awt.*;

public abstract class Tram extends RailVehicle {
    protected int length;
    protected final SoundManager soundManager;

    public Tram(double maxSpeed, int length) {
        this(maxSpeed, length, null);
    }

    public Tram(double maxSpeed, int length, SoundManager soundManager) {
        super(maxSpeed);
        this.length = length;
        this.soundManager = soundManager;
    }

    public void loadSignal(String id, String path) throws Exception {
        if (soundManager == null) throw new IllegalStateException("SoundManager not provided");
        soundManager.load(id, path);
    }

    public void playSignal(String id) {
        playSignal(id, true);
    }

    public void playSignal(String id, boolean allowOverlap) {
        if (soundManager == null) return;
        soundManager.play(id, allowOverlap);
    }

    public abstract void draw(Graphics2D g);
}

