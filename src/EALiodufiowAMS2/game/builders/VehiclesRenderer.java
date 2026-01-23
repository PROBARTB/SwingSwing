//package EALiodufiowAMS2.engine.rendering;
//
//import EALiodufiowAMS2.helpers.Vec2;
//import EALiodufiowAMS2.game.vehicle.Bogie;
//import EALiodufiowAMS2.game.vehicle.RailVehicle;
//import EALiodufiowAMS2.game.vehicle.Section;
//
//import java.awt.*;
//
//public class VehicleRenderer {
//    private final RenderingEngine engine;
//
//    public VehicleRenderer(RenderingEngine engine) { this.engine = engine; }
//
//    public void drawVehicle(Graphics2D g2, RailVehicle v, Dimension vp) {
//        for (int si = v.getSections().size() - 1; si >= 0; si--) {
//            Section sec = v.getSections().get(si);
//            //engine.drawTransformedRect(g2, sec.getWorldPos(), sec.getWorldAngle(), sec.getLength(), sec.getHeight(), vp, Color.RED);
//
//            int bi = 1;
//            for (Bogie b : sec.getBogies()) {
//                double angle = Math.atan2(b.getWorldDir().z, b.getWorldDir().x);
//                //engine.drawBogie(g2, b.getWorldPos(), angle, b.getLength(), b.getHeight(), vp, Color.BLUE);
//
//                // debug texts
//                Point label = engine.railToScreen(b.getWorldPos(), vp);
//                g2.setColor(Color.BLACK);
//                g2.drawString("bogie " + (bi++), label.x + 4, label.y - 4);
//            }
//
//            // debug texts
//            double c = Math.cos(sec.getWorldAngle()), s = Math.sin(sec.getWorldAngle());
//            Vec2 dir = new Vec2(c, s).norm();
//            Vec2 near = new Vec2(sec.getWorldPos().x - dir.x * (sec.getLength()/2.0),
//                    sec.getWorldPos().z - dir.z * (sec.getLength()/2.0));
//            Point label = engine.railToScreen(near, vp);
//            g2.setColor(Color.BLACK);
//            g2.drawString("section " + (si+1), label.x + 4, label.y - 4);
//        }
//
//
//        for (int i = v.getSections().size() - 2; i >= 0; i--) {
//            Section a = v.getSections().get(i);
//            Section b = v.getSections().get(i+1);
//            if (i < v.getJoints().size()) {
//                Vec2 aEnd = a.getEnd();
//                Vec2 bStart = b.getStart();
//                Vec2 jointCenter = new Vec2((aEnd.x + bStart.x)/2.0, (aEnd.z + bStart.z)/2.0);
//                Vec2 jointDir = new Vec2(bStart.x - aEnd.x, bStart.z - aEnd.z).norm();
//                double jointLen = new Vec2(bStart.x - aEnd.x, bStart.z - aEnd.z).len();
//                double jointHeight = 4; // zahardcodowane bo nie wiem jak to pobrać z joint który ma te wartosc
//                engine.drawTransformedRect(g2, jointCenter, jointDir, jointLen, jointHeight, vp, Color.GRAY);
//            }
//        }
//    }
//}
