package EALiodufiowAMS2.general.layouts;

import EALiodufiowAMS2.engine.rendering.RenderingMode;
import EALiodufiowAMS2.general.LayoutContext;
import EALiodufiowAMS2.general.settings.GameplaySettings;
import EALiodufiowAMS2.general.settings.GraphicsSettings;
import EALiodufiowAMS2.general.settings.Settings;
import EALiodufiowAMS2.general.settings.SettingsManager;

import javax.swing.*;
import java.awt.*;

public class SettingsPanel extends JPanel {

    private final SettingsManager settingsManager;

    private final GraphicsSettingsPanel graphicsPanel;
    private final GameplaySettingsPanel gameplayPanel;

    public SettingsPanel(LayoutContext layoutContext) {
        this.settingsManager = layoutContext.getSettingsManager();
        setLayout(new BorderLayout());

        JTabbedPane tabs = new JTabbedPane();

        this.graphicsPanel = new GraphicsSettingsPanel();
        this.gameplayPanel = new GameplaySettingsPanel();

        tabs.addTab("Graphics", graphicsPanel);
        tabs.addTab("Gameplay", gameplayPanel);

        add(tabs, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton applyButton = new JButton("Apply");
        JButton cancelButton = new JButton("Cancel");
        JButton resetButton = new JButton("Reset");

        applyButton.addActionListener(e -> applyChanges());
        cancelButton.addActionListener(e -> layoutContext.getNavigation().returnToMainMenu());
        resetButton.addActionListener(e -> reloadFromSettings());

        bottom.add(cancelButton);
        bottom.add(resetButton);
        bottom.add(applyButton);

        add(bottom, BorderLayout.SOUTH);

        reloadFromSettings();
    }

    public void reloadFromSettings() {
        Settings s = settingsManager.getCurrent();
        graphicsPanel.loadFrom(s.getGraphics());
        gameplayPanel.loadFrom(s.getGameplay());
    }

    private void applyChanges() {
        Settings current = settingsManager.getCurrent();
        GraphicsSettings newGraphics = graphicsPanel.buildGraphicsSettings(current.getGraphics());
        GameplaySettings newGameplay = gameplayPanel.buildGameplaySettings(current.getGameplay());
        Settings updated = current.withGraphics(newGraphics).withGameplay(newGameplay);
        settingsManager.update(updated);
    }

    // --- Graphics tab ---

    private static class GraphicsSettingsPanel extends JPanel {

        private JCheckBox showFpsCheck;
        private JCheckBox showDebugInfoCheck;
        private JSpinner fovSpinner;
        private JComboBox<RenderingMode> renderingModeCombo;
        private JComboBox<String> resolutionPresetCombo;
        private JCheckBox fullscreenCheck;

        public GraphicsSettingsPanel() {
            setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.insets = new Insets(4, 4, 4, 4);
            gbc.anchor = GridBagConstraints.WEST;

            showFpsCheck = new JCheckBox("Show FPS");
            showDebugInfoCheck = new JCheckBox("Show Debug Info");
            fovSpinner = new JSpinner(new SpinnerNumberModel(75, 30, 120, 1));
            renderingModeCombo = new JComboBox<>(RenderingMode.values());
            resolutionPresetCombo = new JComboBox<>(new String[] { "HD 1280x720", "FHD 1920x1080", "QHD 2560x1440" });
            fullscreenCheck = new JCheckBox("Fullscreen");

            add(new JLabel("Rendering mode:"), gbc);
            gbc.gridx = 1;
            add(renderingModeCombo, gbc);

            gbc.gridx = 0;
            gbc.gridy++;
            add(new JLabel("FOV:"), gbc);
            gbc.gridx = 1;
            add(fovSpinner, gbc);

            gbc.gridx = 0;
            gbc.gridy++;
            add(new JLabel("Resolution:"), gbc);
            gbc.gridx = 1;
            add(resolutionPresetCombo, gbc);

            gbc.gridx = 0;
            gbc.gridy++;
            add(showFpsCheck, gbc);
            gbc.gridx = 1;
            add(showDebugInfoCheck, gbc);

            gbc.gridx = 0;
            gbc.gridy++;
            add(new JLabel("Fullscreen:"), gbc);
            gbc.gridx = 1;
            add(fullscreenCheck, gbc);

            gbc.gridx = 0;
            gbc.gridy++;
            gbc.gridwidth = 2;
            gbc.weighty = 1.0;
            gbc.fill = GridBagConstraints.VERTICAL;
            add(Box.createVerticalGlue(), gbc);
        }

        public void loadFrom(GraphicsSettings graphics) {
            showFpsCheck.setSelected(graphics.isShowFps());
            fovSpinner.setValue(graphics.getFov());
            renderingModeCombo.setSelectedItem(graphics.getRenderingMode());
            fullscreenCheck.setSelected(graphics.isFullscreen());

            String preset = "FHD 1920x1080";
            if (graphics.getResolutionWidth() == 1280 && graphics.getResolutionHeight() == 720) {
                preset = "HD 1280x720";
            } else if (graphics.getResolutionWidth() == 2560 && graphics.getResolutionHeight() == 1440) {
                preset = "QHD 2560x1440";
            }
            resolutionPresetCombo.setSelectedItem(preset);
        }

        public GraphicsSettings buildGraphicsSettings(GraphicsSettings base) {
            boolean showFps = showFpsCheck.isSelected();
            boolean showDebugInfo = showDebugInfoCheck.isSelected();
            int fov = (Integer) fovSpinner.getValue();
            RenderingMode mode = (RenderingMode) renderingModeCombo.getSelectedItem();
            boolean fullscreen = fullscreenCheck.isSelected();

            int width = base.getResolutionWidth();
            int height = base.getResolutionHeight();
            String preset = (String) resolutionPresetCombo.getSelectedItem();
            if ("HD 1280x720".equals(preset)) {
                width = 1280;
                height = 720;
            } else if ("FHD 1920x1080".equals(preset)) {
                width = 1920;
                height = 1080;
            } else if ("QHD 2560x1440".equals(preset)) {
                width = 2560;
                height = 1440;
            }

            return new GraphicsSettings(showFps, showDebugInfo, fov, mode, width, height, fullscreen);
        }
    }

    // --- Gameplay tab ---

    private static class GameplaySettingsPanel extends JPanel {

        private JSpinner simulationSpeedSpinner;

        public GameplaySettingsPanel() {
            setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.insets = new Insets(4, 4, 4, 4);
            gbc.anchor = GridBagConstraints.WEST;

            simulationSpeedSpinner = new JSpinner(new SpinnerNumberModel(1.0, 0.1, 5.0, 0.1));

            add(new JLabel("Simulation speed:"), gbc);
            gbc.gridx = 1;
            add(simulationSpeedSpinner, gbc);

            gbc.gridx = 0;
            gbc.gridy++;
            gbc.gridwidth = 2;
            gbc.weighty = 1.0;
            gbc.fill = GridBagConstraints.VERTICAL;
            add(Box.createVerticalGlue(), gbc);
        }

        public void loadFrom(GameplaySettings gameplay) {
            simulationSpeedSpinner.setValue(gameplay.getSimulationSpeed());
        }

        public GameplaySettings buildGameplaySettings(GameplaySettings base) {
            double speed = (Double) simulationSpeedSpinner.getValue();
            return base.withSimulationSpeed(speed);
        }
    }
}
