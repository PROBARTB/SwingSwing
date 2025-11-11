package dasdwdjfhefhj.vechicle.tram;
import java.awt.*;
import java.util.*;
import dasdwdjfhefhj.helpers.*;


public class TramSection {
    public final double length;                // [m]
    public final java.util.List<Bogie> bogies = new java.util.ArrayList<>();
    public Vec2 originXZ = new Vec2(0,0);      // początek sekcji w świecie
    public double heading = 0.0;               // kąt sekcji
    public Color color = Color.RED;            // kolor renderu

    public TramSection(double length) { this.length = length; }
}