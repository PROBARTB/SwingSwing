package EALiodufiowAMS2.general;

//import EALiodufiowAMS2.game.tracks.StraightSegment;
//import EALiodufiowAMS2.game.trams.PesaSwing;
import EALiodufiowAMS2.general.settings.Settings;
import EALiodufiowAMS2.general.settings.SettingsManager;

import javax.swing.*;
import java.awt.*;

//public class Game {
//    public static void main(String[] args) {
//        javax.swing.SwingUtilities.invokeLater(() -> {
//
//            Toolkit toolkit = Toolkit.getDefaultToolkit();
//            Dimension screenSize = toolkit.getScreenSize();
//            int resWidth = screenSize.width;
//            int resHeight = screenSize.height;
////            int resWidth = 2560;
////            int resHeight = 1440;
//
//            World world = new World();
//            //world.addTrackSegment(new StraightSegment("0001", 50)); // id + length
//
//            //world.addVehicle(new PesaSwing("3120", "0001", 0)); // id + current track id + pos on track
//
//            //ScenePanel scene1 = new Scene1(world, 800, 600);
//
//            ScenePanel sceneTest3d = new SceneTest3d(world, resWidth, resHeight);
//
//            GamePanel panel = new GamePanel(resWidth, resHeight);
//            panel.setScene(sceneTest3d);
//            panel.setVisible(true);
//        });
//    }
//}

public class Game {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {

            Toolkit toolkit = Toolkit.getDefaultToolkit();
            Dimension screenSize = toolkit.getScreenSize();
            int resWidth = screenSize.width;
            int resHeight = screenSize.height;

            SettingsManager settingsManager = new SettingsManager(Settings.defaultSettings(resWidth, resHeight));

            GameController gameController = new GameController(settingsManager);

            JFrame frame = new JFrame("SwingSwing");
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

            WindowController windowController = new WindowController(frame);
            settingsManager.addListener(windowController);

            frame.setLayout(new BorderLayout());
            frame.add(gameController.getPanel(), BorderLayout.CENTER);
            frame.setSize(
                    settingsManager.getCurrent().getGraphics().getWindowWidth(),
                    settingsManager.getCurrent().getGraphics().getWindowHeight()
            );
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            gameController.returnToMainMenu();
        });
    }

//    //funkcja ruchająca monitor swing by stasiocudak
//    private void enterFullscreen(int width, int height) {
//        GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
//        if (device.isFullScreenSupported()) {
//            device.setFullScreenWindow(this);
//            DisplayMode target = new DisplayMode(width, height, 32, DisplayMode.REFRESH_RATE_UNKNOWN);
//            try {
//                device.setDisplayMode(target);
//            } catch (Exception e) {
//                System.out.println("Changing resolution failed. Monitor may not support 1280x720.");
//            }
//        } else {
//            System.out.println("Fullscreen not supported — falling back to windowed mode.");
//            setSize(width, height);
//            setLocationRelativeTo(null);
//        }
//    }
}

