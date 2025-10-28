import java.awt.*;

public class Intersection {
    private int x, y;
    public Intersection(int x, int y) { this.x = x; this.y = y; }
    public void draw(Graphics2D g, int offsetX) {
        g.setColor(Color.GRAY);
        g.fillRect(x - offsetX, y, 60, 10);
    }
}
