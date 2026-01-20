//package EALiodufiowAMS2.rendering.renderingObjects;
//
//import EALiodufiowAMS2.helpers.Transform;
//
//public class Rectangle implements RenderingObject {
//    private Transform transform;
//    private Surface surface;
//
//    public Rectangle(Transform transform, Surface surface) {
//        this.transform = transform;
//        this.surface = surface;
//    }
//    public Rectangle(Rectangle rectangle, Transform transform) {
//        this(transform, rectangle.getSurface());
//    }
//
//    @Override
//    public Transform getTransform() {
//        return transform;
//    }
//
//    @Override
//    public String getType() {
//        return "Rectangle";
//    }
//
//    public Surface getSurface() {
//        return this.surface;
//    }
//
//    public void setSurface(Surface surface) {
//        this.surface = surface;
//    }
//}
