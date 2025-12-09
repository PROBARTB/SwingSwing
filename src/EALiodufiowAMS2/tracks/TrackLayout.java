package EALiodufiowAMS2.tracks;

import EALiodufiowAMS2.helpers.*;

import java.util.*;

public class TrackLayout {
    private final Map<String, TrackNode> nodes = new HashMap<>();
    private final Map<String, TrackEdge> edges = new HashMap<>();

    public TrackLayout(List<TrackSegment> segments, Transform startPose) {
        buildLayout(segments, startPose);
    }

    private void buildLayout(List<TrackSegment> segments, Transform startPose) {
        TrackNode currentNode = new TrackNode("start", startPose);

        for (TrackSegment seg : segments) {
            // oblicz globalny endPose
            TrackEndpoint endLocal = seg.getEnd();
            Vec3 globalPos = endLocal.position.rotate(currentNode.pose.getRot())
                    .add(currentNode.pose.getPos());
            Quaternion globalRot = currentNode.pose.getRot().multiply(endLocal.rotation);
            Transform endPose = new Transform(globalPos, globalRot, new Vec3(1,1,1));

            TrackNode nextNode = new TrackNode(seg.getId() + "_end", endPose);
            TrackEdge edge = new TrackEdge(seg.getId(), seg, currentNode, nextNode);

            addEdge(edge);
            currentNode = nextNode;
        }
    }

    private void addEdge(TrackEdge edge) {
        edges.put(edge.id, edge);
        nodes.put(edge.startNode.id, edge.startNode);
        nodes.put(edge.endNode.id, edge.endNode);
        edge.startNode.edges.add(edge);
    }

    public TrackEdge getEdge(String id) { return edges.get(id); }

    public TrackEdge getNextEdge(TrackEdge current) {
        if (current.endNode.edges.isEmpty()) return null;
        return current.endNode.edges.get(0);
    }

    public Vec3 samplePosition(String edgeId, double s) {
        TrackEdge edge = edges.get(edgeId);
        return edge.globalPosition(s);
    }

    public Quaternion sampleRotation(String edgeId, double s) {
        TrackEdge edge = edges.get(edgeId);
        return edge.globalRotation(s);
    }

    public Collection<TrackEdge> getEdges() { return edges.values(); }
    public Collection<TrackNode> getNodes() { return nodes.values(); }
}
