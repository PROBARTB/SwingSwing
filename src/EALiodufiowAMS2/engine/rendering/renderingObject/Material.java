package EALiodufiowAMS2.engine.rendering.renderingObject;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Material {
    private final Color color;
    private final BufferedImage texture;
    private final MaterialBlendMode blendMode;
    private final TextureMode textureMode;

    public Material(Color color, BufferedImage texture, TextureMode textureMode, MaterialBlendMode blendMode) {
        this.color = color;
        this.texture = texture;
        this.blendMode = blendMode;
        this.textureMode = textureMode;
    }
    public Material(Color color, BufferedImage texture, TextureMode textureMode) {
        this(color, texture, textureMode, MaterialBlendMode.TRANSPARENT);
    }
    public Material(Color color, BufferedImage texture, MaterialBlendMode blendMode) {
        this(color, texture, TextureMode.STRETCH, blendMode);
    }
    public Material(Color color, BufferedImage texture) {
        this(color, texture, MaterialBlendMode.TRANSPARENT);
    }
    public Material(Color color) {
        this(color, (BufferedImage) null);
    }
    public Material(Color color, MaterialBlendMode blendMode) {
        this(color, null, blendMode);
    }

    public Color getColor() { return color; }
    public BufferedImage getTexture() { return texture; }
    public MaterialBlendMode getBlendMode() { return blendMode; }
    public TextureMode getTextureMode() { return textureMode; }
}
