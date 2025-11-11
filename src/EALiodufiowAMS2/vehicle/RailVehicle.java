package EALiodufiowAMS2.vehicle;

import EALiodufiowAMS2.helpers.Vec2;
import EALiodufiowAMS2.tracks.TrackLayout;

import java.util.ArrayList;
import java.util.List;

public class RailVehicle {

    protected final List<Section> sections = new ArrayList<>();
    protected final List<Joint> joints = new ArrayList<>();

    protected Section cameraSection;
    protected double speedMS = 0.0;
    protected double leadingSGlobal = 0.0;

    public void setSpeedMS(double s) { this.speedMS = s; }
    public double getSpeedMS() { return speedMS; }

    public List<Section> getSections() { return sections; }
    public List<Joint> getJoints() { return joints; }

    public Section getCameraSection() { return cameraSection; }
    public void setCameraSection(Section sec) { this.cameraSection = sec; }

    protected double computeGlobalOffsetForBogie(int sectionIndex, Bogie bogie) {
        double offset = 0.0;
        for (int i = 0; i < sectionIndex; i++) {
            offset += sections.get(i).getLength();
            offset += joints.get(i).getLength();
        }
        offset += bogie.getOffsetInSection();
        Section first = sections.get(0);
        double leadingOffsetInFirst = first.getBogies().isEmpty() ? 0.0 : first.getBogies().get(0).getOffsetInSection();
        offset -= leadingOffsetInFirst;
        return offset;
    }

    public void update(double dt, TrackLayout layout) {
        if (layout == null || sections.isEmpty()) return;
        double trackLen = layout.getTotalLength();

        leadingSGlobal = Math.max(0.0, Math.min(trackLen, leadingSGlobal + speedMS * dt));

        for (int sIdx = 0; sIdx < sections.size(); sIdx++) {
            Section sec = sections.get(sIdx);
            for (Bogie b : sec.getBogies()) {
                double s = Math.max(0.0, Math.min(trackLen, leadingSGlobal + computeGlobalOffsetForBogie(sIdx, b)));
                b.setSAlongTrack(s);
                TrackLayout.Sample samp = layout.sampleAt(s);
                if (samp != null) b.setWorld(samp.worldPos, samp.worldDir);
            }
        }

        boolean[] hasTransform = new boolean[sections.size()];
        for (int sIdx = 0; sIdx < sections.size(); sIdx++) {
            Section sec = sections.get(sIdx);
            if (!sec.getBogies().isEmpty()) {
                double ax = 0, az = 0, dx = 0, dz = 0;
                for (Bogie b : sec.getBogies()) {
                    ax += b.getWorldPos().x; az += b.getWorldPos().z;
                    dx += b.getWorldDir().x; dz += b.getWorldDir().z;
                }
                int n = sec.getBogies().size();
                Vec2 pos = new Vec2(ax / n, az / n);
                Vec2 dir = new Vec2(dx / n, dz / n).norm();
                sec.setWorld(pos, dir);
                hasTransform[sIdx] = true;
            }
        }

        for (int sIdx = 0; sIdx < sections.size(); sIdx++) {
            Section sec = sections.get(sIdx);
            if (sec.getBogies().isEmpty()) {
                Vec2 startAnchor = null, endAnchor = null;

                if (sIdx > 0 && hasTransform[sIdx - 1]) {
                    Section prev = sections.get(sIdx - 1);
                    Joint jPrev = joints.get(sIdx - 1);
                    Vec2 prevEnd = prev.getEnd();
                    startAnchor = prev.getStart();
                }

                if (sIdx < sections.size() - 1 && hasTransform[sIdx + 1]) {
                    Section next = sections.get(sIdx + 1);
                    Joint jThis = joints.get(sIdx);
                    Vec2 nextStart = next.getStart();
                    endAnchor = next.getEnd();
                }

                if (startAnchor != null && endAnchor != null) {
                    Vec2 mid = new Vec2((startAnchor.x + endAnchor.x) / 2.0,
                            (startAnchor.z + endAnchor.z) / 2.0);
                    Vec2 dir = endAnchor.sub(startAnchor).norm();
                    sec.setWorld(mid, dir);
                    hasTransform[sIdx] = true;
                } else if (startAnchor != null) {
                    Section prev = sections.get(sIdx - 1);
                    Vec2 dir = prev.getWorldDir();
                    Vec2 pos = new Vec2(
                            startAnchor.x + dir.x * (sec.getLength() / 2.0),
                            startAnchor.z + dir.z * (sec.getLength() / 2.0)
                    );
                    sec.setWorld(pos, dir);
                    hasTransform[sIdx] = true;
                } else if (endAnchor != null) {
                    Section next = sections.get(sIdx + 1);
                    Vec2 dir = next.getWorldDir();
                    Vec2 pos = new Vec2(
                            endAnchor.x - dir.x * (sec.getLength() / 2.0),
                            endAnchor.z - dir.z * (sec.getLength() / 2.0)
                    );
                    sec.setWorld(pos, dir);
                    hasTransform[sIdx] = true;
                } else {
                    int left = sIdx - 1;
                    while (left >= 0 && !hasTransform[left]) left--;
                    if (left >= 0) {
                        Section ref = sections.get(left);
                        sec.setWorld(ref.getWorldPos(), ref.getWorldDir());
                        hasTransform[sIdx] = true;
                    }
                }
            }
        }
    }
}
