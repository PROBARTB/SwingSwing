package dasdwdjfhefhj;


import dasdwdjfhefhj.tracks.*;
import dasdwdjfhefhj.vechicle.tram.*;
import dasdwdjfhefhj.helpers.*;

public class World {
    public TrackSegment start;
    public Tram tram;

    public void update(double dt) {
        // 1) Integracja pozycji lidera
        tram.sLead += tram.speed * dt;

        // 2) Kursor dla lidera
        TrackCursor leaderCursor = seekCursor(tram.sLead);
        Bogie leader = tram.leaderBogie();
        leader.sOnTrack = tram.sLead;
        leader.worldPosXZ = leaderCursor.pos();
        leader.heading = leaderCursor.heading();

        // 3) Pozostałe wózki
        int idx = 0;
        for (int i = 0; i < tram.sections.size(); i++) {
            TramSection sec = tram.sections.get(i);
            for (int b = 0; b < sec.bogies.size(); b++) {
                Bogie bogie = sec.bogies.get(b);
                if (idx == 0) { idx++; continue; } // lider już ustawiony
                double dist = tram.distanceFromLeader(i, b);
                double sB = tram.sLead - dist;
                TrackCursor curB = seekCursor(sB);
                bogie.sOnTrack = sB;
                bogie.worldPosXZ = curB.pos();
                bogie.heading = curB.heading();
                idx++;
            }
        }

        // 4) Ustal transformację członów
        for (TramSection sec : tram.sections) {
            if (sec.bogies.size() >= 2) {
                Bogie b1 = sec.bogies.get(0);
                Bogie b2 = sec.bogies.get(1);
                Vec2 axis = b2.worldPosXZ.sub(b1.worldPosXZ);
                Vec2 uHat = axis.norm();
                sec.heading = Math.atan2(uHat.z, uHat.x);
                // origin = pozycja b1 cofnięta o offset b1 wzdłuż osi członu
                sec.originXZ = b1.worldPosXZ.sub(uHat.scale(b1.offsetInSection));
            } else if (sec.bogies.size() == 1) {
                Bogie b = sec.bogies.get(0);
                Vec2 dir = Vec2.fromAngle(b.heading);
                sec.heading = b.heading;
                sec.originXZ = b.worldPosXZ.sub(dir.scale(b.offsetInSection));
            } else {
                // sekcja bez wózków: podeprzyj poprzednią sekcją
                // proste rozwiązanie: jeśli poprzednia istnieje, ustaw origin na jej koniec
                int idxSec = tram.sections.indexOf(sec);
                if (idxSec > 0) {
                    TramSection prev = tram.sections.get(idxSec - 1);
                    Vec2 dirPrev = Vec2.fromAngle(prev.heading);
                    sec.heading = prev.heading;
                    sec.originXZ = prev.originXZ.add(dirPrev.scale(prev.length));
                }
            }
        }
    }

    private TrackCursor seekCursor(double sGlobal) {
        TrackCursor c = new TrackCursor();
        c.segment = start;
        c.sLocal = 0;
        c.advance(sGlobal);
        return c;
    }
}
