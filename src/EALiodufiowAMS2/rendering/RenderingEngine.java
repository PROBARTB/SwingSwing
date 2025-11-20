package EALiodufiowAMS2.rendering;

import EALiodufiowAMS2.helpers.Units;
import EALiodufiowAMS2.helpers.Vec3;
import EALiodufiowAMS2.world.Camera;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

public class RenderingEngine {

    // TODO: trzeba zrobić renderowanie linii dla torów. Copilot mówi, że z bezierem jest łatwo, bo po prostu rzutuje się punkty kontrolne.


    private List<RenderingObject> objects = new ArrayList<>();

    public void addObject(RenderingObject obj) {
        objects.add(obj);
    }

    public void clearObjects() {
        objects.clear();
    }

    public void update(Graphics2D g2) {
        for (RenderingObject obj : objects) {
            switch (obj.getType()) {
                case "cuboid":
                    Cuboid cuboid = (Cuboid) obj;
                    Vec3 posBottomCenter    = cuboid.getTransform().getPos();
                    Vec3 dir                = cuboid.getTransform().getDir();
                    Vec3 size               = cuboid.getTransform().getSize();
                    List<Surface> surfaces  = cuboid.getSurfaces();

                    drawCuboidRelativeToCamera(g2, posBottomCenter, dir, size, surfaces);
                    break;

                default:
                    System.out.printf("Unknown object type: %s%n", obj.getType());
                    break;
            }
        }
    }

    private double k = 2.0; // perspective coefficient in meters (effective focal distance)
    private Camera camera;

    public void setCamera(Camera cam) {
        this.camera = cam;
    }

    private Point project(double xm, double ym, double zm) {
        double f = 1.0 / (1.0 + (zm / k));

        double xScaled_m = xm * f;
        double yScaled_m = ym * f;

        int xp = (int) Math.round(xScaled_m * Units.M_TO_PX);
        int yp = (int) Math.round(yScaled_m * Units.M_TO_PX);

        return new Point(xp, yp);
    }

    public void drawPolygon(Graphics2D g2, Point[] pts, BufferedImage texture, Color color) {
        if (texture != null) {
            int columns = texture.getWidth();
            for (int i = 0; i < columns; i++) {
                double t = (double) i / (columns - 1);

                double xTop    = pts[0].x + t * (pts[1].x - pts[0].x);
                double yTop    = pts[0].y + t * (pts[1].y - pts[0].y);
                double xBottom = pts[3].x + t * (pts[2].x - pts[3].x);
                double yBottom = pts[3].y + t * (pts[2].y - pts[3].y);

                int dx1 = (int) Math.round(xTop);
                int dy1 = (int) Math.round(yTop);
                int dx2 = dx1 + 1;
                int dy2 = (int) Math.round(yBottom);

                int sx1 = i;
                int sy1 = 0;
                int sx2 = i + 1;
                int sy2 = texture.getHeight();

                g2.drawImage(texture, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, null);
            }
        } else {
            g2.setColor(color);
            g2.fillPolygon(
                    new int[]{pts[0].x, pts[1].x, pts[2].x, pts[3].x},
                    new int[]{pts[0].y, pts[1].y, pts[2].y, pts[3].y},
                    4
            );
        }

        // Optional outline for debugging
        g2.setColor(Color.RED);
        g2.drawPolygon(
                new int[]{pts[0].x, pts[1].x, pts[2].x, pts[3].x},
                new int[]{pts[0].y, pts[1].y, pts[2].y, pts[3].y},
                4
        );
    }

    public void drawRectangle3D(Graphics2D g2, Vec3 pos, Vec3 dir, Vec3 size,
                                BufferedImage texture, Color color) {

        Vec3 dirN = dir.normalize();

        double lenXZ = Math.sqrt(dirN.x*dirN.x + dirN.z*dirN.z);
        double ux = lenXZ > 0 ? dirN.x / lenXZ : 1.0;
        double uz = lenXZ > 0 ? dirN.z / lenXZ : 0.0;

        double vy = 1.0, vz = 0.0;

        double w_m = size.x; // meters
        double h_m = size.y; // meters

        double[][] corners_m = {
                {pos.x - (w_m/2)*ux, pos.y - (h_m/2)*vy, pos.z - (w_m/2)*uz}, // left-bottom
                {pos.x + (w_m/2)*ux, pos.y - (h_m/2)*vy, pos.z + (w_m/2)*uz}, // right-bottom
                {pos.x + (w_m/2)*ux, pos.y + (h_m/2)*vy, pos.z + (w_m/2)*uz}, // right-top
                {pos.x - (w_m/2)*ux, pos.y + (h_m/2)*vy, pos.z - (w_m/2)*uz}  // left-top
        };

        Point[] pts = new Point[4];
        for (int i = 0; i < 4; i++) {
            pts[i] = project(corners_m[i][0], corners_m[i][1], corners_m[i][2]);
        }

        drawPolygon(g2, pts, texture, color);
    }

    public void drawRectangleRelativeToCamera(Graphics2D g2, Vec3 worldPos, Vec3 worldDir, Vec3 size, BufferedImage texture, Color color) {
        if (camera == null) throw new IllegalStateException("Camera not set");

        Vec3 relativePos = worldPos.sub(camera.getPos());
        Vec3 relativeDir = worldDir.sub(camera.getDir());

        drawRectangle3D(g2, relativePos, relativeDir, size, texture, color);
    }

    public void drawCuboid(Graphics2D g2, Vec3 posBottomCenter, Vec3 dir, Vec3 size, List<Surface> surfaces) {
        double w = size.x;
        double h = size.y;
        double d = size.z;

        Vec3 forward = dir.normalize();
        Vec3 up = new Vec3(0, 1, 0);
        Vec3 right = forward.cross(up).normalize();
        up = right.cross(forward).normalize();
        Vec3 bottomCenter = posBottomCenter;
        Vec3 center = bottomCenter.add(up.scale(h / 2.0));

        for (Surface s : surfaces) {
            BufferedImage tex = s.texture;
            Color col = s.color;

            Vec3 faceCenter = null;
            Vec3 faceDir = null;
            Vec3 faceSize = null;

            // TRZEBA SPRAWDZIĆ CZY TE ŚCIANY TO DOBRZE SĄ POLICZONE !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            switch (s.getType()) {
                case "front":
                    faceCenter = center.add(forward.scale(d / 2.0));
                    faceDir = forward;
                    faceSize = new Vec3(w, h, 0);
                    break;
                case "back":
                    faceCenter = center.add(forward.scale(-d / 2.0));
                    faceDir = forward.scale(-1);
                    faceSize = new Vec3(w, h, 0);
                    break;
                case "left":
                    faceCenter = center.add(right.scale(-w / 2.0));
                    faceDir = right.scale(-1);
                    faceSize = new Vec3(d, h, 0);
                    break;
                case "right":
                    faceCenter = center.add(right.scale(w / 2.0));
                    faceDir = right;
                    faceSize = new Vec3(d, h, 0);
                    break;
                case "top":
                    faceCenter = center.add(up.scale(h / 2.0));
                    faceDir = up;
                    faceSize = new Vec3(w, d, 0);
                    break;
                case "bottom":
                    faceCenter = center.add(up.scale(-h / 2.0));
                    faceDir = up.scale(-1);
                    faceSize = new Vec3(w, d, 0);
                    break;
                default:
                    System.out.printf("Unknown Cuboid Surface type %s", s.getType());
                    continue;
            }

            drawRectangle3D(g2, faceCenter, faceDir, faceSize, tex, col);
        }
    }

    public void drawCuboidRelativeToCamera(Graphics2D g2, Vec3 worldPos, Vec3 worldDir, Vec3 size, List<Surface> surfaces) {
        if (camera == null) throw new IllegalStateException("Camera not set");

        Vec3 relativePos = worldPos.sub(camera.getPos());
        Vec3 relativeDir = worldDir.sub(camera.getDir());

        drawCuboid(g2, relativePos, relativeDir, size, surfaces);
    }
}
