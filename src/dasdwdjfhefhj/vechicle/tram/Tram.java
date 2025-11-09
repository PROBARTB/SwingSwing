package dasdwdjfhefhj.vechicle.tram;
import java.util.*;

public abstract class Tram {
    public final java.util.List<TramSection> sections = new ArrayList<>();
    public double speed = 0.0;        // m/s
    public double sLead = 0.0;        // pozycja lidera (m)
    public Bogie leaderBogie() { return sections.get(0).bogies.get(0); }

    // wyznacz dystans od lidera do danego wózka (sumę długości poprzednich członów + różnice offsetów)
    public double distanceFromLeader(int secIndex, int bogieIndex) {
        double sum = 0.0;
        for (int i = 0; i < secIndex; i++) sum += sections.get(i).length;
        double leaderOffset = sections.get(0).bogies.get(0).offsetInSection;
        double targetOffset = sections.get(secIndex).bogies.get(bogieIndex).offsetInSection;
        if (secIndex == 0) sum += (targetOffset - leaderOffset);
        else sum += targetOffset;
        return sum;
    }
}