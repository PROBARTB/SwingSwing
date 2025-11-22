package EALiodufiowAMS2.vehicle;

import EALiodufiowAMS2.helpers.Transform;
import EALiodufiowAMS2.helpers.Vec3;

import java.util.*;

public abstract class RailVehicle {
    private final String id;
    private String currentTrackSegmentId;

    protected final List<Section> sections;
    protected final List<Joint> joints;
    protected final Map<Integer, Runnable> keyBindings;

    private final Vec3 size;

    private double speed;           //  [m/s]
    private double acceleration;        //  [m/s^2]
    private boolean isPowered;
    private boolean isDriveEnabled;
    private int driveDirection;         // 1 / 0 / -1
    private double posOnTrack;     // pos in meters on currentTrackSegment
    private double distanceTravelled;

    public RailVehicle(String id, List<Section> sections, List<Joint> joints, String currentTrackSegmentId, double posOnTrack) {
        this.id = id;
        this.keyBindings = new HashMap<>();

        this.sections = sections;
        this.joints = joints;

        this.size = calcSize(sections, joints);

        this.speed = 0.0;
        this.acceleration = 0.0;
        this.isPowered = true; // temp true
        this.isDriveEnabled = true; // temp true
        this.driveDirection = 1;
        this.currentTrackSegmentId = currentTrackSegmentId;
        this.posOnTrack = posOnTrack;
        this.distanceTravelled = 0.0;
    }

    private Vec3 calcSize(List<Section> sections, List<Joint> joints) {
        Vec3 max = new Vec3(0, 0, 0);
        for(Section s : sections) {
            Vec3 ss = s.getTransform().getSize();
            if (ss.x > max.x) max.x = ss.x;
            if (ss.y > max.y) max.y = ss.y;
            if (ss.z > max.z) max.z = ss.z;

            for(Bogie b : s.getBogies()) {
                Vec3 bs = b.getTransform().getSize();
                if (bs.x > max.x) max.x = bs.x;
                if (bs.y > max.y) max.y = bs.y;
                if (bs.z > max.z) max.z = bs.z;
            }
        }
        for(Joint j : joints) {
            Vec3 js = j.getTransform().getSize();
            if (js.x > max.x) max.x = js.x;
            if (js.y > max.y) max.y = js.y;
            if (js.z > max.z) max.z = js.z;
        }
        return max;
    }

    public String getId() { return id; }

    public String getCurrentTrackSegmentId() { return currentTrackSegmentId; }
    public void setCurrentTrackSegmentId(String segmentId) { this.currentTrackSegmentId = segmentId; }

    public Transform getTransform() { return this.sections.get(0).getBogies().get(0).getTransform(); }
    public Vec3 getSize() { return this.size; }

    public double getSpeed() { return speed; }
    public void setSpeed(double speed) { this.speed = speed; }

    public double getAcceleration() { return acceleration; }
    public void setAcceleration(double acceleration) { this.acceleration = acceleration; }

    public boolean isPowered() { return isPowered; }
    public void setPowered(boolean powered) { this.isPowered = powered; }

    public boolean isDriveEnabled() { return isDriveEnabled; }
    public void setDriveEnabled(boolean enabled) { this.isDriveEnabled = enabled; }

    public int getDriveDirection() { return driveDirection; }
    public void setDriveDirection(int direction) { this.driveDirection = direction; }

    public double getPosOnTrack() { return posOnTrack; }
    public void setPosOnTrack(double positionOnTrack) { this.posOnTrack = positionOnTrack; }

    public double getDistanceTravelled() { return distanceTravelled; }
    public void addDistanceTravelled(double deltaMeters) { this.distanceTravelled += Math.max(0.0, deltaMeters); }

    public Map<Integer, Runnable> getKeyBindings() { return keyBindings; }

    public void bindKey(int keyCode, Runnable action) {
        if (action == null) return;
        keyBindings.put(keyCode, action);
    }
    public void unbindKey(int keyCode) {
        keyBindings.remove(keyCode);
    }
    public void clearBindings() {
        keyBindings.clear();
    }

    public List<Section> getSections() { return sections; }

    public List<Joint> getJoints() { return joints; }
}
