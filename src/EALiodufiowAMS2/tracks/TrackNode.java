package EALiodufiowAMS2.tracks;

import EALiodufiowAMS2.helpers.Transform;

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