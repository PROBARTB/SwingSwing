import java.awt.*;

public class TramStop extends Stop {
    public TramStop(int x, int y) { super(x, y); }
    @Override
    public void draw(Graphics2D g, int offsetX) {
        g.setColor(Color.RED);
        g.fillRect(x - offsetX, y, 40, 20);
        g.setColor(Color.BLACK);
        g.drawString("Stop", x - offsetX, y - 5);
    }
}