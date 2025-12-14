package EALiodufiowAMS2.world;

//import EALiodufiowAMS2.tracks.StraightSegment;
//import EALiodufiowAMS2.vehicles.trams.PesaSwing;
import EALiodufiowAMS2.world.scenes.ScenePanel;
import EALiodufiowAMS2.world.scenes.SceneTest3d;

import java.awt.*;

public class Game {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {

            Toolkit toolkit = Toolkit.getDefaultToolkit();
            Dimension screenSize = toolkit.getScreenSize();
            int resWidth = screenSize.width;
            int resHeight = screenSize.height;
//            int resWidth = 2560;
//            int resHeight = 1440;

            World world = new World();
            //world.addTrackSegment(new StraightSegment("0001", 50)); // id + length

            //world.addVehicle(new PesaSwing("3120", "0001", 0)); // id + current track id + pos on track

            //ScenePanel scene1 = new Scene1(world, 800, 600);

            ScenePanel sceneTest3d = new SceneTest3d(world, resWidth, resHeight);

            GamePanel panel = new GamePanel(resWidth, resHeight);
            panel.setScene(sceneTest3d);
            panel.setVisible(true);
        });
    }
}
