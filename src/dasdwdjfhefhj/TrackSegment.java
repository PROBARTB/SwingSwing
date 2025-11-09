package dasdwdjfhefhj;

public interface TrackSegment {
    double getLength();              // długość odcinka w metrach
    Vec2 posAt(double s);            // pozycja na torze dla odległości s ∈ [0, length]
    Vec2 tangentAt(double s);        // wektor styczny (nieznormalizowany) w punkcie s
    double curvatureAt(double s);    // krzywizna κ(s) (dla prostych = 0)
    TrackSegment getNext(Vec2 choiceHint); // dla rozjazdu: zwraca wybrany segment
}
