package dasdwdjfhefhj;

import java.util.*;




public class Tram {
    public final List<TramSection> sections = new ArrayList<>();
    public double speed;    // m/s
    public double sLead;    // pozycja wzdłuż toru lidera (np. pierwszego bogie pierwszej sekcji)
    public TrackCursor cursor; // pozycja w grafie: segment + s lokalne
}
