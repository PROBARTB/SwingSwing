package dasdwdjfhefhj;

class World {
    public TrackSegment start;
    public Tram tram;
    public void update(double dt){
        tram.sLead+=tram.speed*dt;
        // lider = pierwszy bogie pierwszej sekcji
        Bogie leader=tram.sections.get(0).bogies.get(0);
        TrackCursor c=seekCursor(start, tram.sLead);
        leader.worldPosXZ=c.pos();
        leader.heading=c.heading();
        // pozostałe bogie
        int idx=0;
        for(int i=0;i<tram.sections.size();i++){
            TramSection sec=tram.sections.get(i);
            for(int b=0;b<sec.bogies.size();b++){
                Bogie bogie=sec.bogies.get(b);
                if(idx==0){idx++;continue;}
                double dist=distanceFromLeader(tram.sections,i,b);
                double sB=tram.sLead-dist;
                TrackCursor curB=seekCursor(start,sB);
                bogie.worldPosXZ=curB.pos();
                bogie.heading=curB.heading();
                idx++;
            }
        }
        // sekcje
        for(TramSection sec:tram.sections){
            if(sec.bogies.size()==2){
                Bogie b1=sec.bogies.get(0), b2=sec.bogies.get(1);
                Vec2 u=b2.worldPosXZ.sub(b1.worldPosXZ);
                Vec2 uHat=u.norm();
                sec.heading=Math.atan2(u.z,u.x);
                sec.originXZ=b1.worldPosXZ.sub(uHat.scale(b1.offsetInSection));
            } else if(sec.bogies.size()==1){
                Bogie b=sec.bogies.get(0);
                Vec2 dir=Vec2.fromAngle(b.heading);
                sec.heading=b.heading;
                sec.originXZ=b.worldPosXZ.sub(dir.scale(b.offsetInSection));
            }
        }
    }
    private TrackCursor seekCursor(TrackSegment start,double s){
        TrackCursor c=new TrackCursor();
        c.segment=start;c.sLocal=0;c.advance(s);
        return c;
    }
    private double distanceFromLeader(java.util.List<TramSection> secs,int si,int bi){
        double sum = 0.0;
        // dodaj długości wszystkich sekcji przed docelową
        for(int i=0;i<si;i++){
            sum += secs.get(i).length;
        }
        // różnica offsetów wózka względem lidera
        double leaderOffset = secs.get(0).bogies.get(0).offsetInSection;
        double targetOffset = secs.get(si).bogies.get(bi).offsetInSection;
        if(si==0){
            sum += (targetOffset - leaderOffset);
        } else {
            sum += targetOffset;
        }
        return sum;
    }
}
