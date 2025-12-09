package EALiodufiowAMS2.tracks;

import EALiodufiowAMS2.helpers.*;

public class TrackEdge {
    public final String id;
    public final TrackSegment segment;
    public final TrackNode startNode;
    public final TrackNode endNode;

    public TrackEdge(String id, TrackSegment segment, TrackNode start, TrackNode end) {
        this.id = id;
        this.segment = segment;
        this.startNode = start;
        this.endNode = end;
    }

    public Vec3 globalPosition(double s) {
        Vec3 local = segment.localPosition(s);
        // transform lokalnej pozycji przez startNode.pose
        return local.rotate(startNode.pose.getRot()).add(startNode.pose.getPos());
    }

    public Quaternion globalRotation(double s) {
        Quaternion local = segment.localRotation(s);
        return startNode.pose.getRot().multiply(local);
    }
}