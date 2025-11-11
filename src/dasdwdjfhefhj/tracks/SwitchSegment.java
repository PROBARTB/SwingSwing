package dasdwdjfhefhj.tracks;
import dasdwdjfhefhj.helpers.*;

public class SwitchSegment implements TrackSegment {
    private final StraightSegment lead;
    private final TrackSegment leftBranch;
    private final TrackSegment rightBranch;
    private TrackSegment nextAfter;
    private boolean chooseLeft = true;

    public SwitchSegment(Vec2 start, double headingLead) {
        // lead prosty 10 m
        this.lead = new StraightSegment(start, headingLead, 10.0);

        Vec2 leadEnd = lead.posAt(lead.getLength());
        double exitHeadingLead = lead.exitHeading();

        // prawa gałąź prosta 10 m
        this.rightBranch = new StraightSegment(leadEnd, exitHeadingLead, 10.0);
        // lewa gałąź łuk Béziera: offsetX=10, offsetZ=3, exitAngle=0
        this.leftBranch = new BezierArc(leadEnd, exitHeadingLead, 10.0, 3.0, 0.0);
    }

    public void setChoiceLeft(boolean left) { this.chooseLeft = left; }

    public double getLength() { return lead.getLength(); }
    public Vec2 posAt(double s) { return lead.posAt(s); }
    public Vec2 tangentAt(double s) { return lead.tangentAt(s); }

    public TrackSegment getNext() { return chooseLeft ? leftBranch : rightBranch; }

    public void setNext(TrackSegment next) {
        this.nextAfter = next;
        // podłącz po gałęziach
        if (leftBranch instanceof StraightSegment sl) sl.setNext(next);
        if (leftBranch instanceof BezierArc bl) bl.setNext(next);
        if (rightBranch instanceof StraightSegment sr) sr.setNext(next);
        if (rightBranch instanceof BezierArc br) br.setNext(next);
    }

    public double exitHeading() {
        TrackSegment br = chooseLeft ? leftBranch : rightBranch;
        return br.exitHeading();
    }

    public StraightSegment getLead() { return lead; }
    public TrackSegment getLeftBranch() { return leftBranch; }
    public TrackSegment getRightBranch() { return rightBranch; }
}
