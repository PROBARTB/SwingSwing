package dasdwdjfhefhj.vechicle.tram;
import java.awt.*;
import java.util.*;
import dasdwdjfhefhj.helpers.*;


public class TramSection {
    public final double length;                // m
    public final java.util.List<Bogie> bogies = new ArrayList<>();
    public Vec2 originXZ = new Vec2(0,0);      // początek członu w świecie
    public double heading = 0.0;               // kąt członu (oś x członu równoległa do belki)
    public Color color = Color.RED;            // czerwony korpus

    public TramSection(double length) { this.length = length; }
}