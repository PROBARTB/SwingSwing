package EALiodufiowAMS2.vehicle;

import EALiodufiowAMS2.helpers.Vec3;

import java.util.*;

public abstract class RailVehicle {
    private final String id;
    private String currentTrackSegmentId;

    protected final List<Section> sections = new ArrayList<>();
    protected final List<Joint> joints = new ArrayList<>();
    protected final Map<Integer, Runnable> keyBindings;

    private Vec3 size;

    private double speed;           //  [m/s]
    private double acceleration;        //  [m/s^2]
    private boolean isPowered;
    private boolean isDriveEnabled;
    private int driveDirection;         // 1 / 0 / -1
    private double posOnTrack;     // pos in meters on currentTrackSegment
    private double distanceTravelled;

    public RailVehicle(String id) {
        this.id = id;
        this.keyBindings = new HashMap<>();
        this.speed = 0.0;
        this.acceleration = 0.0;
        this.isPowered = true; // temp true
        this.isDriveEnabled = true; // temp true
        this.driveDirection = 1;
        this.posOnTrack = 0.0;
        this.distanceTravelled = 0.0;
    }

    public String getId() { return id; }

    public String getCurrentTrackSegmentId() { return currentTrackSegmentId; }
    public void setCurrentTrackSegmentId(String segmentId) { this.currentTrackSegmentId = segmentId; }

    public Vec3 getSize() { return size; }
    public void setSize(Vec3 size) { this.size = size; }

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

}
