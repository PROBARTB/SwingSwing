//package EALiodufiowAMS2.tracks;
//
//import EALiodufiowAMS2.helpers.Vec2;
//
//public class SwitchSegment implements TrackSegment {
//    private final TrackSegment curved;
//    private final TrackSegment straight;
//    private boolean useCurved;
//    private TrackSegment next; // zgodnie z grafem, choć zwykle końcówki prowadzą na własne next
//
//    public SwitchSegment(TrackSegment curved, TrackSegment straight) {
//        this.curved = curved;
//        this.straight = straight;
//        this.useCurved = false;
//    }
//
//    public void chooseCurved(boolean curvedPath) {
//        this.useCurved = curvedPath;
//    }
//
//    @Override
//    public double getLength() {
//        return (useCurved ? curved : straight).getLength();
//    }
//
//    @Override
//    public TrackSegment getNextSegment() {
//        return (useCurved ? curved : straight).getNextSegment();
//    }
//
//    @Override
//    public void setNextSegment(TrackSegment next) {
//        // w kontekście Switcha najczęściej next jest konfigurowane na podsegmentach
//        this.next = next;
//        // opcjonalnie można propagate, ale zostawiamy prosty mechanizm
//    }
//
//    @Override
//    public Vec2 positionAt(double sMeters) {
//        return (useCurved ? curved : straight).positionAt(sMeters);
//    }
//
//    @Override
//    public Vec2 directionAt(double sMeters) {
//        return (useCurved ? curved : straight).directionAt(sMeters);
//    }
//}
