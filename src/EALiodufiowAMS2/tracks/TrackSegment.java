package EALiodufiowAMS2.tracks;

import EALiodufiowAMS2.helpers.Vec2;

public interface TrackSegment {
    double getLength();

    // Lokalna pozycja (0..length) względem P0=(0,0), headingStart=0 (oś X).
    Vec2 localPosAt(double sMeters);

    // Lokalny kierunek (jednostkowy) względem osi X.
    Vec2 localTangentAt(double sMeters);
}
