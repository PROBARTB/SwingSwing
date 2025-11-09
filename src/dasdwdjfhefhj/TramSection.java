package dasdwdjfhefhj;


import java.util.ArrayList;
import java.util.List;

public class TramSection {
    public final double length;
    public final List<Bogie> bogies = new ArrayList<>(); // 0,1,2
    // transformacja sekcji w świecie
    public Vec2 originXZ;      // pozycja początku sekcji (na ziemi)
    public double heading;     // kąt sekcji (x:z)
    public TramSection(double length) { this.length = length; }
}