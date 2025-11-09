package dasdwdjfhefhj.tracks;
import dasdwdjfhefhj.helpers.*;

public class SwitchSegment implements TrackSegment {
    private final StraightSegment lead;
    private final TrackSegment leftBranch;
    private final TrackSegment rightBranch;
    private boolean chooseLeft = true; // można sterować zewnętrznie
    private TrackSegment nextAfterBranch; // segment po gałęziach (opcjonalnie)

    public SwitchSegment(Vec2 start, double headingLead) {
        // lead prosty 10 m
        this.lead = new StraightSegment(start, headingLead, 10.0);

        // Pozycja końca lead
        Vec2 leadEnd = lead.posAt(lead.getLength());
        double exitHeadingLead = lead.exitHeading();

        // Gałąź prawa: prosty 10 m (jako przykładowa)
        this.rightBranch = new StraightSegment(leadEnd, exitHeadingLead, 10.0);

        // Gałąź lewa: łuk Béziera offsetX=10, offsetZ=3, exitAngle=0
        // Uwaga: offset jest względem leadEnd (końca prowadzenia)
        this.leftBranch = new BezierArc(leadEnd, exitHeadingLead, 10.0, 3.0, 0.0);

        // Domyślny „next” nie ma znaczenia na samym Switch, používamy getNext() dynamicznie
    }

    public void setChoiceLeft(boolean left) { this.chooseLeft = left; }

    public double getLength() { return lead.getLength(); }

    public Vec2 posAt(double s) { return lead.posAt(s); }

    public Vec2 tangentAt(double s) { return lead.tangentAt(s); }

    public TrackSegment getNext() {
        // Zwraca wybraną gałąź po przejechaniu całego lead (obsługiwane w TrackCursor.advance)
        return chooseLeft ? leftBranch : rightBranch;
    }

    public double exitHeading() {
        // Exit heading = heading wybranego branch po jego końcu (jeśli ktoś chce „dziedziczyć”)
        TrackSegment br = chooseLeft ? leftBranch : rightBranch;
        return br.exitHeading();
    }

    public void setNext(TrackSegment next) {
        this.nextAfterBranch = next;
        // podpinamy nextAfterBranch do końca obu gałęzi
        if (leftBranch instanceof StraightSegment s) { s.setNext(next); }
        if (leftBranch instanceof BezierArc b) { b.setNext(next); }
        if (rightBranch instanceof StraightSegment s2) { s2.setNext(next); }
        if (rightBranch instanceof BezierArc b2) { b2.setNext(next); }
    }

    public StraightSegment getLead() { return lead; }

    public TrackSegment getLeftBranch() { return leftBranch; }
    public TrackSegment getRightBranch() { return rightBranch; }
}
