package EALiodufiowAMS2.engine.uiRendering;

import java.util.Objects;

/**
 * @param width  null => preferredSize
 * @param height null => preferredSize
 */
public record UiOverlayConstraints(UiOverlayAnchor anchor, int offsetX, int offsetY, Integer width, Integer height,
                                   boolean visible) {

    public UiOverlayConstraints(UiOverlayAnchor anchor,
                                int offsetX,
                                int offsetY,
                                Integer width,
                                Integer height,
                                boolean visible) {
        this.anchor = Objects.requireNonNull(anchor, "anchor");
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.width = width;
        this.height = height;
        this.visible = visible;
    }

    public UiOverlayConstraints withVisibility(boolean visible) {
        return new UiOverlayConstraints(anchor, offsetX, offsetY, width, height, visible);
    }

    public UiOverlayConstraints withOffsets(int offsetX, int offsetY) {
        return new UiOverlayConstraints(anchor, offsetX, offsetY, width, height, visible);
    }

    public UiOverlayConstraints withSize(Integer width, Integer height) {
        return new UiOverlayConstraints(anchor, offsetX, offsetY, width, height, visible);
    }
}
