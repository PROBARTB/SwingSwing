package EALiodufiowAMS2.general.settings;


import EALiodufiowAMS2.engine.rendering.RenderingMode;

public final class Settings {

    private final GraphicsSettings graphics;
    private final GameplaySettings gameplay;

    public Settings(GraphicsSettings graphics, GameplaySettings gameplay) {
        this.graphics = graphics;
        this.gameplay = gameplay;
    }

    public GraphicsSettings getGraphics() {
        return graphics;
    }

    public GameplaySettings getGameplay() {
        return gameplay;
    }

    public Settings withGraphics(GraphicsSettings graphics) {
        return new Settings(graphics, gameplay);
    }

    public Settings withGameplay(GameplaySettings gameplay) {
        return new Settings(graphics, gameplay);
    }

    public static Settings defaultSettings() {
        return defaultSettings(1920, 1080);
    }

    public static Settings defaultSettings(int width, int height) {
        GraphicsSettings graphics = new GraphicsSettings(
                true,
                false,
                75,
                RenderingMode.GPU,
                width,
                height,
                false
        );

        GameplaySettings gameplay = new GameplaySettings(1.0);

        return new Settings(graphics, gameplay);
    }
}
