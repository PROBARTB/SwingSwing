package EALiodufiowAMS2.general.settings;


import EALiodufiowAMS2.engine.rendering.RenderingMode;

public final class Settings {

    private final GraphicsSettings graphics;
    private final GameplaySettings gameplay;
    private final UiSettings ui;

    public Settings(GraphicsSettings graphics, GameplaySettings gameplay, UiSettings ui) {
        this.graphics = graphics;
        this.gameplay = gameplay;
        this.ui = ui;
    }

    public GraphicsSettings getGraphics() {
        return graphics;
    }

    public GameplaySettings getGameplay() {
        return gameplay;
    }

    public UiSettings getUi() {
        return ui;
    }


    public Settings withGraphics(GraphicsSettings graphics) {
        return new Settings(graphics, gameplay, ui);
    }

    public Settings withGameplay(GameplaySettings gameplay) {
        return new Settings(graphics, gameplay, ui);
    }

    public Settings withUi(UiSettings ui) {
        return new Settings(graphics, gameplay, ui);
    }

    public static Settings defaultSettings() {
        return defaultSettings(1920, 1080);
    }

    public static Settings defaultSettings(int width, int height) {
        GraphicsSettings graphics = new GraphicsSettings(
            75,
            RenderingMode.GPU,
            width,
            height,
            ResolutionMode.AUTO,
            width,
            height,
            ResolutionMode.AUTO,
            WindowMode.WINDOWED,
            60,
            1.0,
            0.5
        );

        GameplaySettings gameplay = new GameplaySettings(1.0);

        UiSettings ui = new UiSettings(true, false);

        return new Settings(graphics, gameplay, ui);
    }
}
