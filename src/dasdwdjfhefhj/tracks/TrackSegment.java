package dasdwdjfhefhj.tracks;
import dasdwdjfhefhj.helpers.*;

public interface TrackSegment {
    double getLength();        // długość segmentu (m)
    Vec2 posAt(double s);      // pozycja na segmencie dla długości s od początku (m)
    Vec2 tangentAt(double s);  // wektor styczny w punkcie s (skala ~ prędkość parametryczna)
    TrackSegment getNext();    // domyślny następnik
    double exitHeading();      // kąt wyjścia (dziedziczy prosty po łuku)
    void setNext(TrackSegment next);
}
