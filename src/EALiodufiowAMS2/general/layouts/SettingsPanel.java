package EALiodufiowAMS2.general.layouts;

import EALiodufiowAMS2.engine.rendering.RenderingMode;
import EALiodufiowAMS2.general.LayoutContext;
import EALiodufiowAMS2.general.settings.*;

import javax.swing.*;
import java.awt.*;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class SettingsPanel extends JPanel {

    private final SettingsManager settingsManager;

    private final GraphicsSettingsPanel graphicsPanel;
    private final GameplaySettingsPanel gameplayPanel;
    private final UiSettingsPanel uiPanel;

    public SettingsPanel(LayoutContext layoutContext) {
        this.settingsManager = layoutContext.getSettingsManager();
        setLayout(new BorderLayout());

        JTabbedPane tabs = new JTabbedPane();

        this.graphicsPanel = new GraphicsSettingsPanel();
        this.gameplayPanel = new GameplaySettingsPanel();
        this.uiPanel = new UiSettingsPanel();

        tabs.addTab("Graphics", graphicsPanel);
        tabs.addTab("Gameplay", gameplayPanel);
        tabs.addTab("UI", uiPanel);

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
        uiPanel.loadFrom(s.getUi());
    }

    private void applyChanges() {
        Settings current = settingsManager.getCurrent();
        GraphicsSettings newGraphics = graphicsPanel.buildGraphicsSettings(current.getGraphics());
        GameplaySettings newGameplay = gameplayPanel.buildGameplaySettings(current.getGameplay());
        UiSettings newUi = uiPanel.buildUiSettings(current.getUi());
        Settings updated = current
                .withGraphics(newGraphics)
                .withGameplay(newGameplay)
                .withUi(newUi);
        settingsManager.update(updated);
    }

    // --- Graphics tab ---

    private static class GraphicsSettingsPanel extends JPanel {

        // FOV
        private JSlider fovSlider;

        // Rendering
        private JComboBox<RenderingMode> renderingModeCombo;
        private JComboBox<WindowMode> windowModeCombo;
        private JSpinner fpsLimitSpinner;

        // Window resolution
        private JRadioButton windowAutoRadio;
        private JRadioButton windowFixedRadio;
        private JTextField windowWidthField;
        private JTextField windowHeightField;
        private JComboBox<String> windowPresetCombo;

        // Render resolution
        private JRadioButton renderAutoRadio;
        private JRadioButton renderFixedRadio;
        private JTextField renderWidthField;
        private JTextField renderHeightField;
        private JComboBox<String> renderPresetCombo;

        // Quality
        private JComboBox<String> curveQualityCombo;
        private JComboBox<String> secondaryQualityCombo;

        public GraphicsSettingsPanel() {
            setLayout(new BorderLayout(8, 8));
            setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

            JPanel main = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.weightx = 1.0;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(4, 4, 4, 4);

            // Rendering section
            JPanel renderingSection = createRenderingSection();
            main.add(renderingSection, gbc);

            // FOV section
            gbc.gridy++;
            JPanel fovSection = createFovSection();
            main.add(fovSection, gbc);

            // Window resolution section
            gbc.gridy++;
            JPanel windowResSection = createWindowResolutionSection();
            main.add(windowResSection, gbc);

            // Render resolution section
            gbc.gridy++;
            JPanel renderResSection = createRenderResolutionSection();
            main.add(renderResSection, gbc);

            // Quality section
            gbc.gridy++;
            JPanel qualitySection = createQualitySection();
            main.add(qualitySection, gbc);

            // filler
            gbc.gridy++;
            gbc.weighty = 1.0;
            gbc.fill = GridBagConstraints.BOTH;
            main.add(Box.createVerticalGlue(), gbc);

            add(main, BorderLayout.CENTER);
        }

        private JPanel createRenderingSection() {
            JPanel panel = new JPanel(new GridBagLayout());
            panel.setBorder(new TitledBorder("Rendering"));

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.insets = new Insets(4, 4, 4, 4);
            gbc.anchor = GridBagConstraints.WEST;

            renderingModeCombo = new JComboBox<>(RenderingMode.values());
            windowModeCombo = new JComboBox<>(WindowMode.values());
            fpsLimitSpinner = new JSpinner(new SpinnerNumberModel(60, 15, 240, 1));

            panel.add(new JLabel("Rendering mode:"), gbc);
            gbc.gridx = 1;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            panel.add(renderingModeCombo, gbc);

            gbc.gridx = 0;
            gbc.gridy++;
            gbc.fill = GridBagConstraints.NONE;
            panel.add(new JLabel("Window mode:"), gbc);
            gbc.gridx = 1;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            panel.add(windowModeCombo, gbc);

            gbc.gridx = 0;
            gbc.gridy++;
            gbc.fill = GridBagConstraints.NONE;
            panel.add(new JLabel("FPS limit:"), gbc);
            gbc.gridx = 1;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            panel.add(fpsLimitSpinner, gbc);

            return panel;
        }

        private JPanel createFovSection() {
            JPanel panel = new JPanel(new GridBagLayout());
            panel.setBorder(new TitledBorder("Field of view"));

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.insets = new Insets(4, 4, 4, 4);
            gbc.anchor = GridBagConstraints.WEST;

            fovSlider = new JSlider(30, 120, 75);
            fovSlider.setPaintTicks(true);
            fovSlider.setPaintLabels(true);
            fovSlider.setMajorTickSpacing(15);
            fovSlider.setMinorTickSpacing(5);

            panel.add(new JLabel("FOV:"), gbc);
            gbc.gridx = 1;
            gbc.weightx = 1.0;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            panel.add(fovSlider, gbc);

            return panel;
        }

        private JPanel createWindowResolutionSection() {
            JPanel panel = new JPanel(new GridBagLayout());
            panel.setBorder(new TitledBorder("Window resolution"));

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(4, 4, 4, 4);
            gbc.anchor = GridBagConstraints.WEST;

            windowAutoRadio = new JRadioButton("Auto");
            windowFixedRadio = new JRadioButton("Fixed");
            ButtonGroup group = new ButtonGroup();
            group.add(windowAutoRadio);
            group.add(windowFixedRadio);

            windowWidthField = new JTextField(6);
            windowHeightField = new JTextField(6);
            windowPresetCombo = new JComboBox<>(new String[]{
                    "HD 1280x720",
                    "FHD 1920x1080",
                    "QHD 2560x1440"
            });

            // row: auto / fixed
            gbc.gridx = 0;
            gbc.gridy = 0;
            panel.add(windowAutoRadio, gbc);
            gbc.gridx = 1;
            panel.add(windowFixedRadio, gbc);

            // row: width x height
            gbc.gridx = 0;
            gbc.gridy++;
            panel.add(new JLabel("Size:"), gbc);
            gbc.gridx = 1;
            JPanel sizePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
            sizePanel.add(windowWidthField);
            sizePanel.add(new JLabel("x"));
            sizePanel.add(windowHeightField);
            panel.add(sizePanel, gbc);

            // row: presets
            gbc.gridx = 0;
            gbc.gridy++;
            panel.add(new JLabel("Preset:"), gbc);
            gbc.gridx = 1;
            panel.add(windowPresetCombo, gbc);

            // listeners
            windowAutoRadio.addActionListener(e -> updateWindowResolutionEnabled());
            windowFixedRadio.addActionListener(e -> updateWindowResolutionEnabled());
            windowPresetCombo.addActionListener(e -> applyWindowPreset());

            windowAutoRadio.setSelected(true);
            updateWindowResolutionEnabled();

            return panel;
        }

        private JPanel createRenderResolutionSection() {
            JPanel panel = new JPanel(new GridBagLayout());
            panel.setBorder(new TitledBorder("Render resolution"));

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(4, 4, 4, 4);
            gbc.anchor = GridBagConstraints.WEST;

            renderAutoRadio = new JRadioButton("Auto");
            renderFixedRadio = new JRadioButton("Fixed");
            ButtonGroup group = new ButtonGroup();
            group.add(renderAutoRadio);
            group.add(renderFixedRadio);

            renderWidthField = new JTextField(6);
            renderHeightField = new JTextField(6);
            renderPresetCombo = new JComboBox<>(new String[]{
                    "HD 1280x720",
                    "FHD 1920x1080",
                    "QHD 2560x1440"
            });

            // row: auto / fixed
            gbc.gridx = 0;
            gbc.gridy = 0;
            panel.add(renderAutoRadio, gbc);
            gbc.gridx = 1;
            panel.add(renderFixedRadio, gbc);

            // row: width x height
            gbc.gridx = 0;
            gbc.gridy++;
            panel.add(new JLabel("Size:"), gbc);
            gbc.gridx = 1;
            JPanel sizePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
            sizePanel.add(renderWidthField);
            sizePanel.add(new JLabel("x"));
            sizePanel.add(renderHeightField);
            panel.add(sizePanel, gbc);

            // row: presets
            gbc.gridx = 0;
            gbc.gridy++;
            panel.add(new JLabel("Preset:"), gbc);
            gbc.gridx = 1;
            panel.add(renderPresetCombo, gbc);

            // listeners
            renderAutoRadio.addActionListener(e -> updateRenderResolutionEnabled());
            renderFixedRadio.addActionListener(e -> updateRenderResolutionEnabled());
            renderPresetCombo.addActionListener(e -> applyRenderPreset());

            renderAutoRadio.setSelected(true);
            updateRenderResolutionEnabled();

            return panel;
        }

        private JPanel createQualitySection() {
            JPanel panel = new JPanel(new GridBagLayout());
            panel.setBorder(new TitledBorder("Quality"));

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(4, 4, 4, 4);
            gbc.anchor = GridBagConstraints.WEST;

            curveQualityCombo = new JComboBox<>(new String[]{"Low", "Medium", "High"});
            secondaryQualityCombo = new JComboBox<>(new String[]{"Low", "Medium", "High"});

            gbc.gridx = 0;
            gbc.gridy = 0;
            panel.add(new JLabel("Curve quality:"), gbc);
            gbc.gridx = 1;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            panel.add(curveQualityCombo, gbc);

            gbc.gridx = 0;
            gbc.gridy++;
            gbc.fill = GridBagConstraints.NONE;
            panel.add(new JLabel("Secondary scene quality:"), gbc);
            gbc.gridx = 1;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            panel.add(secondaryQualityCombo, gbc);

            return panel;
        }

        private void updateWindowResolutionEnabled() {
            boolean fixed = windowFixedRadio.isSelected();
            windowWidthField.setEnabled(fixed);
            windowHeightField.setEnabled(fixed);
            windowPresetCombo.setEnabled(fixed);
        }

        private void updateRenderResolutionEnabled() {
            boolean fixed = renderFixedRadio.isSelected();
            renderWidthField.setEnabled(fixed);
            renderHeightField.setEnabled(fixed);
            renderPresetCombo.setEnabled(fixed);
        }

        private void applyWindowPreset() {
            String preset = (String) windowPresetCombo.getSelectedItem();
            if (preset == null) return;
            int[] wh = parsePreset(preset);
            if (wh != null) {
                windowWidthField.setText(String.valueOf(wh[0]));
                windowHeightField.setText(String.valueOf(wh[1]));
            }
        }

        private void applyRenderPreset() {
            String preset = (String) renderPresetCombo.getSelectedItem();
            if (preset == null) return;
            int[] wh = parsePreset(preset);
            if (wh != null) {
                renderWidthField.setText(String.valueOf(wh[0]));
                renderHeightField.setText(String.valueOf(wh[1]));
            }
        }

        private int[] parsePreset(String preset) {
            // e.g. "FHD 1920x1080"
            int idx = preset.lastIndexOf(' ');
            if (idx < 0) return null;
            String res = preset.substring(idx + 1);
            String[] parts = res.split("x");
            if (parts.length != 2) return null;
            try {
                int w = Integer.parseInt(parts[0].trim());
                int h = Integer.parseInt(parts[1].trim());
                return new int[]{w, h};
            } catch (NumberFormatException e) {
                return null;
            }
        }

        private String qualityToPreset(double value) {
            // simple mapping by ranges
            if (value >= 0.8) return "High";
            if (value >= 0.4) return "Medium";
            return "Low";
        }

        private double presetToQuality(String preset) {
            if ("High".equals(preset)) return 1.0;
            if ("Medium".equals(preset)) return 0.6;
            return 0.3;
        }

        public void loadFrom(GraphicsSettings g) {
            // FOV
            fovSlider.setValue(g.getFov());

            // Rendering
            renderingModeCombo.setSelectedItem(g.getRenderingMode());
            windowModeCombo.setSelectedItem(g.getWindowMode());
            fpsLimitSpinner.setValue(g.getFpsLimit());

            // Window resolution
            if (g.getWindowResolutionMode() == ResolutionMode.AUTO) {
                windowAutoRadio.setSelected(true);
            } else {
                windowFixedRadio.setSelected(true);
            }
            windowWidthField.setText(String.valueOf(g.getWindowWidth()));
            windowHeightField.setText(String.valueOf(g.getWindowHeight()));
            selectPresetClosest(windowPresetCombo, g.getWindowWidth(), g.getWindowHeight());
            updateWindowResolutionEnabled();

            // Render resolution
            if (g.getRenderResolutionMode() == ResolutionMode.AUTO) {
                renderAutoRadio.setSelected(true);
            } else {
                renderFixedRadio.setSelected(true);
            }
            renderWidthField.setText(String.valueOf(g.getRenderWidth()));
            renderHeightField.setText(String.valueOf(g.getRenderHeight()));
            selectPresetClosest(renderPresetCombo, g.getRenderWidth(), g.getRenderHeight());
            updateRenderResolutionEnabled();

            // Quality
            curveQualityCombo.setSelectedItem(qualityToPreset(g.getCurveQuality()));
            secondaryQualityCombo.setSelectedItem(qualityToPreset(g.getSecondarySceneQuality()));
        }

        private void selectPresetClosest(JComboBox<String> combo, int w, int h) {
            String best = null;
            int bestDiff = Integer.MAX_VALUE;
            for (int i = 0; i < combo.getItemCount(); i++) {
                String item = combo.getItemAt(i);
                int[] wh = parsePreset(item);
                if (wh == null) continue;
                int diff = Math.abs(wh[0] - w) + Math.abs(wh[1] - h);
                if (diff < bestDiff) {
                    bestDiff = diff;
                    best = item;
                }
            }
            if (best != null) {
                combo.setSelectedItem(best);
            }
        }

        public GraphicsSettings buildGraphicsSettings(GraphicsSettings base) {
            int fov = fovSlider.getValue();
            RenderingMode mode = (RenderingMode) renderingModeCombo.getSelectedItem();
            WindowMode windowMode = (WindowMode) windowModeCombo.getSelectedItem();
            int fpsLimit = (Integer) fpsLimitSpinner.getValue();

            // Window resolution
            ResolutionMode windowResMode = windowAutoRadio.isSelected()
                    ? ResolutionMode.AUTO
                    : ResolutionMode.FIXED;
            int windowWidth = base.getWindowWidth();
            int windowHeight = base.getWindowHeight();
            if (windowResMode == ResolutionMode.FIXED) {
                try {
                    windowWidth = Integer.parseInt(windowWidthField.getText().trim());
                    windowHeight = Integer.parseInt(windowHeightField.getText().trim());
                } catch (NumberFormatException ignored) {
                    // fallback to base if invalid
                    windowWidth = base.getWindowWidth();
                    windowHeight = base.getWindowHeight();
                }
            }

            // Render resolution
            ResolutionMode renderResMode = renderAutoRadio.isSelected()
                    ? ResolutionMode.AUTO
                    : ResolutionMode.FIXED;
            int renderWidth = base.getRenderWidth();
            int renderHeight = base.getRenderHeight();
            if (renderResMode == ResolutionMode.FIXED) {
                try {
                    renderWidth = Integer.parseInt(renderWidthField.getText().trim());
                    renderHeight = Integer.parseInt(renderHeightField.getText().trim());
                } catch (NumberFormatException ignored) {
                    renderWidth = base.getRenderWidth();
                    renderHeight = base.getRenderHeight();
                }
            }

            double curveQuality = presetToQuality((String) curveQualityCombo.getSelectedItem());
            double secondaryQuality = presetToQuality((String) secondaryQualityCombo.getSelectedItem());

            return new GraphicsSettings(
                    fov,
                    mode,
                    windowWidth,
                    windowHeight,
                    windowResMode,
                    renderWidth,
                    renderHeight,
                    renderResMode,
                    windowMode,
                    fpsLimit,
                    curveQuality,
                    secondaryQuality
            );
        }
    }

    // --- Gameplay tab ---

    private static class GameplaySettingsPanel extends JPanel {

        private JSpinner simulationSpeedSpinner;

        public GameplaySettingsPanel() {
            setLayout(new BorderLayout(8, 8));
            setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

            JPanel main = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.insets = new Insets(4, 4, 4, 4);
            gbc.anchor = GridBagConstraints.WEST;

            JPanel gameplaySection = new JPanel(new GridBagLayout());
            gameplaySection.setBorder(new TitledBorder("Gameplay"));

            GridBagConstraints g2 = new GridBagConstraints();
            g2.gridx = 0;
            g2.gridy = 0;
            g2.insets = new Insets(4, 4, 4, 4);
            g2.anchor = GridBagConstraints.WEST;

            simulationSpeedSpinner = new JSpinner(new SpinnerNumberModel(1.0, 0.1, 5.0, 0.1));

            gameplaySection.add(new JLabel("Simulation speed:"), g2);
            g2.gridx = 1;
            g2.fill = GridBagConstraints.HORIZONTAL;
            gameplaySection.add(simulationSpeedSpinner, g2);

            main.add(gameplaySection, gbc);

            gbc.gridy++;
            gbc.weighty = 1.0;
            gbc.fill = GridBagConstraints.BOTH;
            main.add(Box.createVerticalGlue(), gbc);

            add(main, BorderLayout.CENTER);
        }

        public void loadFrom(GameplaySettings gameplay) {
            simulationSpeedSpinner.setValue(gameplay.getSimulationSpeed());
        }

        public GameplaySettings buildGameplaySettings(GameplaySettings base) {
            double speed = (Double) simulationSpeedSpinner.getValue();
            return base.withSimulationSpeed(speed);
        }
    }

    // --- UI tab ---

    private static class UiSettingsPanel extends JPanel {

        private JCheckBox showFpsCheck;
        private JCheckBox showDebugInfoCheck;

        public UiSettingsPanel() {
            setLayout(new BorderLayout(8, 8));
            setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

            JPanel main = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.insets = new Insets(4, 4, 4, 4);
            gbc.anchor = GridBagConstraints.WEST;

            JPanel uiSection = new JPanel(new GridBagLayout());
            uiSection.setBorder(new TitledBorder("UI"));

            GridBagConstraints g2 = new GridBagConstraints();
            g2.gridx = 0;
            g2.gridy = 0;
            g2.insets = new Insets(4, 4, 4, 4);
            g2.anchor = GridBagConstraints.WEST;

            showFpsCheck = new JCheckBox("Show FPS");
            showDebugInfoCheck = new JCheckBox("Show debug info");

            uiSection.add(showFpsCheck, g2);
            g2.gridy++;
            uiSection.add(showDebugInfoCheck, g2);

            main.add(uiSection, gbc);

            gbc.gridy++;
            gbc.weighty = 1.0;
            gbc.fill = GridBagConstraints.BOTH;
            main.add(Box.createVerticalGlue(), gbc);

            add(main, BorderLayout.CENTER);
        }

        public void loadFrom(UiSettings ui) {
            showFpsCheck.setSelected(ui.isShowFps());
            showDebugInfoCheck.setSelected(ui.isShowDebugInfo());
        }

        public UiSettings buildUiSettings(UiSettings base) {
            boolean showFps = showFpsCheck.isSelected();
            boolean showDebug = showDebugInfoCheck.isSelected();
            return new UiSettings(showFps, showDebug);
        }
    }
}
