//package EALiodufiowAMS2.rendering.renderers;
//
//import EALiodufiowAMS2.helpers.*;
//import EALiodufiowAMS2.rendering.renderingObjects.Line;
//import EALiodufiowAMS2.rendering.renderingObjects.RenderingObject;
//import EALiodufiowAMS2.tracks.*;
//import EALiodufiowAMS2.world.World;
//
//import java.util.*;
//
//public class TracksRenderer implements Renderer {
//
//    //https://copilot.microsoft.com/shares/HpaDbXKJfxwxXvWbcN9pz
//
//
//    private final World world;
//    private final List<RenderingObject> lines;
//
//    public TracksRenderer (World world) {
//        this.world = world;
//        this.lines = buildTrackLines(world.getLayout(), 1.0);
//    }
//
//    public List<RenderingObject> buildTrackLines(TrackLayout layout, double gauge) {
//        List<RenderingObject> lines = new ArrayList<>();
//        for (TrackEdge edge : layout.getEdges()) {
//            int samples = 20; // ile próbek na odcinek
//            for (int i = 0; i < samples; i++) {
//                double s0 = edge.segment.getLength() * i / samples;
//                double s1 = edge.segment.getLength() * (i+1) / samples;
//
//                Vec3 pos0 = edge.globalPosition(s0);
//                Vec3 pos1 = edge.globalPosition(s1);
//
//                // offset w prawo/lewo od osi toru
//                Vec3 forward = pos1.sub(pos0).normalize();
//                Vec3 up = new Vec3(0,1,0);
//                Vec3 right = forward.cross(up).normalize().scale(gauge/2.0);
//
//                // lewa szyna
//                lines.add(new Line(pos0.sub(right), pos1.sub(right)));
//                // prawa szyna
//                lines.add(new Line(pos0.add(right), pos1.add(right)));
//            }
//        }
//        return lines;
//    }
//
//
//    @Override
//    public void update(double deltaTime) {
//
//    }
//
//    @Override
//    public List<String> getObjectIds() {
//        return new ArrayList<>();
//    }
//
//    @Override
//    public RenderingObject buildRenderingObject(String id) {
//        return null;
//    }
//
//    @Override
//    public Transform getObjectTransform(String id) {
//        RenderingObject obj = null;
//        return obj != null ? obj.getTransform() : null;
//    }
//
//}