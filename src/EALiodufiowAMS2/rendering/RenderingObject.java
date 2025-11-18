package EALiodufiowAMS2.rendering;

import EALiodufiowAMS2.helpers.Transform;

import java.awt.*;
import java.awt.image.BufferedImage;

public interface RenderingObject {
    Transform getTransform();
    String getType();
}
