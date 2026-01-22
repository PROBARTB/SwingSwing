package EALiodufiowAMS2.uiRendering;

public class UiLabelObject implements UiObject {
    private final String id;
    private String text;
    private final UiAnchor anchor;
    private final int marginX;
    private final int marginY;
    private boolean visible = true;

    public UiLabelObject(String id, String text, UiAnchor anchor, int marginX, int marginY) {
        this.id = id;
        this.text = text;
        this.anchor = anchor;
        this.marginX = marginX;
        this.marginY = marginY;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public void setText(String text) {
        this.text = text;
    }

    @Override
    public UiAnchor getAnchor() {
        return anchor;
    }

    @Override
    public int getMarginX() {
        return marginX;
    }

    @Override
    public int getMarginY() {
        return marginY;
    }

    @Override
    public boolean isVisible() {
        return visible;
    }

    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
