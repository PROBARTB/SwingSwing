package dasdwdjfhefhj;

public class Bogie {
    public final double offsetInSection; // m od początku członu
    public double sOnTrack;              // m po globalnym torze (akumulator po grafie)
    public Vec2 worldPosXZ;              // pozycja na torze
    public double heading;               // kąt stycznej toru w miejscu bogie
    public Bogie(double offsetInSection) { this.offsetInSection = offsetInSection; }
}