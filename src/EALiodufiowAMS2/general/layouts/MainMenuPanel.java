package EALiodufiowAMS2.general.layouts;

import EALiodufiowAMS2.general.GameNavigation;
import EALiodufiowAMS2.general.LayoutContext;

import javax.swing.*;
import java.awt.*;

public class MainMenuPanel extends JPanel {

    private GameNavigation navigation;

    public MainMenuPanel(LayoutContext layoutContext) {
        this.navigation = layoutContext.getNavigation();

        setLayout(new GridBagLayout());
        rebuildUi();
    }

    private void rebuildUi() {
        removeAll();

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        JButton startGameButton = new JButton("Start Game");
        startGameButton.addActionListener(e -> {
            if (navigation != null) navigation.startNewGameFlow();
        });

        JButton worldEditorButton = new JButton("World Editor");
        worldEditorButton.addActionListener(e -> {
            if (navigation != null) navigation.startEditorDefault();
        });

        JButton settingsButton = new JButton("Settings");
        settingsButton.addActionListener(e -> {
            if (navigation != null) navigation.openSettings();
        });

        JButton debugButton = new JButton("Load Debug Scene");
        debugButton.addActionListener(e -> {
            if (navigation != null) navigation.loadDebugScene();
        });

        JButton quitButton = new JButton("Quit");
        quitButton.addActionListener(e -> {
            if (navigation != null) navigation.quitGame();
        });

        gbc.gridy = 0;
        add(startGameButton, gbc);
        gbc.gridy++;
        add(worldEditorButton, gbc);
        gbc.gridy++;
        add(settingsButton, gbc);
        gbc.gridy++;
        add(debugButton, gbc);
        gbc.gridy++;
        add(quitButton, gbc);

        revalidate();
        repaint();
    }
}
