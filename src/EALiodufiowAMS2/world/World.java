package EALiodufiowAMS2.world;

import EALiodufiowAMS2.tracks.TrackSegment;
import EALiodufiowAMS2.vehicle.RailVehicle;

import java.util.ArrayList;
import java.util.List;

public class World {
    private final List<TrackSegment> trackGraph = new ArrayList<>();
    private final List<RailVehicle> vehicles = new ArrayList<>();

    public void addTrackSegment(TrackSegment seg) { trackGraph.add(seg); }
    public List<TrackSegment> getTrackGraph() { return trackGraph; }

    public void addVehicle(RailVehicle v) { vehicles.add(v); }
    public List<RailVehicle> getVehicles() { return vehicles; }

    public void update(double dt, EALiodufiowAMS2.tracks.TrackLayout layout) {
        for (RailVehicle v : vehicles) {
            v.update(dt, layout);
        }
    }
}
