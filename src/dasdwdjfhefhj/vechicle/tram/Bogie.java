package dasdwdjfhefhj.vechicle.tram;
import dasdwdjfhefhj.helpers.*;

public class Bogie {
    public final double offsetInSection; // [m] od początku sekcji
    public double sOnTrack;              // globalna pozycja po torze [m]
    public Vec2 worldPosXZ;              // globalna pozycja na ziemi
    public double heading;               // kąt stycznej toru w miejscu wózka

    public Bogie(double offsetInSection) { this.offsetInSection = offsetInSection; }
}
