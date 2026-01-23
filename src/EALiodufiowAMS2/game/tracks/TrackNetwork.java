package EALiodufiowAMS2.game.tracks;

import EALiodufiowAMS2.helpers.*;

import java.util.*;

public // --- TrackNetwork ---
class TrackNetwork {
    private Map<String, TrackSegment> segments = new HashMap<>();
    private Map<String, TrackNode> nodes = new HashMap<>();

    // Cache pozycji globalnych (Start każdego segmentu)
    private Map<String, Transform> segmentsGlobalStart = new HashMap<>();

    // Lista segmentów "Startowych" (Roots) dla rozłącznych pętli
    private List<String> rootSegmentIds = new ArrayList<>();

    // --- Budowanie Grafu ---
    public void addSegment(TrackSegment seg) {
        segments.put(seg.getId(), seg);
    }

    public void addRoot(String segId, Transform worldPos) {
        if(segments.containsKey(segId)) {
            rootSegmentIds.add(segId);
            segmentsGlobalStart.put(segId, worldPos);
        }
    }

    // Przeliczenie pozycji w świecie dla całego grafu
    public void rebuildWorldPositions() {
        Set<String> visited = new HashSet<>();

        for (String rootId : rootSegmentIds) {
            propagatePosition(rootId, segmentsGlobalStart.get(rootId), visited);
        }
    }

    private void propagatePosition(String segId, Transform startTrans, Set<String> visited) {
        if (visited.contains(segId)) return;
        visited.add(segId);

        segmentsGlobalStart.put(segId, startTrans);
        TrackSegment seg = segments.get(segId);

        // Oblicz, gdzie kończy się ten segment
        Transform endTrans = startTrans.combine(seg.getLocalTransform(seg.getLength()));

        // Znajdź kolejne segmenty wychodzące z EndNode tego segmentu
        TrackNode endNode = seg.getEndNode(); // Tu uwaga przy Switchach!
        if (endNode != null) {
            for (TrackSegment nextSeg : endNode.connectedSegments) {
                // Unikamy cofania się do samego siebie
                if (nextSeg != seg) {
                    propagatePosition(nextSeg.getId(), endTrans, visited);
                }
            }
        }

        // UWAGA: Obsługa SwitchSegment w rendererze wymagałaby
        // rekurencji po obu ścieżkach, tutaj upraszczamy do aktywnej ścieżki.
    }

    // --- API dla Pojazdu ---

    public Transform getTransformAt(String segmentId, double distance) {
        TrackSegment seg = segments.get(segmentId);
        if (seg == null) return new Transform(new Vec3(0,0,0), Quaternion.identity());

        Transform globalStart = segmentsGlobalStart.get(segmentId);
        if (globalStart == null) return new Transform(new Vec3(0,0,0), Quaternion.identity()); // Błąd: segment niepodłączony

        Transform local = seg.getLocalTransform(distance);
        return globalStart.combine(local);
    }

    public TrackSegment getSegment(String id) {
        return segments.get(id);
    }

    public void switchToggle(String switchId) {
        TrackSegment seg = segments.get(switchId);
        if (seg instanceof SwitchSegment) {
            ((SwitchSegment) seg).toggle();
            System.out.println("Switch " + switchId + " toggled.");
            // Po przełączeniu trzeba odświeżyć graf pozycji (jeśli geometria się zmienia drastycznie)
            // lub tylko logikę połączeń.
        }
    }

    public Collection<TrackSegment> getAllSegments() {
        return segments.values();
    }
}