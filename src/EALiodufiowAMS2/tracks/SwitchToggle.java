package EALiodufiowAMS2.tracks;

public class SwitchToggle {
    private final String targetSwitchId;
    private final TrackNetwork network;

    public SwitchToggle(String switchId, TrackNetwork net) {
        this.targetSwitchId = switchId;
        this.network = net;
    }

    // Metoda wywoływana np. przez Raycast po kliknięciu myszą
    public void interact() {
        network.switchToggle(targetSwitchId);
    }
}