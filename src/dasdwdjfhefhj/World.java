package dasdwdjfhefhj;


import dasdwdjfhefhj.tracks.*;
import dasdwdjfhefhj.vechicle.tram.*;
import dasdwdjfhefhj.helpers.*;

public class World {
    public TrackSegment start;
    public Tram tram;

    private static final double MAX_JOINT_ANGLE = Math.toRadians(30.0);

    public void update(double dt) {
        tram.sLead += tram.speed * dt;

        // 1) Lider
        Bogie leader = tram.sections.get(0).bogies.get(0);
        TrackCursor leaderCursor = seekCursor(tram.sLead);
        leader.sOnTrack = tram.sLead;
        leader.worldPosXZ = leaderCursor.pos();
        leader.heading = leaderCursor.heading();

        // 2) Pozostałe wózki
        int idx = 0;
        for (int i = 0; i < tram.sections.size(); i++) {
            TramSection sec = tram.sections.get(i);
            for (int b = 0; b < sec.bogies.size(); b++) {
                Bogie bogie = sec.bogies.get(b);
                if (idx == 0) { idx++; continue; }
                double dist = tram.distanceFromLeader(i, b);
                double sB = tram.sLead - dist;
                TrackCursor curB = seekCursor(sB);
                bogie.sOnTrack = sB;
                bogie.worldPosXZ = curB.pos();
                bogie.heading = curB.heading();
                idx++;
                System.out.printf("  Wózek %d: pos=(%.2f, %.2f), heading=%.2f rad, distFromLeader=%.2f%n",
                        b+1, bogie.worldPosXZ.x, bogie.worldPosXZ.z, bogie.heading,
                        tram.distanceFromLeader(i,b));

            }
        }

        // 3) Sekcje
        final double MAX_JOINT_ANGLE = Math.toRadians(30.0);
        for (int i = 0; i < tram.sections.size(); i++) {
            TramSection sec = tram.sections.get(i);

            Double baseHeading = (i > 0) ? tram.sections.get(i - 1).heading : null;
            double computedHeading;

            if (sec.bogies.size() >= 2) {
                Bogie b1 = sec.bogies.get(0), b2 = sec.bogies.get(1);
                Vec2 axis = b2.worldPosXZ.sub(b1.worldPosXZ);
                Vec2 uHat = axis.norm();
                double rawHeading = Math.atan2(uHat.z, uHat.x);
                computedHeading = (baseHeading != null) ? clampAngle(baseHeading, rawHeading, MAX_JOINT_ANGLE)
                        : rawHeading;
            } else if (sec.bogies.size() == 1) {
                Bogie b = sec.bogies.get(0);
                double rawHeading = b.heading;
                computedHeading = (baseHeading != null) ? clampAngle(baseHeading, rawHeading, MAX_JOINT_ANGLE)
                        : rawHeading;
            } else {
                computedHeading = (baseHeading != null) ? baseHeading : leader.heading;
            }
            sec.heading = computedHeading;

            // Origin
            if (i == 0) {
                // pierwszy człon: ustaw tak, aby liderowy wózek był wewnątrz sekcji
                Bogie b = tram.sections.get(0).bogies.get(0);
                Vec2 dir = Vec2.fromAngle(sec.heading);
                sec.originXZ = b.worldPosXZ.sub(dir.scale(b.offsetInSection));
            } else {
                // kolejne: origin = koniec poprzedniego + JOINT_GAP
                TramSection prev = tram.sections.get(i - 1);
                Vec2 prevDir = Vec2.fromAngle(prev.heading);
                Vec2 prevEnd = prev.originXZ.sub(prevDir.scale(prev.length));
                Vec2 jointPos = prevEnd; //.sub(prevDir.scale(Tram.JOINT_GAP))

                // LOGI diagnostyczne
                System.out.printf(
                        "Sekcja %d: length=%.2f, prevLength=%.2f, prevOrigin=(%.2f, %.2f), prevDir=(%.3f, %.3f), " +
                                "prevDir*length=(%.3f, %.3f), prevEnd=(%.3f, %.3f), jointPos=(%.3f, %.3f)%n",
                        i,
                        sec.length,
                        prev.length,
                        prev.originXZ.x, prev.originXZ.z,
                        prevDir.x, prevDir.z,
                        prevDir.scale(prev.length).x, prevDir.scale(prev.length).z,
                        prevEnd.x, prevEnd.z,
                        jointPos.x, jointPos.z
                );

                sec.originXZ = jointPos;
            }

//            System.out.printf("Sekcja %d: origin=(%.2f, %.2f), heading=%.2f rad, length=%.2f%n",
//                    i+1, sec.originXZ.x, sec.originXZ.z, sec.heading, sec.length);

        }
    }


    private TrackCursor seekCursor(double sGlobal) {
        TrackCursor c = new TrackCursor();
        c.segment = start; c.sLocal = 0;
        c.advance(sGlobal);
        return c;
    }

    private Vec2 sectionEnd(TramSection sec) {
        Vec2 dir = Vec2.fromAngle(sec.heading);
        return sec.originXZ.add(dir.scale(sec.length));
    }

    private double clampAngle(double base, double raw, double maxRad) {
        double d = raw - base;
        d = Math.atan2(Math.sin(d), Math.cos(d)); // różnica do [-π, π]
        if (Math.abs(d) > maxRad) raw = base + Math.signum(d) * maxRad;
        return raw;
    }
}
