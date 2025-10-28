import java.awt.*;

class PesaSwing extends Tram {
    public PesaSwing() {
        super(300, 120); // max speed, length
    }

    @Override
    public void draw(Graphics2D g) {
        g.setColor(Color.BLUE);
        g.fillRect(50, 200, length, 40); // fixed position on left
    }
}