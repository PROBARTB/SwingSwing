package CWERTEL;

import javax.swing.*;
import java.awt.*;

public class TramPanel extends JPanel {
    private final MyTram tram;

    public TramPanel(MyTram tram) {
        this.tram = tram;
        setPreferredSize(new Dimension(800, 600));
        setBackground(new Color(240, 240, 240));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        tram.draw(g2);
        g2.dispose();
    }
}

