package EALiodufiowAMS2.tracks;

import EALiodufiowAMS2.helpers.*;

public abstract class TrackSegment {
    protected String id;
    protected double length;

    protected TrackNode startNode;
    protected TrackNode endNode;

    public TrackSegment(String id) {
        this.id = id;
    }

    public String getId() { return id; }
    public double getLength() { return length; }

    public void setNodes(TrackNode start, TrackNode end) {
        this.startNode = start;
        this.endNode = end;
        start.connect(this);
        end.connect(this);
    }

    public TrackNode getStartNode() { return startNode; }

    // Metoda wirtualna, bo SwitchSegment może mieć zmienny EndNode
    public TrackNode getEndNode() { return endNode; }

    public abstract Transform getLocalTransform(double distance);
}