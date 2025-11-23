package EALiodufiowAMS2.rendering;

import EALiodufiowAMS2.helpers.*;
import EALiodufiowAMS2.rendering.renderingObjects.Cuboid;
import EALiodufiowAMS2.rendering.renderingObjects.RenderingObject;
import EALiodufiowAMS2.world.Camera;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

public class RenderingEngine {

    // TODO: trzeba zrobić renderowanie linii dla torów. Copilot mówi, że z bezierem jest łatwo, bo po prostu rzutuje się punkty kontrolne.

    /* TODO globalne:
        ! naprawianie RenderingEngine
        Vec3 metody nadpisują, a Matrix3 zwracają nowe i niespójne jest -> zmienić Vec3 i zaktualizować gdzie używane
        VehicleRenderer,
        przepisać cały system torów: segmenty + renderer,
        ✅ Game,
        ✅ Scene,
        ✅ World,
        zaimplementować actions do testów + PlayerVechicleController,
        T E S T O W A Ć,
        przygotować tekstury 120Na,
        zaimplementować SwitchSegment z działającym przełączaniem,
        drzwi w 120Na,
        różne kontrolery w Tram i co za tym idzie w 120NA (czuwak, drzwi, dzwonek, stacyjka),
        przystanki,
        skrzyżowania,
        poruszające się tło + inne obiekty dekoracyjne przy torach,

    */

    private List<RenderingObject> objects = new ArrayList<>();

    public void addObject(RenderingObject obj) {
        objects.add(obj);
    }

    public void setObjects(List<RenderingObject> objects) {
        this.objects = objects;
    }

    public void clearObjects() {
        objects.clear();
    }

    public void update(Graphics2D g2) {
        for (RenderingObject obj : objects) {

            // DEBUG
            System.out.println("RenderingEngine update: object " + obj.getTransform().toString());
            //

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

    private Camera camera;

    public void setCamera(Camera cam) {
        this.camera = cam;
    }

    public Transform getTransformRelativeToCamera(Transform transform) {
        if (camera == null) throw new IllegalStateException("Camera not set");

        Transform camt = new Transform(camera.getTransform());
        Transform t = new Transform(transform);

        t.getPos().sub(camt.getPos());
        //t.getPos().add(camt.getSize().scale(0.5));

        t.getDir().sub(camt.getDir());

        return t;
    }

    private Point project(double xm, double ym, double zm) {
        // Środek ekranu
        int cx = (int) Math.round((this.camera.getTransform().getSize().x * Units.M_TO_PX) / 2);
        int cy = (int) Math.round((this.camera.getTransform().getSize().y * Units.M_TO_PX) / 2);

        // Punkt musi być przed kamerą
        if (zm <= 0) return null;

        double k = camera.getK();
        System.out.println("k " + k);

        // Projekcja perspektywiczna
        double xProj = (xm * k) / zm;
        double yProj = (ym * k) / zm;

        // Skalowanie i przesunięcie
        int xp = (int) Math.round(xProj * Units.M_TO_PX) + cx;
        int yp = (int) Math.round(-yProj * Units.M_TO_PX) + cy;

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

    public void drawOrientedRectangle(Graphics2D g2, Vec3 posCenter, Matrix3 rot, double width, double height, BufferedImage texture, Color color) {
        System.out.println("drawOrientedRectangle:");
        System.out.println("  posCenter = " + posCenter);
        System.out.println("  width = " + width + ", height = " + height);
        System.out.println("  rotation matrix:");
        System.out.printf("    [%.3f %.3f %.3f]\n", rot.m00, rot.m01, rot.m02);
        System.out.printf("    [%.3f %.3f %.3f]\n", rot.m10, rot.m11, rot.m12);
        System.out.printf("    [%.3f %.3f %.3f]\n", rot.m20, rot.m21, rot.m22);

        Vec3[] cornersLocal = new Vec3[] {
                new Vec3(-width/2.0, -height/2.0, 0.0),
                new Vec3(+width/2.0, -height/2.0, 0.0),
                new Vec3(+width/2.0, +height/2.0, 0.0),
                new Vec3(-width/2.0, +height/2.0, 0.0)
        };

        Point[] pts = new Point[4];
        for (int i = 0; i < 4; i++) {
            Vec3 local = cornersLocal[i];
            Vec3 world = rot.multiply(local).add(posCenter);
            pts[i] = project(world.x, world.y, world.z);

            System.out.printf("  corner %d local: (%.3f, %.3f, %.3f)\n", i, local.x, local.y, local.z);
            System.out.printf("  corner %d world: (%.3f, %.3f, %.3f)\n", i, world.x, world.y, world.z);
            if (pts[i] != null) System.out.printf("  corner %d projected: (%d, %d)\n", i, pts[i].x, pts[i].y);
            else System.out.printf("  corner %d NOT projected - behind camera\n", i);

            if(pts[i] == null) return;
        }

        drawPolygon(g2, pts, texture, color);
    }


    public void drawRectangle3D(Graphics2D g2, Vec3 posCenter, Vec3 dir, Vec3 size, BufferedImage texture, Color color) {
        Matrix3 R = Matrix3.fromEuler(dir.x, dir.y, dir.z);

        drawOrientedRectangle(g2, posCenter, R, size.x, size.y, texture, color);
    }


    public void drawRectangleRelativeToCamera(Graphics2D g2, Vec3 worldPos, Vec3 worldDir, Vec3 size, BufferedImage texture, Color color) {
        if (camera == null) throw new IllegalStateException("Camera not set");

        Transform relativeTransform = getTransformRelativeToCamera( new Transform(worldPos, worldDir, size));

        drawRectangle3D(g2, relativeTransform.getPos(), relativeTransform.getDir(), relativeTransform.getSize(), texture, color);
    }

    /*
        CUBOID
     */
    private class FaceResult {
        Vec3 position;
        Matrix3 rotation;
        double width;
        double height;
        FaceResult(Vec3 p, Matrix3 r, double w, double h) { position = p; rotation = r; width = w; height = h; }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("FaceResult {\n");
            sb.append("  position: ").append(position).append("\n");
            sb.append("  rotation:\n");
            sb.append(String.format("    [%.3f %.3f %.3f]\n", rotation.m00, rotation.m01, rotation.m02));
            sb.append(String.format("    [%.3f %.3f %.3f]\n", rotation.m10, rotation.m11, rotation.m12));
            sb.append(String.format("    [%.3f %.3f %.3f]\n", rotation.m20, rotation.m21, rotation.m22));
            sb.append("  width: ").append(String.format("%.3f", width)).append("\n");
            sb.append("  height: ").append(String.format("%.3f", height)).append("\n");
            sb.append("}");
            return sb.toString();
        }
    }

    private FaceResult computeFace(Vec3 posBottomCenter, Vec3 size, Matrix3 globalRot, FaceType type) {
        Vec3 localOffset;
        Matrix3 localRot;
        double width, height;

        switch (type) {
            case FRONT:
                localOffset = new Vec3(0, size.y/2, -size.z/2);
                localRot = Matrix3.identity();
                width = size.x; height = size.y;
                break;
            case BACK:
                localOffset = new Vec3(0, size.y/2, size.z/2);
                localRot = Matrix3.rotY(Math.PI);
                width = size.x; height = size.y;
                break;
            case TOP:
                localOffset = new Vec3(0, size.y, 0);
                localRot = Matrix3.rotX(-Math.PI/2);
                width = size.x; height = size.z;
                break;
            case BOTTOM:
                localOffset = new Vec3(0, 0, 0);
                localRot = Matrix3.rotX(Math.PI/2);
                width = size.x; height = size.z;
                break;
            case RIGHT:
                localOffset = new Vec3(size.x/2, size.y/2, 0);
                localRot = Matrix3.rotY(Math.PI/2);
                width = size.z; height = size.y;
                break;
            case LEFT:
                localOffset = new Vec3(-size.x/2, size.y/2, 0);
                localRot = Matrix3.rotY(-Math.PI/2);
                width = size.z; height = size.y;
                break;
            default:
                throw new IllegalArgumentException("Unknown face type");
        }

        Vec3 worldOffset = globalRot.multiply(localOffset);
        Vec3 position = new Vec3(
                posBottomCenter.x + worldOffset.x,
                posBottomCenter.y + worldOffset.y,
                posBottomCenter.z + worldOffset.z
        );

        Matrix3 faceRot = globalRot.multiply(localRot);

        return new FaceResult(position, faceRot, width, height);
    }

    public void drawCuboid(Graphics2D g2, Vec3 posBottomCenter, Vec3 dir, Vec3 size, List<Surface> surfaces) {
        Matrix3 globalRot = Matrix3.fromEuler(dir.x, dir.y, dir.z);

        for (Surface s : surfaces) {
            BufferedImage tex = s.texture;
            Color col = s.color;

            FaceResult face = computeFace(posBottomCenter, size, globalRot, s.getType());

            // DEBUG
            //System.out.println("drawCuboid surface " + s.getType() + ": " + face.toString());
            System.out.println("drawCuboid surface " + s.getType());
            //

            drawOrientedRectangle(g2, face.position, face.rotation, face.width, face.height, tex, col);
        }
    }

    public void drawCuboidRelativeToCamera(Graphics2D g2, Vec3 worldPos, Vec3 worldDir, Vec3 size, List<Surface> surfaces) {
        if (camera == null) throw new IllegalStateException("Camera not set");

        Transform relativeTransform = getTransformRelativeToCamera( new Transform(worldPos, worldDir, size));

        // DEBUG
        System.out.println("drawCuboidRelativeToCamera: relativeTransform " + relativeTransform.toString());
        //

        drawCuboid(g2, relativeTransform.getPos(), relativeTransform.getDir(), relativeTransform.getSize(), surfaces);

    }
}
