package dasdwdjfhefhj.vechicle.tram;
import dasdwdjfhefhj.helpers.*;

public class Bogie {
    public final double offsetInSection; // m od początku członu
    public double sOnTrack;              // globalna pozycja po torze (m)
    public Vec2 worldPosXZ;              // pozycja w świecie
    public double heading;               // kąt styczny toru w miejscu wózka

    public Bogie(double offsetInSection) { this.offsetInSection = offsetInSection; }
}