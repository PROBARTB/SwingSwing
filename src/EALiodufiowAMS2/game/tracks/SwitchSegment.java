package EALiodufiowAMS2.game.tracks;

import EALiodufiowAMS2.helpers.*;

public class SwitchSegment extends TrackSegment {
    private TrackSegment straightPath;
    private TrackSegment curvedPath;
    private boolean isSwitched = false; // false = straight, true = curved

    public SwitchSegment(String id, StraightSegment sPath, CurvedSegment cPath) {
        super(id);
        this.straightPath = sPath;
        this.curvedPath = cPath;
        // Długość domyślnie z głównego toru
        this.length = sPath.getLength();
    }

    public void toggle() {
        isSwitched = !isSwitched;
        // Aktualizacja długości i EndNode
        this.length = isSwitched ? curvedPath.getLength() : straightPath.getLength();
    }

    @Override
    public TrackNode getEndNode() {
        // Zwracamy węzeł końca aktywnej ścieżki
        return isSwitched ? curvedPath.getEndNode() : straightPath.getEndNode();
    }

    @Override
    public Transform getLocalTransform(double distance) {
        // Delegacja do aktywnego segmentu wewnętrznego
        return isSwitched ? curvedPath.getLocalTransform(distance)
                : straightPath.getLocalTransform(distance);
    }

    // Metody pomocnicze dla Renderera (żeby rysował oba tory)
    public TrackSegment getStraightPart() { return straightPath; }
    public TrackSegment getCurvedPart() { return curvedPath; }
}