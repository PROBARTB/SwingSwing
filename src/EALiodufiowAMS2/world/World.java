package EALiodufiowAMS2.world;

import EALiodufiowAMS2.helpers.*;
import EALiodufiowAMS2.tracks.*;
import EALiodufiowAMS2.tracks.TrackSegment;
//import EALiodufiowAMS2.vehicle.RailVehicle;

import java.util.ArrayList;
import java.util.List;

public class World { ;
//    private final List<RailVehicle> vehicles = new ArrayList<>();
//
//    public void addTrack(TrackSegment t) { tracks.add(t); }
//    public void addVehicle(RailVehicle v) { vehicles.add(v); }
//
//    public List<TrackSegment> getTracks() { return Collections.unmodifiableList(tracks); }
//    public List<RailVehicle> getVehicles() { return Collections.unmodifiableList(vehicles); }


    private final TrackLayout layout;

    public World() {
        List<TrackSegment> segments = new ArrayList<>();
        segments.add(new StraightSegment("straight1", 100));
        segments.add(new CurvedSegment("curve1", 50, Math.PI * 25));
        segments.add(new StraightSegment("straight2", 80));
        segments.add(new CurvedSegment("curve2", 40, 60, Math.toRadians(45)));

        StraightSegment main1 = new StraightSegment("switchMain1", 70);
        CurvedSegment branch1 = new CurvedSegment("switchBranch1", 30, Math.PI * 15);
        segments.add(new SwitchSegment("switch1", main1, branch1));

        segments.add(new StraightSegment("straight3", 60));

        StraightSegment main2 = new StraightSegment("switchMain2", 90);
        CurvedSegment branch2 = new CurvedSegment("switchBranch2", 25, 40, Math.toRadians(-30));
        segments.add(new SwitchSegment("switch2", main2, branch2));

        segments.add(new StraightSegment("straight4", 120));

        Transform startPose = new Transform(new Vec3(0,0,0), Quaternion.fromEuler(new Vec3(Math.toRadians(0), 0, 0)), new Vec3(1,0,1));
        this.layout = new TrackLayout(segments, startPose);
    }

    public TrackLayout getLayout() { return layout; }
}
