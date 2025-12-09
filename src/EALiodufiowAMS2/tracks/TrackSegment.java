package EALiodufiowAMS2.tracks;

import EALiodufiowAMS2.helpers.*;

public interface TrackSegment {
    String getId();
    double getLength();
    Vec3 localPosition(double s);
    Quaternion localRotation(double s);
    TrackEndpoint getStart();
    TrackEndpoint getEnd();
}