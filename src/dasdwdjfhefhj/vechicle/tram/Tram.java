package dasdwdjfhefhj.vechicle.tram;
import java.util.*;

public abstract class Tram {
    public static final double JOINT_GAP = 0.5; // [m] odstęp między członami (przegub)
    public final java.util.List<TramSection> sections = new java.util.ArrayList<>();
    public double speed = 0.0;   // m/s
    public double sLead = 0.0;   // globalna pozycja lidera

    public Bogie leaderBogie() { return sections.get(0).bogies.get(0); }

    // Dystans od lidera do podanego wózka: suma długości sekcji + przeguby + lokalny offset różnicowy
    // Tram.java — spójny dystans po torze dla wózków względem lidera
    public double distanceFromLeader(int secIndex, int bogieIndex) {
        double sum = 0.0;
        // pełne długości sekcji przed docelową + przeguby
        for (int i = 0; i < secIndex; i++) {
            sum += sections.get(i).length;
            sum += JOINT_GAP;
        }
        // różnica offsetów w obrębie sekcji lidera, a dalej po prostu offset docelowego wózka
        double leaderOffset = sections.get(0).bogies.get(0).offsetInSection;
        double targetOffset = sections.get(secIndex).bogies.get(bogieIndex).offsetInSection;
        if (secIndex == 0) sum += (targetOffset - leaderOffset);
        else sum += targetOffset;
        return sum;
    }

}
