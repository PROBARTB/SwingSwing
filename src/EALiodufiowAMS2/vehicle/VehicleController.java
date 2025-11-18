package EALiodufiowAMS2.vehicle;

import java.awt.event.*;
import java.util.*;

public class VehicleController implements KeyListener {
    private RailVehicle attachedVehicle;

    public void attachTo(RailVehicle vehicle) {
        this.attachedVehicle = vehicle;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (attachedVehicle == null) return;
        Map<Integer, Runnable> bindings = attachedVehicle.getKeyBindings();
        Runnable action = bindings.get(e.getKeyCode());
        if (action != null) {
            action.run();
        }
    }

    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}
}
