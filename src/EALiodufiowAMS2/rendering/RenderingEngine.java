package EALiodufiowAMS2.rendering;

import EALiodufiowAMS2.helpers.*;
import EALiodufiowAMS2.rendering.renderingObjects.*;
import EALiodufiowAMS2.rendering.renderingObjects.Rectangle;
import EALiodufiowAMS2.world.Camera;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

public class RenderingEngine {

    // TODO: trzeba zrobić renderowanie linii dla torów. Copilot mówi, że z bezierem jest łatwo, bo po prostu rzutuje się punkty kontrolne.

    /* TODO globalne:
        ! naprawianie RenderingEngine
        !!!!!!!!!!!!!!!!! zmainić w transform aby nie używać eulerów tylko Matrix3 wszędzie dla obrotu. Ustawianie i toString może być eulerami ale dane matrix !!!!!!!!! To powinno naprawić odbijanie się lustrzane kostki przy obrocie https://copilot.microsoft.com/shares/eQ9T6bP2rMjyRVkj8Sf5V
        Vec3 metody nadpisują, a Matrix3 zwracają nowe i niespójne jest -> zmienić Vec3 i zaktualizować gdzie używane
        przepisać cały system torów: segmenty + renderer,
        VehicleRenderer,
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

    /** RenderingObjects list */
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

    /** Rasterizer with Buffers */
    private final Rasterizer rasterizer;

    public void clear() {
        rasterizer.clearBuffers(0xFF4D5861); // backgroundColor
    }
    public BufferedImage getFrameBuffer() {
        return rasterizer.getFrameBuffer();
    }
    public void setBufferSize(int w, int h) {
        rasterizer.setBufferSize(w, h);
    }

    /** RenderingEngine */
    public RenderingEngine(int bufferWidth, int bufferHeight) {
        this.rasterizer = new Rasterizer(bufferWidth, bufferHeight);
    }

    public void update(Graphics2D g2) {
        System.out.println("RenderingEngine update");
        clear(); // clear buffer (may not be necessary, idk)
        for (RenderingObject obj : objects) {


            // DEBUG
            System.out.println("RenderingEngine update: object " + obj.getTransform().toString());
            //

            switch (obj.getType()) {
                case "Cuboid":
                    Cuboid cuboid = (Cuboid) obj;
                    drawCuboidRelativeToCamera(cuboid);
                    break;
                case "Rectangle":
                    Rectangle rectangle = (Rectangle) obj;
                    drawRectangle3DRelativeToCamera(rectangle);
                    break;
                case "StraightLine":
                    StraightLine straightLine = (StraightLine) obj;
                    drawStraightLine3DRelativeToCamera(straightLine);
                    break;
                case "CurvedLine":
                    CurvedLine curvedLine = (CurvedLine) obj;
                    drawCurvedLine3DRelativeToCamera(curvedLine);
                    break;
                default:
                    System.out.printf("Unknown object type: %s%n", obj.getType());
                    break;
            }
        }
    }

    /** Camera */
    private Camera camera;

    public void setCamera(Camera cam) {
        this.camera = cam;
    }

    public Transform getTransformRelativeToCamera(Transform transform) {
        if (camera == null) throw new IllegalStateException("Camera not set");

        Transform camt = camera.getTransform();

        Vec3 relPos = transform.getPos().sub(camt.getPos());

        // Zastosuj odwrotną rotację kamery do pozycji
        Quaternion camRotInv = camt.getRot().normalize().conjugate(); // dla unit quaternion odwrotność = sprzężenie
        relPos = relPos.rotate(camRotInv);

        Quaternion objRot = transform.getRot().normalize();
        Quaternion relRot = camRotInv.multiply(objRot);

        Transform result = new Transform(relPos, relRot, transform.getSize());

        return result;
    }
    public Vec3 getPosRelativeToCamera(Vec3 worldPos) {
        if (camera == null) throw new IllegalStateException("Camera not set");

        Transform camt = camera.getTransform();

        // Pozycja względem kamery
        Vec3 relPos = worldPos.sub(camt.getPos());

        // Zastosuj odwrotną rotację kamery
        Quaternion camRotInv = camt.getRot().normalize().conjugate();
        relPos = relPos.rotate(camRotInv);

        return relPos;
    }


    /** Projection from 3D to 2D */
    private Point project(double xm, double ym, double zm) {
        // camera center
        int cx = (int) Math.round((this.camera.getTransform().getSize().x * Units.M_TO_PX) / 2);
        int cy = (int) Math.round((this.camera.getTransform().getSize().y * Units.M_TO_PX) / 2);

        // not behind camera
        if (zm <= 0) return null;

        double k = camera.getK();

        // perspective projection
        double xProj = (xm * k) / zm;
        double yProj = (ym * k) / zm;

        // scaling and offsetting
        int xp = (int) Math.round(xProj * Units.M_TO_PX) + cx;
        int yp = (int) Math.round(-yProj * Units.M_TO_PX) + cy;

        return new Point(xp, yp);
    }

    /** Rectangle */
    // uses Swing Graphics2D g2 to render a polygon without respecting Z, which causes overlapping. Use Rasterizer.drawPolygon() instead.
    public void drawPolygonSwing(Graphics2D g2, Point[] pts, BufferedImage texture, Color color) {
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

    public void drawOrientedRectangle(Vec3 posCenter, Matrix3 rot, double width, double height, BufferedImage texture, Color color) {
//        System.out.println("drawOrientedRectangle:");
//        System.out.println("  posCenter = " + posCenter);
//        System.out.println("  width = " + width + ", height = " + height);
//        System.out.println("  rotation matrix:");
//        System.out.printf("    [%.3f %.3f %.3f]\n", rot.m00, rot.m01, rot.m02);
//        System.out.printf("    [%.3f %.3f %.3f]\n", rot.m10, rot.m11, rot.m12);
//        System.out.printf("    [%.3f %.3f %.3f]\n", rot.m20, rot.m21, rot.m22);

        Vec3[] cornersLocal = new Vec3[] {
                new Vec3(-width/2.0, -height/2.0, 0.0),
                new Vec3(+width/2.0, -height/2.0, 0.0),
                new Vec3(+width/2.0, +height/2.0, 0.0),
                new Vec3(-width/2.0, +height/2.0, 0.0)
        };

        Point[] pts = new Point[4];
        double[] ptsDepths = new double[4];
        for (int i = 0; i < 4; i++) {
            Vec3 local = cornersLocal[i];
            Vec3 global = rot.multiply(local).add(posCenter);
            pts[i] = project(global.x, global.y, global.z);
            ptsDepths[i] = global.z;

//            System.out.printf("  corner %d local: (%.3f, %.3f, %.3f)\n", i, local.x, local.y, local.z);
//            System.out.printf("  corner %d global: (%.3f, %.3f, %.3f)\n", i, global.x, global.y, global.z);
//            if (pts[i] != null) System.out.printf("  corner %d projected: (%d, %d)\n", i, pts[i].x, pts[i].y);
//            else System.out.printf("  corner %d NOT projected - behind camera\n", i);

            if(pts[i] == null) return;
        }

        //drawPolygonSwing(g2, pts, texture, color); //<--- deprecated
        rasterizer.drawPolygon(pts, ptsDepths, texture, color);
    }


    public void drawRectangle3D(Rectangle rectangle) {
        Transform t = rectangle.getTransform();
        Surface s = rectangle.getSurface();

        Vec3 posCenter = t.getPos().add(new Vec3(0, t.getSize().y/2, 0));

        drawOrientedRectangle(posCenter, t.getRot().toMatrix3(), t.getSize().x, t.getSize().y, s.texture, s.color);
    }
    public void drawRectangle3DRelativeToCamera(Rectangle rectangle) {
        Transform relativeTransform = getTransformRelativeToCamera(rectangle.getTransform());

        drawRectangle3D(new Rectangle(rectangle, relativeTransform));
    }


    /** CUBOID */
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
                localOffset = new Vec3(0, size.y / 2, -size.z / 2);
                localRot = Matrix3.rotY(Math.PI);
                width = size.x; height = size.y;
                break;
            case BACK:
                localOffset = new Vec3(0, size.y / 2, size.z / 2);
                localRot = Matrix3.identity();
                width = size.x; height = size.y;
                break;
            case TOP:
                localOffset = new Vec3(0, size.y, 0);
                localRot = Matrix3.rotX(-Math.PI / 2);
                width = size.x; height = size.z;
                break;
            case BOTTOM:
                localOffset = new Vec3(0, 0, 0);
                localRot = Matrix3.rotX(Math.PI / 2);
                width = size.x; height = size.z;
                break;
            case RIGHT:
                localOffset = new Vec3(size.x / 2, size.y / 2, 0);
                localRot = Matrix3.rotY(Math.PI / 2);
                width = size.z; height = size.y;
                break;
            case LEFT:
                localOffset = new Vec3(-size.x / 2, size.y / 2, 0);
                localRot = Matrix3.rotY(-Math.PI / 2);
                width = size.z; height = size.y;
                break;
            default:
                throw new IllegalArgumentException("Unknown face type");
        }

        Vec3 worldOffset = globalRot.multiply(localOffset);
        Vec3 position = posBottomCenter.add(worldOffset);
        Matrix3 faceRot = globalRot.multiply(localRot);

        return new FaceResult(position, faceRot, width, height);
    }


    public void drawCuboid(Cuboid cuboid) {
        Vec3 posBottomCenter = cuboid.getTransform().getPos();
        Vec3 size = cuboid.getTransform().getSize();
        Matrix3 globalRot = cuboid.getTransform().getRot().toMatrix3();

        for (Surface s : cuboid.getSurfaces()) {

            FaceResult face = computeFace(posBottomCenter, size, globalRot, s.getType());

            // DEBUG
            //System.out.println("drawCuboid surface " + s.getType() + ": " + face.toString());
            //System.out.println("drawCuboid surface " + s.getType());
            //

            drawOrientedRectangle(face.position, face.rotation, face.width, face.height, s.texture, s.color);
        }
    }
    public void drawCuboidRelativeToCamera(Cuboid cuboid) {
        Transform relativeTransform = getTransformRelativeToCamera(cuboid.getTransform());

        // DEBUG
        System.out.println("drawCuboidRelativeToCamera: relativeTransform " + relativeTransform.toString());
        //

        drawCuboid(new Cuboid(cuboid, relativeTransform));

    }


    /** LINES */

    // Funkcja pomocnicza: kierunek uchwytu z rotacji
    private Vec3 computeHandleDirection(Quaternion rot) {
        // lokalna oś X jako kierunek uchwytu
        return new Vec3(1,0,0).rotate(rot).normalize();
    }

    // Funkcja pomocnicza: liczenie punktów kontrolnych Béziera
    private Vec3[] computeControlPoints(CurvedLine line) {
        Vec3 p0 = line.getStartPosition();
        Vec3 p3 = line.getEndPosition();

        Vec3 dirStart = computeHandleDirection(line.getStartRotation());
        Vec3 dirEnd   = computeHandleDirection(line.getEndRotation());

        // p1 wychodzi od p0 wzdłuż dirStart
        Vec3 p1 = p0.add(dirStart.scale(line.getStartHandleLength() * line.getCurvatureFactor()));

        // p2 wchodzi do p3 wzdłuż dirEnd (odejmujemy, żeby kierunek był zgodny)
        Vec3 p2 = p3.sub(dirEnd.scale(line.getEndHandleLength() * line.getCurvatureFactor()));

        return new Vec3[]{p0, p1, p2, p3};
    }

    // Prosta linia
    public void drawStraightLine3DRelativeToCamera(StraightLine line) {
        // transformacja do kamery
        Vec3 startCam = getPosRelativeToCamera(line.getStartPos());
        Vec3 endCam   = getPosRelativeToCamera(line.getEndPos());

        // projekcja na ekran
        Point startScreen = project(startCam.x, startCam.y, startCam.z);
        Point endScreen   = project(endCam.x, endCam.y, endCam.z);

        rasterizer.drawStraightLine(startScreen, startCam.z,
                endScreen, endCam.z,
                line.getColor());
    }

    private final int CURVE_DETAIL = 32;

//    public void drawCurvedLine3DRelativeToCamera(CurvedLine line) {
//        Vec3[] ctrl = computeControlPoints(line);
//
//        Vec3 p0Cam = getPosRelativeToCamera(ctrl[0]);
//        Vec3 p1Cam = getPosRelativeToCamera(ctrl[1]);
//        Vec3 p2Cam = getPosRelativeToCamera(ctrl[2]);
//        Vec3 p3Cam = getPosRelativeToCamera(ctrl[3]);
//
//        Point p0Screen = project(p0Cam.x, p0Cam.y, p0Cam.z);
//        Point p1Screen = project(p1Cam.x, p1Cam.y, p1Cam.z);
//        Point p2Screen = project(p2Cam.x, p2Cam.y, p2Cam.z);
//        Point p3Screen = project(p3Cam.x, p3Cam.y, p3Cam.z);
//
//        rasterizer.drawBezierCurve(p0Screen, p0Cam.z, p1Screen, p1Cam.z, p2Screen, p2Cam.z, p3Screen, p3Cam.z, line.getColor(), CURVE_DETAIL);
//    }
public void drawCurvedLine3DRelativeToCamera(CurvedLine line) {
    Vec3[] ctrlWorld = computeControlPoints(line);

    // do układu kamery
    Vec3 p0Cam = getPosRelativeToCamera(ctrlWorld[0]);
    Vec3 p1Cam = getPosRelativeToCamera(ctrlWorld[1]);
    Vec3 p2Cam = getPosRelativeToCamera(ctrlWorld[2]);
    Vec3 p3Cam = getPosRelativeToCamera(ctrlWorld[3]);

    // rysuj forward differencing z projektowaniem próbek
    drawBezierCurveFD(p0Cam, p1Cam, p2Cam, p3Cam, line.getColor(), CURVE_DETAIL);
}



    // Rysowanie krzywej Béziera przez forward differencing,
// na wejściu kontrolne punkty w PRZESTRZENI KAMERY (x,y,z).
    public void drawBezierCurveFD(Vec3 p0Cam, Vec3 p1Cam, Vec3 p2Cam, Vec3 p3Cam,
                                  Color color, int steps) {
        if (steps < 2) steps = 2;

        // współczynniki wielomianu (osie liczymy niezależnie)
        double ax = -p0Cam.x + 3*p1Cam.x - 3*p2Cam.x + p3Cam.x;
        double bx =  3*p0Cam.x - 6*p1Cam.x + 3*p2Cam.x;
        double cx = -3*p0Cam.x + 3*p1Cam.x;
        double dx =  p0Cam.x;

        double ay = -p0Cam.y + 3*p1Cam.y - 3*p2Cam.y + p3Cam.y;
        double by =  3*p0Cam.y - 6*p1Cam.y + 3*p2Cam.y;
        double cy = -3*p0Cam.y + 3*p1Cam.y;
        double dy =  p0Cam.y;

        double az = -p0Cam.z + 3*p1Cam.z - 3*p2Cam.z + p3Cam.z;
        double bz =  3*p0Cam.z - 6*p1Cam.z + 3*p2Cam.z;
        double cz = -3*p0Cam.z + 3*p1Cam.z;
        double dz =  p0Cam.z;

        double h  = 1.0 / steps;
        double h2 = h * h;
        double h3 = h2 * h;

        // wartości początkowe
        double x = dx, y = dy, z = dz;

        // różnice (delta1, delta2, delta3)
        double dx1 = ax*h3 + bx*h2 + cx*h;
        double dy1 = ay*h3 + by*h2 + cy*h;
        double dz1 = az*h3 + bz*h2 + cz*h;

        double dx2 = 6*ax*h3 + 2*bx*h2;
        double dy2 = 6*ay*h3 + 2*by*h2;
        double dz2 = 6*az*h3 + 2*bz*h2;

        double dx3 = 6*ax*h3;
        double dy3 = 6*ay*h3;
        double dz3 = 6*az*h3;

        // poprzedni widoczny punkt (ekran) i jego z w kamerze
        Point prevScreen = null;
        double prevZCam = 0.0;

        for (int i = 0; i <= steps; i++) {
            // projektuj próbkę (zgodnie z near-plane: projekt() zwróci null gdy z<=0)
            Point screen = project(x, y, z);

            if (screen != null) {
                if (prevScreen != null) {
                    // narysuj odcinek między poprzednią a bieżącą próbką
                    // interpolując Z po przestrzeni kamery (liniowo względem parametru kroku)
                    rasterizer.drawStraightLine(prevScreen, prevZCam, screen, z, color);
                }
                prevScreen = screen;
                prevZCam   = z;
            } else {
                // bieżąca próbka niewidoczna → przerwij ciągłość
                prevScreen = null;
            }

            // aktualizacja różnic
            x += dx1; dx1 += dx2; dx2 += dx3;
            y += dy1; dy1 += dy2; dy2 += dy3;
            z += dz1; dz1 += dz2; dz2 += dz3;
        }
    }




}
