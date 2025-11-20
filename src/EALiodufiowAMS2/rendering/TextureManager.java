package EALiodufiowAMS2.rendering;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

public class TextureManager {
    private static final Map<String, BufferedImage> cache = new HashMap<>();

    public static BufferedImage getTexture(String path) {
        return cache.computeIfAbsent(path, p -> {
            try {
                return ImageIO.read(new File(p));
            } catch (IOException e) {
                throw new RuntimeException("Failed to load texture: " + p, e);
            }
        });
    }
}
