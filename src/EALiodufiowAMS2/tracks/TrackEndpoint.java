package EALiodufiowAMS2.tracks;

import EALiodufiowAMS2.helpers.*;

public class TrackEndpoint {
    public final Vec3 position;
    public final Quaternion rotation;

    public TrackEndpoint(Vec3 pos, Quaternion rot) {
        this.position = pos;
        this.rotation = rot;
    }
}