package EALiodufiowAMS2.engine.uiRendering;

import java.util.Objects;

public final class UiOverlayConstraints {

    private final UiOverlayAnchor anchor;
    private final int offsetX;
    private final int offsetY;
    private final Integer width;   // null => preferredSize
    private final Integer height;  // null => preferredSize
    private final boolean visible;

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

    public UiOverlayAnchor getAnchor() {
        return anchor;
    }

    public int getOffsetX() {
        return offsetX;
    }

    public int getOffsetY() {
        return offsetY;
    }

    public Integer getWidth() {
        return width;
    }

    public Integer getHeight() {
        return height;
    }

    public boolean isVisible() {
        return visible;
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
