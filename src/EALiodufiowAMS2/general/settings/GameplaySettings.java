package EALiodufiowAMS2.general.settings;

public final class GameplaySettings {

    // Na razie placeholder – później np. poziom trudności, prędkość symulacji itd.
    private final double simulationSpeed;

    public GameplaySettings(double simulationSpeed) {
        this.simulationSpeed = simulationSpeed;
    }

    public double getSimulationSpeed() {
        return simulationSpeed;
    }

    public GameplaySettings withSimulationSpeed(double speed) {
        return new GameplaySettings(speed);
    }
}
