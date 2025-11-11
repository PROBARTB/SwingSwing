//package EALiodufiowAMS2.rendering;
//
//import EALiodufiowAMS2.helpers.Units;
//import EALiodufiowAMS2.helpers.Vec2;
//
//import java.awt.*;
//import java.awt.geom.Path2D;
//
///**
// * RenderingEngine w widoku z boku - KAMERA JAKO CENTRUM LOKALNE.
// *
// * System współrzędnych:
// * - Człon z kamerą ZAWSZE na środku ekranu w pozycji (0, 0) w logice
// * - Wszystkie obiekty transformowane względem kamery
// * - X na ekranie = X logiki (bez rotacji)
// * - Y na ekranie = -Z logiki (Z dodatni = wyżej na ekranie)
// *
// * Trapez sekcji:
// * - Lewa/prawa krawędź: zawsze PIONOWE (X stałe)
// * - Odstęp X między krawędziami: width * cos(angle) - efekt perspektywy przez obrót
// * - Wysokość krawędzi: zależy od Z (im większy Z, tym krótsza)
// * - Pozycja Y: Yekran = screenCenterY - scaleY * zRelative
// */
//public class RenderingEngine {
//    // Parametry perspektywy
//    private double perspectiveScaleY = 0.004; // skala wysokości na ekranie względem Z
//    private double perspectiveYShiftM = 0.12; // dodatkowe przesunięcie Y względem Z
//
//    // Kamera: pozycja w XZ świata
//    private Vec2 cameraPos = new Vec2(0, 0);
//    private Vec2 cameraDir = new Vec2(0, 0);
//
//    public void setPerspective(double scaleY, double yShiftMetersPerZMeter) {
//            this.perspectiveScaleY = scaleY;
//            this.perspectiveYShiftM = yShiftMetersPerZMeter;
//           // this.cameraDir= 0.0, 0.0;
//
//
//    }
//    public void setCamera(Vec2 pos, Vec2 dir) {
//        this.cameraPos = pos;
//        this.cameraDir = dir;
//    }
//
//    private Vec2 worldPosToCamera(Vec2 worldXZ) {
//            double dx = worldXZ.x - cameraPos.x;
//            double dz = worldXZ.z - cameraPos.z;
//
//            //double ca = Math.cos(-cameraAngle);
//            //double sa = Math.sin(-cameraAngle);
//            //double xCam = dx * ca - dz * sa;
//            //double zCam = dx * sa + dz * ca;
//
//            //return new Vec2(xCam, zCam);
//    }
//
//    // Projekcja: współrzędne kamery (X, Z) -> ekran (Xpx, Ypx)
//    public Point cameraToScreen(Vec2 cameraXZ, Dimension vp) {
//        // Xpx: X kamery bez zmian, centrowany
//        // Ypx: Y zależy od Z (perspektywa) - Z dodatni = wyżej
//            double zScale = 1.0 / (1.0 + perspectiveScaleY * Math.max(0.0, cameraXZ.z));
//        double yShiftPx = Units.mToPx(perspectiveYShiftM * Math.max(0.0, cameraXZ.z));
//
//        int cx = vp.width / 2, cy = vp.height / 2;
//        int xPx = cx + (int)Math.round(Units.mToPx(cameraXZ.x));
//        int yPx = cy - (int)Math.round(yShiftPx);
//        return new Point(xPx, yPx);
//    }
//
//    // Do wstecz: dla rasterowania
//    public Point railToScreen(Vec2 worldXZ, Dimension vp) {
//        Vec2 camXZ = worldPosToCamera(worldXZ);
//        return cameraToScreen(camXZ, vp);
//    }
//
//    public void drawTransformedRect(Graphics2D g2, Vec2 posWorld, Vec2 dirWorld, double lengthM, double heightM, Dimension vp, Color fillColor) {
//        // 1) licz wektory pozycji i kierunku względem kamery.
//        Vec2 posRel = worldPosToCamera(posWorld);
//       // Vec2 dirRel = dirWorldToCamera(dirWorld);
//
//        // 2) licz szerokość po transformacji -> zmniejszona przez obrót względem kamery
//        double transformedLength = lengthM * Math.max(0.01, Math.abs(dirRel.z));
//
//        // 3) określ pozycje końca i początku
//        Vec2 rectStart = new Vec2(posRel.x - (transformedLength / 2.0), posRel.z - (transformedLength / 2.0));
//        Vec2 rectEnd = new Vec2(posRel.x + (transformedLength / 2.0), posRel.z + (transformedLength / 2.0));
//
//        double halfW = transformedLength / 2.0;
//        Vec2 topLeft = new Vec2(rectEnd.x + nrmCam.x * halfW, nearCam.z + nrmCam.z * halfW);
//        Vec2 leftFarCam  = new Vec2(farCam.x  + nrmCam.x * halfW,  farCam.z  + nrmCam.z * halfW);
//        Vec2 rightNearCam= new Vec2(nearCam.x - nrmCam.x * halfW, nearCam.z - nrmCam.z * halfW);
//        Vec2 rightFarCam = new Vec2(farCam.x  - nrmCam.x * halfW,  farCam.z  - nrmCam.z * halfW);
//
//        // 4) projekcja na ekran
//        int cx = vp.width / 2, cy = vp.height / 2;
//
//        int leftXNear  = cx + (int)Math.round(Units.mToPx(leftNearCam.x));
//        int rightXNear = cx + (int)Math.round(Units.mToPx(rightNearCam.x));
//        int leftXFar   = cx + (int)Math.round(Units.mToPx(leftFarCam.x));
//        int rightXFar  = cx + (int)Math.round(Units.mToPx(rightFarCam.x));
//
//        int leftYNearBase  = cy - (int)Math.round(Units.mToPx(perspectiveYShiftM * Math.max(0.0, leftNearCam.z)));
//        int rightYNearBase = cy - (int)Math.round(Units.mToPx(perspectiveYShiftM * Math.max(0.0, rightNearCam.z)));
//        int leftYFarBase   = cy - (int)Math.round(Units.mToPx(perspectiveYShiftM * Math.max(0.0, leftFarCam.z)));
//        int rightYFarBase  = cy - (int)Math.round(Units.mToPx(perspectiveYShiftM * Math.max(0.0, rightFarCam.z)));
//
//        // 5) licz wysokość końca i początku -> skalowana przez Z i perspectiveScaleY
//        double scaleNear = 1.0 / (1.0 + perspectiveScaleY * Math.max(0.0, Math.max(leftNearCam.z, rightNearCam.z)));
//        double scaleFar  = 1.0 / (1.0 + perspectiveScaleY * Math.max(0.0, Math.max(leftFarCam.z, rightFarCam.z)));
//        int heightPxNear = (int)Math.round(Units.mToPx(heightM) * scaleNear);
//        int heightPxFar  = (int)Math.round(Units.mToPx(heightM) * scaleFar);
//
//        // 6) górne rogi -> dolne rogi Y - wysokość
//        int leftYNearTop  = leftYNearBase - heightPxNear;
//        int rightYNearTop = rightYNearBase - heightPxNear;
//        int leftYFarTop   = leftYFarBase - heightPxFar;
//        int rightYFarTop  = rightYFarBase - heightPxFar;
//
//        // 7) polygon (dolne rogi -> górne rogi)
//        Path2D poly = new Path2D.Double();
//        poly.moveTo(leftXNear, leftYNearBase);
//        poly.lineTo(rightXNear, rightYNearBase);
//        poly.lineTo(rightXFar, rightYFarBase);
//        poly.lineTo(leftXFar, leftYFarBase);
//        poly.closePath();
//
//        if (fillColor != null) {
//            g2.setColor(fillColor);
//            g2.fill(poly);
//        } else {
//            g2.setColor(Color.RED);
//            g2.fill(poly);
//        }
//
//        g2.setColor(Color.BLACK);
//        g2.setStroke(new BasicStroke(2.0f));
//        g2.draw(poly);
//    }
//
////    // Sekcja jako trapez w lokalnym układzie kamery
////    public void drawSection(Graphics2D g2, Vec2 centerWorld, double angleWorld, double lengthM, double widthM, double heightM, Dimension vp) {
////        Vec2 dirWorld = new Vec2(Math.cos(angleWorld), Math.sin(angleWorld));
////        drawTransformedRect(g2, centerWorld, dirWorld, lengthM, widthM, heightM, vp, Color.RED);
////    }
////
////    // Wózek jako prostokąt w tym samym modelu
////    public void drawBogie(Graphics2D g2, Vec2 centerWorld, double angleWorld, double lengthM, double widthM, double heightM, Dimension vp) {
////        Vec2 dirWorld = new Vec2(Math.cos(angleWorld), Math.sin(angleWorld));
////        drawTransformedRect(g2, centerWorld, dirWorld, lengthM, widthM, heightM, vp, Color.BLUE);
////    }
//}
