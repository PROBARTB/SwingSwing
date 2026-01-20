//package EALiodufiowAMS2.rendering.renderingObjects;
//
//import EALiodufiowAMS2.helpers.*;
//
//import java.awt.*;
//import java.util.*;
//import java.util.List;
//
//public class StraightLine implements Line {
//    private final Vec3 startPos;
//    private final Vec3 endPos;
//    private final Color color;
//
//    public StraightLine(Vec3 startPos, Vec3 endPos, Color color) {
//        this.startPos = startPos;
//        this.endPos = endPos;
//        this.color = color;
//    }
//
//    public Vec3 getStartPos() { return startPos; }
//    public Vec3 getEndPos() { return endPos; }
//
//    @Override
//    public Color getColor() { return color; }
//    @Override
//    public Transform getTransform() { return new Transform(startPos, null, null); }
//    @Override
//    public String getType() { return "StraightLine"; }
//}
