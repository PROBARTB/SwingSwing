//package EALiodufiowAMS2.rendering.builders;
//////
//////import EALiodufiowAMS2.helpers.*;
//////import EALiodufiowAMS2.rendering.renderingObjects.Line;
//////import EALiodufiowAMS2.rendering.renderingObjects.RenderingObject;
//////import EALiodufiowAMS2.tracks.*;
//////import EALiodufiowAMS2.world.World;
//////
//////import java.util.*;
//////
//////public class TracksRenderer implements Renderer {
//////
//////    //https://copilot.microsoft.com/shares/HpaDbXKJfxwxXvWbcN9pz
//////
//////
//////    private final World world;
//////    private final List<RenderingObject> lines;
//////
//////    public TracksRenderer (World world) {
//////        this.world = world;
//////        this.lines = buildTrackLines(world.getLayout(), 1.0);
//////    }
//////
//////    public List<RenderingObject> buildTrackLines(TrackLayout layout, double gauge) {
//////        List<RenderingObject> lines = new ArrayList<>();
//////        for (TrackEdge edge : layout.getEdges()) {
//////            int samples = 20; // ile próbek na odcinek
//////            for (int i = 0; i < samples; i++) {
//////                double s0 = edge.segment.getLength() * i / samples;
//////                double s1 = edge.segment.getLength() * (i+1) / samples;
//////
//////                Vec3 pos0 = edge.globalPosition(s0);
//////                Vec3 pos1 = edge.globalPosition(s1);
//////
//////                // offset w prawo/lewo od osi toru
//////                Vec3 forward = pos1.sub(pos0).normalize();
//////                Vec3 up = new Vec3(0,1,0);
//////                Vec3 right = forward.cross(up).normalize().scale(gauge/2.0);
//////
//////                // lewa szyna
//////                lines.add(new Line(pos0.sub(right), pos1.sub(right)));
//////                // prawa szyna
//////                lines.add(new Line(pos0.add(right), pos1.add(right)));
//////            }
//////        }
//////        return lines;
//////    }
//////
//////
//////    @Override
//////    public void update(double deltaTime) {
//////
//////    }
//////
//////    @Override
//////    public List<String> getObjectIds() {
//////        return new ArrayList<>();
//////    }
//////
//////    @Override
//////    public RenderingObject buildRenderingObject(String id) {
//////        return null;
//////    }
//////
//////    @Override
//////    public Transform getObjectTransform(String id) {
//////        RenderingObject obj = null;
//////        return obj != null ? obj.getTransform() : null;
//////    }
//////
//////}
////
//import EALiodufiowAMS2.helpers.Quaternion;
//import EALiodufiowAMS2.helpers.Transform;
//import EALiodufiowAMS2.helpers.Vec3;
//import EALiodufiowAMS2.rendering.renderingObject.CurvedLine;
//import EALiodufiowAMS2.rendering.renderingObject.RenderingObject;
//import EALiodufiowAMS2.rendering.renderingObject.StraightLine;
//import EALiodufiowAMS2.tracks.CurvedSegment;
//import EALiodufiowAMS2.tracks.TrackNetwork;
//import EALiodufiowAMS2.tracks.TrackSegment;
//
//import java.awt.*;
//import java.util.*;
//import java.util.List;
//
////public class TracksRenderer implements Renderer {
////    private final TrackNetwork network;
////    private final double HALF_GAUGE = 1.435 / 2.0; // Standardowy rozstaw szyn / 2
////
////    // Cache mapujący ID wizualne -> Definicja obiektu
////    // Używamy tego, by getObjectTransform i buildRenderingObject działały szybko
////    private final Map<String, RenderJob> renderJobs = new HashMap<>();
////    private final List<String> objectIds = new ArrayList<>();
////
////    // Klasa pomocnicza trzymająca dane potrzebne do stworzenia obiektu
////    private static class RenderJob {
////        String originalSegmentId;
////        boolean isLeftRail; // true = lewa szyna, false = prawa
////        boolean isCurved;   // typ segmentu
////        // Parametry dla buildera:
////        Transform startT, endT;
////        double curvature; // Tylko dla curved
////        Color color;
////
////        public RenderJob(String segId, boolean left, boolean curved, Transform s, Transform e, double c, Color col) {
////            this.originalSegmentId = segId;
////            this.isLeftRail = left;
////            this.isCurved = curved;
////            this.startT = s;
////            this.endT = e;
////            this.curvature = c;
////            this.color = col;
////        }
////    }
////
////    public TracksRenderer(TrackNetwork network) {
////        this.network = network;
////        rebuildRenderList();
////    }
////
////    // Wywoływane raz lub gdy zmieni się układ torów (np. edytor)
////    // SwitchToggle nie wymaga przebudowy listy, tylko update() dla animacji
////    public void rebuildRenderList() {
////        renderJobs.clear();
////        objectIds.clear();
////
////        for (TrackSegment seg : network.getAllSegments()) {
////            // Pobieramy globalną pozycję startu segmentu z Networku
////            Transform globalStart = network.getVehicleTransform(seg.getId(), 0);
////
////            // Tworzymy dwie szyny dla każdego segmentu
////            createRailJob(seg, globalStart, true);  // Lewa
////            createRailJob(seg, globalStart, false); // Prawa
////
////            // Tutaj można dodać podkłady (Sleepers) jako osobne obiekty StraightLine
////        }
////    }
////
////    private void createRailJob(TrackSegment seg, Transform globalSegStart, boolean isLeft) {
////        String visualId = seg.getId() + (isLeft ? "_L" : "_R");
////        double offset = isLeft ? -HALF_GAUGE : HALF_GAUGE;
////
////        // 1. Obliczamy Globalny Start Szyny
////        // Przesuwamy punkt startowy w bok względem jego rotacji
////        Vec3 offsetVec = globalSegStart.getRot().right().multiply(offset); // right() musi zwracać wektor "w prawo" z kwaternionu
////        Transform railStart = new Transform(
////                globalSegStart.position.add(offsetVec),
////                globalSegStart.rotation // Rotacja ta sama co środek toru
////        );
////
////        // 2. Obliczamy Globalny Koniec Szyny
////        // Musimy pobrać lokalny koniec segmentu i dodać go do globalnego startu
////        Transform localEnd = seg.getLocalTransform(seg.getLength());
////        Transform globalSegEnd = globalSegStart.combine(localEnd);
////
////        Vec3 endOffsetVec = globalSegEnd.rotation.right().multiply(offset);
////        Transform railEnd = new Transform(
////                globalSegEnd.position.add(endOffsetVec),
////                globalSegEnd.rotation
////        );
////
////        // 3. Rejestrujemy zadanie
////        if (seg instanceof CurvedSegment) {
////            // Tu jest trick z CurvatureFactor.
////            // Dla łuków równoległych promień się zmienia (R_wew = R - offset, R_zew = R + offset).
////            // Jeśli CurvatureFactor to np. 1/R, trzeba to skorygować.
////            // Zakładam jednak, że engine używa Transformów (stycznych), więc factor może zostać zbliżony.
////            double originalCurvature = 1.0; // Wartość domyślna lub pobrana z seg
////
////            renderJobs.put(visualId, new RenderJob(
////                    seg.getId(), isLeft, true, railStart, railEnd, originalCurvature, Color.GRAY
////            ));
////        } else {
////            renderJobs.put(visualId, new RenderJob(
////                    seg.getId(), isLeft, false, railStart, railEnd, 0, Color.GRAY
////            ));
////        }
////        objectIds.add(visualId);
////    }
////
////    @Override
////    public void update(double deltaTime) {
////        // Tu obsługujemy animację zwrotnic.
////        // Sprawdzamy stan SwitchSegmentów i ewentualnie podmieniamy geometrię
////        // dla "iglic" (ruchomych części toru).
////        // W najprostszej wersji statycznej - puste.
////    }
////
////    @Override
////    public List<String> getObjectIds() {
////        return objectIds;
////    }
////
////    @Override
////    public Transform getObjectTransform(String id) {
////        // Zwracamy pozycję startową danego kawałka szyny
////        RenderJob job = renderJobs.get(id);
////        if (job != null) return job.startT;
////        return new Transform(new Vec3(0,0,0), Quaternion.identity());
////    }
////
////    @Override
////    public RenderingObject buildRenderingObject(String id) {
////        RenderJob job = renderJobs.get(id);
////        if (job == null) return null;
////
////        if (job.isCurved) {
////            // Tworzymy CurvedLine używając Transformów (które zawierają rotację/styczną)
////            // Dzięki temu renderer wie, w którą stronę wygiąć linię
////            return new CurvedLine(job.startT, job.endT, job.curvature, job.color);
////        } else {
////            return new StraightLine(job.startT.position, job.endT.position, job.color);
////        }
////    }
////}
//
//
//
/////https://copilot.microsoft.com/shares/6BvAoREWYYoxkBx7Fnndr
//
//public class TracksBuilder implements Builder {
//    private final TrackNetwork network;
//    private final double HALF_GAUGE = 1.435 / 2.0;
//
//    private final Map<String, RenderJob> renderJobs = new HashMap<>();
//    private final List<String> objectIds = new ArrayList<>();
//
//    private static class RenderJob {
//        String originalSegmentId;
//        boolean isLeftRail;
//        boolean isCurved;
//
//        Transform startT, endT;
//        double curvature;
//        Color color;
//
//        public RenderJob(String segId, boolean left, boolean curved,
//                         Transform s, Transform e, double c, Color col) {
//            this.originalSegmentId = segId;
//            this.isLeftRail = left;
//            this.isCurved = curved;
//            this.startT = s;
//            this.endT = e;
//            this.curvature = c;
//            this.color = col;
//        }
//    }
//
//    public TracksBuilder(TrackNetwork network) {
//        this.network = network;
//        rebuildRenderList();
//    }
//
//    public void rebuildRenderList() {
//        renderJobs.clear();
//        objectIds.clear();
//
//        for (TrackSegment seg : network.getAllSegments()) {
//            Transform globalStart = network.getTransformAt(seg.getId(), 0);
//
//            createRailJob(seg, globalStart, true);
//            createRailJob(seg, globalStart, false);
//        }
//    }
//
//    private void createRailJob(TrackSegment seg, Transform globalSegStart, boolean isLeft) {
//        String visualId = seg.getId() + (isLeft ? "_L" : "_R");
//        double offset = isLeft ? -HALF_GAUGE : HALF_GAUGE;
//
//        // START RAIL
//        Vec3 offsetVec = globalSegStart.getRot().right().scale(offset);
//        Transform railStart = new Transform(
//                globalSegStart.getPos().add(offsetVec),
//                globalSegStart.getRot()
//        );
//
//        // END RAIL
//        Transform localEnd = seg.getLocalTransform(seg.getLength());
//        Transform globalSegEnd = globalSegStart.combine(localEnd);
//
//        Vec3 endOffsetVec = globalSegEnd.getRot().right().scale(offset);
//        Transform railEnd = new Transform(
//                globalSegEnd.getPos().add(endOffsetVec),
//                globalSegEnd.getRot()
//        );
//
//        boolean curved = seg instanceof CurvedSegment;
//        double curvature = curved ? ((CurvedSegment) seg).getCurvature() : 0;
//
//        renderJobs.put(visualId, new RenderJob(
//                seg.getId(), isLeft, curved, railStart, railEnd, curvature, Color.GRAY
//        ));
//
//        objectIds.add(visualId);
//    }
//
//    @Override
//    public void update(double deltaTime) {}
//
//    @Override
//    public List<String> getObjectIds() {
//        return objectIds;
//    }
//
//    @Override
//    public Transform getObjectTransform(String id) {
//        RenderJob job = renderJobs.get(id);
//        return job != null ? job.startT : new Transform(new Vec3(0,0,0), Quaternion.identity());
//    }
//
//    @Override
//    public RenderingObject buildRenderingObject(String id) {
//        RenderJob job = renderJobs.get(id);
//        if (job == null) return null;
//
//        if (job.isCurved) {
//            return new CurvedLine(job.startT, job.endT, job.curvature, job.color);
//        } else {
//            return new StraightLine(job.startT.getPos(), job.endT.getPos(), job.color);
//        }
//    }
//}
//
