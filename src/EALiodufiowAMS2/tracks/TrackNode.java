package EALiodufiowAMS2.tracks;

import EALiodufiowAMS2.helpers.Transform;

import java.util.*;

public class TrackNode {
    public final String id;
    public final Transform pose;
    public final List<TrackEdge> edges = new ArrayList<>();

    public TrackNode(String id, Transform pose) {
        this.id = id;
        this.pose = pose;
    }
}
