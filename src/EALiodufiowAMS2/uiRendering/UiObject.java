package EALiodufiowAMS2.uiRendering;

public interface UiObject {
    String getId();

    String getText();
    void setText(String text);

    UiAnchor getAnchor();
    int getMarginX();
    int getMarginY();

    boolean isVisible();
    void setVisible(boolean visible);
}
