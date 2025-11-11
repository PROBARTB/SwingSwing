//package EALiodufiowAMS2.world;
//
//import EALiodufiowAMS2.helpers.Vec2;
//import EALiodufiowAMS2.rendering.RenderingEngine;
//import EALiodufiowAMS2.rendering.TracksRenderer;
//import EALiodufiowAMS2.rendering.VehicleRenderer;
//import EALiodufiowAMS2.tracks.TrackLayout;
//import EALiodufiowAMS2.vehicle.RailVehicle;
//import EALiodufiowAMS2.vehicle.Section;
//
//import javax.swing.*;
//import java.awt.*;
//import java.awt.event.ActionEvent;
//import java.awt.event.KeyAdapter;
//import java.awt.event.KeyEvent;
//
//public class ScenePanel extends JPanel {
//    private final World world;
//    private final RenderingEngine engine;
//    private final TracksRenderer tracksRenderer;
//    private final VehicleRenderer vehicleRenderer;
//
//    private TrackLayout layout;
//
//    public ScenePanel(World world, RenderingEngine engine) {
//        this.world = world;
//        this.engine = engine;
//        this.tracksRenderer = new TracksRenderer(engine);
//        this.vehicleRenderer = new VehicleRenderer(engine);
//
//        setBackground(new Color(220, 230, 240));
//        setFocusable(true);
//
//        addKeyListener(new KeyAdapter() {
//            @Override public void keyPressed(KeyEvent e) {
//                if (world.getVehicles().isEmpty()) return;
//                RailVehicle v = world.getVehicles().get(0);
//                if (e.getKeyCode() == KeyEvent.VK_W) v.setSpeedMS(v.getSpeedMS() + 0.5);
//                if (e.getKeyCode() == KeyEvent.VK_S) v.setSpeedMS(Math.max(0.0, v.getSpeedMS() - 0.5));
//                if (e.getKeyCode() == KeyEvent.VK_1) v.setCameraSection(v.getSections().get(0));
//                if (e.getKeyCode() == KeyEvent.VK_2) v.setCameraSection(v.getSections().get(1));
//                if (e.getKeyCode() == KeyEvent.VK_3) v.setCameraSection(v.getSections().get(2));
//                if (e.getKeyCode() == KeyEvent.VK_4) v.setCameraSection(v.getSections().get(3));
//                if (e.getKeyCode() == KeyEvent.VK_5) v.setCameraSection(v.getSections().get(4));
//            }
//        });
//
//        Timer timer = new Timer(16, (ActionEvent e) -> {
//            layout = tracksRenderer.buildLayout(world.getTrackGraph(), new Vec2(0,0), 0.0);
//            world.update(0.016, layout);
//            updateCameraFromAttachment();
//            repaint();
//        });
//        timer.start();
//    }
//
//    private void updateCameraFromAttachment() {
//        if (world.getVehicles().isEmpty()) return;
//        RailVehicle v = world.getVehicles().get(0);
//        Section camSec = v.getCameraSection();
//        if (camSec == null) return;
//           // Ustaw kamerę na pozycji sekcji z jej kątem
//           engine.setCamera(camSec.getWorldPos(), camSec.getWorldAngle());
//    }
//
//    @Override
//    protected void paintComponent(Graphics g) {
//        super.paintComponent(g);
//        Dimension vp = getSize();
//        Graphics2D g2 = (Graphics2D) g.create();
//        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//
//        if (layout != null && !world.getVehicles().isEmpty()) {
//            RailVehicle v = world.getVehicles().get(0);
//            Section camSec = v.getCameraSection();
//            if (camSec != null) {
//                tracksRenderer.drawTracks(g2, layout, vp, camSec.getWorldPos());
//            }
//        }
//        for (RailVehicle v : world.getVehicles()) {
//            vehicleRenderer.drawVehicle(g2, v, vp);
//        }
//
//        if (!world.getVehicles().isEmpty()) {
//            RailVehicle v = world.getVehicles().get(0);
//            g2.setColor(Color.BLACK);
//            g2.drawString(String.format("Speed: %.2f m/s", v.getSpeedMS()), 10, 20);
//        }
//
//        g2.dispose();
//    }
//}
