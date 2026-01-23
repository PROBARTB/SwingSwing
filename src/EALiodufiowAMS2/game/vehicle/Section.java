package EALiodufiowAMS2.game.vehicle;

import EALiodufiowAMS2.helpers.Transform;
import EALiodufiowAMS2.helpers.Vec3;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Section {
    private Transform transform;
    private BufferedImage texture;
    private final List<Bogie> bogies = new ArrayList<>();

    public Section(Vec3 size, BufferedImage texture) {
        this.transform = new Transform(null, null, size);
        this.texture = texture;
    }

    public Transform getTransform() { return transform; }
    public void setTransform(Transform transform) { this.transform = transform; }

    public BufferedImage getTexture() { return texture; }
    public void setTexture(BufferedImage texture) { this.texture = texture; }

    public Bogie addBogie(double offsetFromStartMeters, Vec3 size, BufferedImage texture) {
        Bogie b = new Bogie(offsetFromStartMeters, size, texture);
        bogies.add(b);
        return b;
    }
    public List<Bogie> getBogies() { return bogies; }

}
