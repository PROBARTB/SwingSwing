package EALiodufiowAMS2.general.layouts;

import EALiodufiowAMS2.general.GameNavigation;
import EALiodufiowAMS2.general.LayoutContext;
import EALiodufiowAMS2.general.scene.scenes.GameplaySceneConfig;
import EALiodufiowAMS2.general.world.GameplayWorldConfig;

import javax.swing.*;
import java.awt.*;

public class StartGamePanel extends JPanel {

    public StartGamePanel(LayoutContext layoutContext) {
        GameNavigation navigation = layoutContext.getNavigation();

        setLayout(new BorderLayout());

        JLabel label = new JLabel("Game configuration (placeholder)", SwingConstants.CENTER);
        add(label, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton backButton = new JButton("Back");
        JButton startButton = new JButton("Start");

        GameplaySceneConfig config = new GameplaySceneConfig(new GameplayWorldConfig());

        backButton.addActionListener(e -> navigation.returnToMainMenu());
        startButton.addActionListener(e -> navigation.startGameWithConfig(config));

        bottomPanel.add(backButton);
        bottomPanel.add(startButton);

        add(bottomPanel, BorderLayout.SOUTH);
    }
}
