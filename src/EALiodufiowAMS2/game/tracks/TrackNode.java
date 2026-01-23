package EALiodufiowAMS2.game.tracks;

import java.util.*;

public class TrackNode {
    public String id;
    public List<TrackSegment> connectedSegments = new ArrayList<>();

    public TrackNode(String id) { this.id = id; }

    public void connect(TrackSegment segment) {
        if (!connectedSegments.contains(segment)) {
            connectedSegments.add(segment);
        }
    }
}