//package EALiodufiowAMS2.world;
//
//import EALiodufiowAMS2.rendering.RenderingEngine;
//import EALiodufiowAMS2.tracks.CurvedSegment;
//import EALiodufiowAMS2.tracks.StraightSegment;
//import EALiodufiowAMS2.vehicles.trams.PesaSwing;
//
//import javax.swing.*;
//
//public class Game {
//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(() -> {
//            World world = new World();
//
//            // Graf torów (kolejność w liście = układ): prosty -> łuk -> prosty
//            world.addTrackSegment(new StraightSegment(50.0));
//            world.addTrackSegment(new CurvedSegment(30.0, 40.0));
//            world.addTrackSegment(new StraightSegment(50.0));
//
//            // Pojazd
//            PesaSwing tram = new PesaSwing();
//            tram.setSpeedMS(2.0);
//            world.addVehicle(tram);
//
//            RenderingEngine engine = new RenderingEngine();
//            engine.setPerspective(0.004, 0.50);
//
//            ScenePanel panel = new ScenePanel(world, engine);
//
//            JFrame frame = new JFrame("Tram Game - Pseudo 3D");
//            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//            frame.setSize(1000, 700);
//            frame.setLocationRelativeTo(null);
//            frame.setContentPane(panel);
//            frame.setVisible(true);
//        });
//    }
//}
