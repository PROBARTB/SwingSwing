import java.awt.*;

public abstract class Stop {
    protected int x, y;
    public Stop(int x, int y) { this.x = x; this.y = y; }
    public abstract void draw(Graphics2D g, int offsetX);
}