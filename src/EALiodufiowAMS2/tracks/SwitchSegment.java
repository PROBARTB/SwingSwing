package EALiodufiowAMS2.tracks;

import EALiodufiowAMS2.helpers.*;

public class SwitchSegment implements TrackSegment {
    public enum Path { MAIN, BRANCH }

    private final String id;
    private final StraightSegment main;
    private final CurvedSegment branch;
    private Path currentPath = Path.MAIN;

    public SwitchSegment(String id, StraightSegment main, CurvedSegment branch) {
        this.id = id;
        this.main = main;
        this.branch = branch;
    }

    public void setPath(Path path) { this.currentPath = path; }
    public Path getPath() { return currentPath; }

    @Override public String getId() { return id; }

    @Override public double getLength() {
        return currentPath == Path.MAIN ? main.getLength() : branch.getLength();
    }

    @Override
    public Vec3 localPosition(double s) {
        return currentPath == Path.MAIN ? main.localPosition(s) : branch.localPosition(s);
    }

    @Override
    public Quaternion localRotation(double s) {
        return currentPath == Path.MAIN ? main.localRotation(s) : branch.localRotation(s);
    }

    @Override
    public TrackEndpoint getStart() { return new TrackEndpoint(new Vec3(0,0,0), localRotation(0)); }

    @Override
    public TrackEndpoint getEnd() {
        return currentPath == Path.MAIN ? main.getEnd() : branch.getEnd();
    }
}