package EALiodufiowAMS2.general.components;

import javax.swing.*;
import java.awt.*;

public class GameplayPauseMenu extends JPanel {

    public GameplayPauseMenu(Runnable onResume,
                             Runnable onSettings,
                             Runnable onMainMenu) {
        setOpaque(true);
        setBackground(new Color(0, 0, 0, 200));
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        JButton resumeButton = new JButton("Resume");
        resumeButton.addActionListener(e -> onResume.run());

        JButton settingsButton = new JButton("Settings");
        settingsButton.addActionListener(e -> onSettings.run());

        JButton mainMenuButton = new JButton("Main Menu");
        mainMenuButton.addActionListener(e -> onMainMenu.run());

        gbc.gridy = 0;
        add(resumeButton, gbc);
        gbc.gridy++;
        add(settingsButton, gbc);
        gbc.gridy++;
        add(mainMenuButton, gbc);
    }
}
