package CWERTEL;

import java.awt.*;
import java.awt.geom.AffineTransform;

public class MyTram extends Tram {
    private double x;
    private double y;
    private double heading;      // radiany
    private double currentSpeed; // piksele na sekundę
    private Color bodyColor = new Color(255, 165, 0);
    private boolean selected = false;

    public MyTram(double maxSpeed, int length, SoundManager soundManager, double startX, double startY) {
        super(maxSpeed, length, soundManager);
        this.x = startX;
        this.y = startY;
    }

    @Override
    public void draw(Graphics2D g) {
        Object oldAA = g.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        AffineTransform old = g.getTransform();
        g.translate(x, y);
        g.rotate(heading);

        int w = Math.max(40, length);
        int h = 30;

        // kadłub
        g.setColor(bodyColor);
        g.fillRoundRect(-w/2, -h/2, w, h, 12, 12);

        // okna
        g.setColor(new Color(220, 240, 255));
        int winCount = Math.max(2, w / 30);
        int gap = 6;
        int winW = (w - 20 - (winCount - 1) * gap) / winCount;
        for (int i = 0; i < winCount; i++) {
            int wx = -w/2 + 10 + i * (winW + gap);
            g.fillRoundRect(wx, -h/4, winW, h/3, 4, 4);
        }

        // drzwi
        g.setColor(new Color(200, 200, 200));
        int doorW = Math.max(12, w / 8);
        g.fillRect(-doorW/2, -h/2, doorW, h);

        // koła
        g.setColor(Color.DARK_GRAY);
        int wheelSize = 12;
        g.fillOval(-w/2 + 6, h/2 - wheelSize/2, wheelSize, wheelSize);
        g.fillOval(w/2 - 6 - wheelSize, h/2 - wheelSize/2, wheelSize, wheelSize);

        if (selected) {
            g.setColor(Color.RED);
            g.setStroke(new BasicStroke(2));
            g.drawRoundRect(-w/2, -h/2, w, h, 12, 12);
        }

        g.setTransform(old);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, oldAA);
    }

    // ruch i kontrola
    public void setPosition(double x, double y) { this.x = x; this.y = y; }
    public void setHeading(double radians) { this.heading = radians; }
    public void setSpeed(double speed) { this.currentSpeed = speed; }
    public void toggleSelected() { selected = !selected; }

    public void update(double deltaSeconds) {
        x += currentSpeed * Math.cos(heading) * deltaSeconds;
        y += currentSpeed * Math.sin(heading) * deltaSeconds;
    }

    // wygodne metody dźwiękowe
    public void playHorn() { playSignal("horn"); }
    public void playBell() { playSignal("bell"); }

    public void loadDefaultSignals() throws Exception {
        if (soundManager == null) return;
        // ścieżki do plików ustaw w demo (np. "sounds/horn.wav")
        soundManager.load("horn", "sounds/horn.wav");
        soundManager.load("bell", "sounds/bell.wav");
    }
}
