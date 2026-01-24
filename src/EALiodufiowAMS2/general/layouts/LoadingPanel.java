package EALiodufiowAMS2.general.layouts;

import EALiodufiowAMS2.general.LayoutContext;

import javax.swing.*;
import java.awt.*;

public class LoadingPanel extends JPanel {

    private final JLabel messageLabel;

    public LoadingPanel(LayoutContext layoutContext) {
        setLayout(new BorderLayout());
        messageLabel = new JLabel("Loading...", SwingConstants.CENTER);
        add(messageLabel, BorderLayout.CENTER);
    }

    public void setMessage(String message) {
        messageLabel.setText(message);
    }
}
