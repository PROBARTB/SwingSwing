import java.awt.*;
import java.util.*;
import java.util.List;

public class World {
    private List<Stop> stops = new ArrayList<>();
    private List<Intersection> intersections = new ArrayList<>();

    public void addStop(Stop s) { stops.add(s); }
    public void addIntersection(Intersection i) { intersections.add(i); }

    public void draw(Graphics2D g, int offsetX) {
        // Background buildings
        g.setColor(Color.LIGHT_GRAY);
        for (int i = 0; i < 800; i += 120) {
            g.fillRect(i - offsetX % 120, 100, 80, 150);
        }

        // Foreground objects
        for (Stop s : stops) s.draw(g, offsetX);
        for (Intersection i : intersections) i.draw(g, offsetX);
    }
}