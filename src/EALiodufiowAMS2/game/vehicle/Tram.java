package EALiodufiowAMS2.game.vehicle;

import java.util.List;

public class Tram extends RailVehicle {
    public Tram(String id, List<Section> sections, List<Joint> joints, String currentTrackSegmentId, double posOnTrack) {
        super(id, sections, joints, currentTrackSegmentId, posOnTrack);
    }
}
