package EALiodufiowAMS2.rendering.renderingObject;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Material {
    private final Color color;
    private final BufferedImage texture;

    public Material(Color color, BufferedImage texture) {
        this.color = color;
        this.texture = texture;
    }
    public Material(Color color) {
        this.color = color;
        this.texture = null;
    }

    public Color getColor() { return color; }
    public BufferedImage getTexture() { return texture; }
}
