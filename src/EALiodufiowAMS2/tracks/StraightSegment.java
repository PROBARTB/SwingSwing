package EALiodufiowAMS2.tracks;

import EALiodufiowAMS2.helpers.*;

public class StraightSegment extends TrackSegment {

    public StraightSegment(String id, double length) {
        super(id);
        this.length = length;
    }

    @Override
    public Transform getLocalTransform(double distance) {
        // Prosta linia wzdłuż osi X
        distance = Math.max(0, Math.min(distance, length)); // Clamp
        return new Transform(new Vec3(distance, 0, 0), Quaternion.identity());
    }
}