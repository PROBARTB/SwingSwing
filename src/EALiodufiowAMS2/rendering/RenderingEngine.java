package EALiodufiowAMS2.rendering;

import EALiodufiowAMS2.helpers.Units;
import EALiodufiowAMS2.helpers.Vec3;
import EALiodufiowAMS2.world.Camera;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * RenderingEngine provides textured strip-based rendering and simple perspective projection.
 * All input positions and dimensions are in meters; conversion to pixels happens at the end.
 */
public class RenderingEngine {

    // TRZEBA DODAĆ ABY BYŁY TU TRZYMANE LISTA RENDERINGOBJECTS I METODĘ UPDATE KTÓRA JE RENDERUJE !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

    private double k = 2.0; // perspective coefficient in meters (effective focal distance)
    private Camera camera;

    /** Assign camera to be used as view origin. */
    public void setCamera(Camera cam) {
        this.camera = cam;
    }

    /**
     * Project a 3D point (meters) to 2D screen space (pixels).
     * Conversion from meters to pixels happens after perspective scaling.
     */
    private Point project(double xm, double ym, double zm) {
        // Perspective factor: f = 1 / (1 + z/k) where k is in meters
        double f = 1.0 / (1.0 + (zm / k));

        // Apply perspective scaling while still in meters
        double xScaled_m = xm * f;
        double yScaled_m = ym * f;

        // Convert to pixels at the end
        int xp = (int) Math.round(xScaled_m * Units.M_TO_PX);
        int yp = (int) Math.round(yScaled_m * Units.M_TO_PX);

        return new Point(xp, yp);
    }

    /**
     * Draws a convex quad polygon using either a texture (strip-mapped) or a flat color.
     * Points are in screen pixels.
     */
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

    /**
     * Draw a rectangle in 3D (meters), oriented by dir (normalized).
     * The rectangle lies in a vertical plane with height along Y and width along XZ plane given by dir.
     * pos: center of the rectangle (meters)
     * dir: normalized direction in XZ plane for width vector
     * size: (width, height, depthUnused) in meters; depthUnused is ignored
     */
    public void drawRectangle3D(Graphics2D g2, Vec3 pos, Vec3 dir, Vec3 size,
                                BufferedImage texture, Color color) {

        Vec3 dirN = dir.normalize();

        // Build width vector in XZ plane from dirN
        double lenXZ = Math.sqrt(dirN.x*dirN.x + dirN.z*dirN.z);
        // Protect against zero-length in XZ
        double ux = lenXZ > 0 ? dirN.x / lenXZ : 1.0;
        double uz = lenXZ > 0 ? dirN.z / lenXZ : 0.0;

        // Height vector is global Y axis (vertical face assumption)
        double vy = 1.0, vz = 0.0;

        double w_m = size.x; // meters
        double h_m = size.y; // meters

        // Compute 3D corners (meters)
        double[][] corners_m = {
                {pos.x - (w_m/2)*ux, pos.y - (h_m/2)*vy, pos.z - (w_m/2)*uz}, // left-bottom
                {pos.x + (w_m/2)*ux, pos.y - (h_m/2)*vy, pos.z + (w_m/2)*uz}, // right-bottom
                {pos.x + (w_m/2)*ux, pos.y + (h_m/2)*vy, pos.z + (w_m/2)*uz}, // right-top
                {pos.x - (w_m/2)*ux, pos.y + (h_m/2)*vy, pos.z - (w_m/2)*uz}  // left-top
        };

        // Project corners to pixels
        Point[] pts = new Point[4];
        for (int i = 0; i < 4; i++) {
            pts[i] = project(corners_m[i][0], corners_m[i][1], corners_m[i][2]);
        }

        drawPolygon(g2, pts, texture, color);
    }

    /**
     * Draw a rectangle relative to the camera. Inputs are world-space in meters.
     * Uses camera as view origin, converts to camera-relative position, and draws.
     */
    public void drawRectangleRelativeToCamera(Graphics2D g2, Vec3 worldPos, Vec3 worldDir, Vec3 size, BufferedImage texture, Color color) {
        if (camera == null) throw new IllegalStateException("Camera not set");

        Vec3 relativePos = worldPos.sub(camera.getPos());
        Vec3 relativeDir = worldDir.sub(camera.getDir());

        drawRectangle3D(g2, relativePos, relativeDir, size, texture, color);
    }

    /**
     * Draw a cuboid from surfaces relative to its center (meters).
     * dimensions: (width, height, depth) in meters
     * Each surface type maps to a face with its own center and orientation.
     */
    public void drawCuboid(Graphics2D g2, Vec3 posBottomCenter, Vec3 dir, Vec3 size, Surface[] surfaces) {
        double w = size.x;
        double h = size.y;
        double d = size.z;

        for (Surface s : surfaces) {
            BufferedImage tex = s.texture;
            Color col = s.color;

            Vec3 faceCenter = null;
            Vec3 faceDir = null;
            Vec3 faceSize = null;

            // TRZEBA SPRAWDZIĆ CZY TE ŚCIANY TO DOBRZE SĄ POLICZONE !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            // TRZEBA DODAĆ UŻYWANIE DIRECTION !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            switch (s.getType()) {
                case "front":
                    // Face at +Z side, width across XZ with dir along +X
                    faceCenter = new Vec3(posBottomCenter.x, posBottomCenter.y, posBottomCenter.z + d/2);
                    faceDir    = new Vec3(1, 0, 0).normalize();
                    faceSize   = new Vec3(w, h, 0);
                    break;
                case "back":
                    // Face at -Z side, width across XZ with dir along -X
                    faceCenter = new Vec3(posBottomCenter.x, posBottomCenter.y, posBottomCenter.z - d/2);
                    faceDir    = new Vec3(-1, 0, 0).normalize();
                    faceSize   = new Vec3(w, h, 0);
                    break;
                case "left":
                    // Face at -X side, width across XZ with dir along +Z
                    faceCenter = new Vec3(posBottomCenter.x - w/2, posBottomCenter.y, posBottomCenter.z);
                    faceDir    = new Vec3(0, 0, 1).normalize();
                    faceSize   = new Vec3(d, h, 0);
                    break;
                case "right":
                    // Face at +X side, width across XZ with dir along -Z
                    faceCenter = new Vec3(posBottomCenter.x + w/2, posBottomCenter.y, posBottomCenter.z);
                    faceDir    = new Vec3(0, 0, -1).normalize();
                    faceSize   = new Vec3(d, h, 0);
                    break;
                case "top":
                    // Top face: vertical plane assumption (height along Y),
                    // width along +X (could also be along Z depending on your needs)
                    faceCenter = new Vec3(posBottomCenter.x, posBottomCenter.y + h/2, posBottomCenter.z);
                    faceDir    = new Vec3(1, 0, 0).normalize();
                    faceSize   = new Vec3(w, d, 0); // showing top as w by d
                    break;
                case "bottom":
                    faceCenter = new Vec3(posBottomCenter.x, posBottomCenter.y - h/2, posBottomCenter.z);
                    faceDir    = new Vec3(1, 0, 0).normalize();
                    faceSize   = new Vec3(w, d, 0);
                    break;
                default:
                    System.out.printf("Unknown Cuboid Surface type %s", s.getType());
                    continue;
            }

            drawRectangle3D(g2, faceCenter, faceDir, faceSize, tex, col);
        }
    }

    /**
     * Draw a cuboid relative to camera. Inputs are world-space in meters.
     * Computes cuboid center relative to camera and delegates to drawCuboid.
     */
    public void drawCuboidRelativeToCamera(Graphics2D g2, Vec3 worldPos, Vec3 worldDir, Vec3 size, Surface[] surfaces) {
        if (camera == null) throw new IllegalStateException("Camera not set");

        Vec3 relativePos = worldPos.sub(camera.getPos());
        Vec3 relativeDir = worldDir.sub(camera.getDir());

        drawCuboid(g2, relativePos, relativeDir, size, surfaces);
    }
}
