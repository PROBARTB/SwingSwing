package EALiodufiowAMS2.rendering;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Surface {
    private final FaceType type; // e.g. for cuboid: "front", "back", "left", "right", "top", "bottom"
    public BufferedImage texture;
    public Color color;

    public Surface(FaceType type, BufferedImage texture, Color color) {
        this.type = type;
        this.texture = texture;
        this.color = color;
    }

    public FaceType getType() { return type; }
}
