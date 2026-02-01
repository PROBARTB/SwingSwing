package EALiodufiowAMS2.engine.rendering.graphicsRenderers.cpu;

import EALiodufiowAMS2.engine.rendering.renderingObject.Material;
import EALiodufiowAMS2.engine.rendering.renderingObject.TextureMode;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.Arrays;

/**
 * Rasterizer:
 * - Frame buffer (BufferedImage)
 * - Z-buffer
 * - Rasteryzacja trójkątów z teksturowaniem lub jednolitym kolorem
 * - Alpha blending dla przezroczystych tekstur
 */
public class Rasterizer {
    private int bufferWidth;
    private int bufferHeight;
    private BufferedImage frameBuffer;
    private double[] zBuffer;

    public Rasterizer(BufferedImage buffer) {
        setFrameBuffer(buffer);
    }

    public int getBufferWidth() { return bufferWidth; }
    public int getBufferHeight() { return bufferHeight; }
    public BufferedImage getFrameBuffer() { return frameBuffer; }

//    public void setBufferSize(int newWidth, int newHeight) {
//        if (newWidth <= 0 || newHeight <= 0) {
//            throw new IllegalArgumentException("Buffer size must be positive");
//        }
//        this.bufferWidth = newWidth;
//        this.bufferHeight = newHeight;
//        this.frameBuffer = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
//        this.zBuffer = new double[newWidth * newHeight];
//        clearBuffers(0xff000000);
//    }

    public void setFrameBuffer(BufferedImage buffer) {
        this.frameBuffer = buffer;
        this.bufferWidth = buffer.getWidth();
        this.bufferHeight = buffer.getHeight();
        this.zBuffer = new double[bufferWidth * bufferHeight];
    }


    /** Czyści bufor koloru i głębokości */
    public void clearBuffers(int argb) {
        int[] pixels = ((DataBufferInt) frameBuffer.getRaster().getDataBuffer()).getData();
        Arrays.fill(pixels, argb);
        Arrays.fill(zBuffer, Double.POSITIVE_INFINITY);
    }
    public void clearBuffers(Material material) {
        int[] pixels = ((DataBufferInt) frameBuffer.getRaster().getDataBuffer()).getData();

        BufferedImage texture = material.getTexture();
        Color color = material.getColor();

        if (texture != null) {
            fillWithTexture(pixels, texture, material.getTextureMode());
        } else if (color != null) {
            Arrays.fill(pixels, color.getRGB());
        } else {
            Arrays.fill(pixels, 0xff5c6e7d);
        }

        Arrays.fill(zBuffer, Double.POSITIVE_INFINITY);
    }

    private void fillWithTexture(int[] pixels, BufferedImage texture, TextureMode mode) {

        int w = frameBuffer.getWidth();
        int h = frameBuffer.getHeight();

        int texW = texture.getWidth();
        int texH = texture.getHeight();

        int[] texPixels = getImagePixels(texture);

        switch (mode) {
            case STRETCH -> fillStretch(pixels, texPixels, w, h, texW, texH);
            case TILE -> fillTile(pixels, texPixels, w, h, texW, texH);
            case TILE_FIT -> fillTileFit(pixels, texPixels, w, h, texW, texH);
        }
    }
    private int[] getImagePixels(BufferedImage img) {
        int w = img.getWidth();
        int h = img.getHeight();
        int[] out = new int[w * h];

        img.getRGB(0, 0, w, h, out, 0, w);

        return out;
    }

    private void fillStretch(int[] dst, int[] src, int w, int h, int texW, int texH) {
        for (int y = 0; y < h; y++) {
            float ty = (y / (float) h) * (texH - 1);
            int iy = (int) ty;

            for (int x = 0; x < w; x++) {
                float tx = (x / (float) w) * (texW - 1);
                int ix = (int) tx;

                dst[y * w + x] = src[iy * texW + ix];
            }
        }
    }
    private void fillTile(int[] dst, int[] src, int w, int h, int texW, int texH) {
        for (int y = 0; y < h; y++) {
            int ty = y % texH;
            for (int x = 0; x < w; x++) {
                int tx = x % texW;
                dst[y * w + x] = src[ty * texW + tx];
            }
        }
    }
    private void fillTileFit(int[] dst, int[] src, int w, int h, int texW, int texH) {

        int tilesX = Math.max(1, w / texW);
        int tilesY = Math.max(1, h / texH);

        float scaleX = w / (float) (tilesX * texW);
        float scaleY = h / (float) (tilesY * texH);

        for (int y = 0; y < h; y++) {
            float ty = (y / scaleY) % texH;
            int iy = (int) ty;

            for (int x = 0; x < w; x++) {
                float tx = (x / scaleX) % texW;
                int ix = (int) tx;

                dst[y * w + x] = src[iy * texW + ix];
            }
        }
    }





    /** Rysuje quad (4 punkty) rozbijając go na dwa trójkąty */
    public void drawPolygon(Point[] pts, double[] depths, BufferedImage texture, Color color) {
        if (pts == null || pts.length != 4 || depths == null || depths.length != 4) return;

        double[] u = {0.0, 1.0, 1.0, 0.0};
        double[] v = {0.0, 0.0, 1.0, 1.0};

        rasterizeTriangle(pts[0], pts[1], pts[2], depths[0], depths[1], depths[2],
                u[0], v[0], u[1], v[1], u[2], v[2], texture, color);

        rasterizeTriangle(pts[0], pts[2], pts[3], depths[0], depths[2], depths[3],
                u[0], v[0], u[2], v[2], u[3], v[3], texture, color);
    }

    public void drawTriangle(
            Point[] pts,
            double[] depths,
            double[] uvs,          // [u0, v0, u1, v1, u2, v2]
            BufferedImage texture,
            Color flatColor
    ) {
        if (pts == null || pts.length != 3) return;
        if (depths == null || depths.length != 3) return;
        if (uvs == null || uvs.length != 6) return;

        Point p0 = pts[0];
        Point p1 = pts[1];
        Point p2 = pts[2];

        double z0 = depths[0];
        double z1 = depths[1];
        double z2 = depths[2];

        double u0 = uvs[0], v0 = uvs[1];
        double u1 = uvs[2], v1 = uvs[3];
        double u2 = uvs[4], v2 = uvs[5];

        rasterizeTriangle(
                p0, p1, p2,
                z0, z1, z2,
                u0, v0, u1, v1, u2, v2,
                texture,
                flatColor
        );
    }




    private void rasterizeTriangle(
            Point p0, Point p1, Point p2,
            double z0, double z1, double z2,
            double u0, double v0, double u1, double v1, double u2, double v2,
            BufferedImage texture, Color flatColor
    ) {
        int minX = clamp(Math.min(p0.x, Math.min(p1.x, p2.x)), 0, bufferWidth - 1);
        int maxX = clamp(Math.max(p0.x, Math.max(p1.x, p2.x)), 0, bufferWidth - 1);
        int minY = clamp(Math.min(p0.y, Math.min(p1.y, p2.y)), 0, bufferHeight - 1);
        int maxY = clamp(Math.max(p0.y, Math.max(p1.y, p2.y)), 0, bufferHeight - 1);

        double area = edgeFunction(p0.x, p0.y, p1.x, p1.y, p2.x, p2.y);
        if (area == 0.0) return;
        double invArea = 1.0 / area;

        int[] fb = ((java.awt.image.DataBufferInt) frameBuffer.getRaster().getDataBuffer()).getData();

        int[] tex = null;
        int texW = 0;
        if (texture != null) {
            tex = getImagePixels(texture);
            texW = texture.getWidth();
        }

        double invZ0 = 1.0 / z0;
        double invZ1 = 1.0 / z1;
        double invZ2 = 1.0 / z2;

        double u0z = u0 * invZ0;
        double v0z = v0 * invZ0;
        double u1z = u1 * invZ1;
        double v1z = v1 * invZ1;
        double u2z = u2 * invZ2;
        double v2z = v2 * invZ2;

        for (int y = minY; y <= maxY; y++) {
            for (int x = minX; x <= maxX; x++) {
                double w0 = edgeFunction(p1.x, p1.y, p2.x, p2.y, x, y) * invArea;
                double w1 = edgeFunction(p2.x, p2.y, p0.x, p0.y, x, y) * invArea;
                double w2 = edgeFunction(p0.x, p0.y, p1.x, p1.y, x, y) * invArea;

                boolean inside = (w0 >= 0 && w1 >= 0 && w2 >= 0) || (w0 <= 0 && w1 <= 0 && w2 <= 0);
                if (!inside) continue;

                // interpolacja perspektywiczna
                double invZ = w0 * invZ0 + w1 * invZ1 + w2 * invZ2;
                double tu = (w0 * u0z + w1 * u1z + w2 * u2z) / invZ;
                double tv = (w0 * v0z + w1 * v1z + w2 * v2z) / invZ;
                double z = 1.0 / invZ;

                int idx = y * bufferWidth + x;
                if (z < zBuffer[idx]) {
                    int src;
                    if (tex != null) {
                        int tx = clamp((int)(tu * texture.getWidth()), 0, texture.getWidth() - 1);
                        int ty = clamp((int)((1.0 - tv) * texture.getHeight()), 0, texture.getHeight() - 1);

                        src = tex[ty * texW + tx];
                    } else {
                        src = (flatColor != null ? flatColor.getRGB() : 0xFFFFFFFF);
                    }

                    int srcA = (src >>> 24) & 0xFF;
                    if (srcA > 128) { // !!!!!! Alpha cutoff at 128
                        zBuffer[idx] = z;
                        fb[idx] = src;
                    }
                }
            }
        }
    }


//    private static int blendColors(int src, int dst) {
//        int srcA = (src >>> 24) & 0xFF;
//        if (srcA == 255) return src; // pełna nieprzezroczystość
//        if (srcA == 0) return dst;   // całkowita przezroczystość
//
//        int srcR = (src >>> 16) & 0xFF;
//        int srcG = (src >>> 8) & 0xFF;
//        int srcB = src & 0xFF;
//
//        int dstR = (dst >>> 16) & 0xFF;
//        int dstG = (dst >>> 8) & 0xFF;
//        int dstB = dst & 0xFF;
//
//        double alpha = srcA / 255.0;
//
//        int outR = (int)(srcR * alpha + dstR * (1 - alpha));
//        int outG = (int)(srcG * alpha + dstG * (1 - alpha));
//        int outB = (int)(srcB * alpha + dstB * (1 - alpha));
//
//        return (0xFF << 24) | (outR << 16) | (outG << 8) | outB;
//    }
//
    private static double edgeFunction(int ax, int ay, int bx, int by, int px, int py) {
        return (double)(px - ax) * (by - ay) - (double)(py - ay) * (bx - ax);
    }

    private static int clamp(int v, int lo, int hi) {
        return Math.max(lo, Math.min(hi, v));
    }






    /** LINES */
    // --- Prosta linia (Bresenham + interpolacja Z parametryczna) ---
    public void drawStraightLine(Point p0, double z0,
                                 Point p1, double z1,
                                 Color color) {
        int x0 = p0.x, y0 = p0.y;
        int x1 = p1.x, y1 = p1.y;

        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);
        int sx = x0 < x1 ? 1 : -1;
        int sy = y0 < y1 ? 1 : -1;
        int err = dx - dy;

        int steps = Math.max(dx, dy);
        double z = z0;

        // interpolacja Z parametryczna
        double length = Math.hypot(x1 - x0, y1 - y0);
        double invLength = (length == 0) ? 0 : 1.0 / length;

        int[] fb = ((java.awt.image.DataBufferInt) frameBuffer.getRaster().getDataBuffer()).getData();

        while (true) {
            double t = (length == 0) ? 0 : Math.hypot(x0 - p0.x, y0 - p0.y) * invLength;
            z = z0 + (z1 - z0) * t;

            if (x0 >= 0 && x0 < bufferWidth && y0 >= 0 && y0 < bufferHeight) {
                int idx = y0 * bufferWidth + x0;
                if (z < zBuffer[idx]) {
                    fb[idx] = color.getRGB();
                    zBuffer[idx] = z;
                }
            }
            if (x0 == x1 && y0 == y1) break;
            int e2 = 2 * err;
            if (e2 > -dy) { err -= dy; x0 += sx; }
            if (e2 < dx) { err += dx; y0 += sy; }
        }
    }

//    // --- Krzywa Béziera (adaptacyjne dzielenie) ---
//    public void drawBezierCurve(Point p0, double z0,
//                                Point p1, double z1,
//                                Point p2, double z2,
//                                Point p3, double z3,
//                                Color color) {
//        double flatnessPx = 0.75; // próg płaskości w pikselach
//        subdivideAndDraw(p0, z0, p1, z1, p2, z2, p3, z3,
//                color, flatnessPx, 0, 12);
//    }
//
//    private void subdivideAndDraw(Point p0, double z0,
//                                  Point p1, double z1,
//                                  Point p2, double z2,
//                                  Point p3, double z3,
//                                  Color color,
//                                  double flatnessPx, int depth, int maxDepth) {
//        double d1 = distancePointToSegment(p1, p0, p3);
//        double d2 = distancePointToSegment(p2, p0, p3);
//        double d3 = distancePointToSegment(p0, p1, p2); // dodatkowy test
//        double maxDist = Math.max(Math.max(d1, d2), d3);
//
//        boolean flat = (maxDist <= flatnessPx) || depth >= maxDepth;
//
//        if (flat) {
//            drawStraightLine(p0, z0, p3, z3, color);
//            return;
//        }
//
//        // de Casteljau (t=0.5) – interpolacja punktów i Z
//        PointD p01 = lerpD(p0, p1, 0.5);
//        PointD p12 = lerpD(p1, p2, 0.5);
//        PointD p23 = lerpD(p2, p3, 0.5);
//
//        PointD p012 = lerpD(p01, p12, 0.5);
//        PointD p123 = lerpD(p12, p23, 0.5);
//
//        PointD mid = lerpD(p012, p123, 0.5);
//
//        double z01 = lerp(z0, z1, 0.5);
//        double z12 = lerp(z1, z2, 0.5);
//        double z23 = lerp(z2, z3, 0.5);
//
//        double z012 = lerp(z01, z12, 0.5);
//        double z123 = lerp(z12, z23, 0.5);
//
//        double zMid = lerp(z012, z123, 0.5);
//
//        // Lewa i prawa krzywa
//        subdivideAndDraw(p0,  z0,  p01.toPoint(), z01,  p012.toPoint(), z012, mid.toPoint(), zMid,
//                color, flatnessPx, depth+1, maxDepth);
//        subdivideAndDraw(mid.toPoint(), zMid, p123.toPoint(), z123, p23.toPoint(), z23, p3, z3,
//                color, flatnessPx, depth+1, maxDepth);
//    }
//
//    // --- Funkcje pomocnicze ---
//    private double distancePointToSegment(Point p, Point a, Point b) {
//        double vx = b.x - a.x, vy = b.y - a.y;
//        double wx = p.x - a.x, wy = p.y - a.y;
//        double c1 = vx * wx + vy * wy;
//        if (c1 <= 0) return Math.hypot(p.x - a.x, p.y - a.y);
//        double c2 = vx * vx + vy * vy;
//        if (c2 <= c1) return Math.hypot(p.x - b.x, p.y - b.y);
//        double t = c1 / c2;
//        double projx = a.x + t * vx;
//        double projy = a.y + t * vy;
//        return Math.hypot(p.x - projx, p.y - projy);
//    }
//
//    private PointD lerpD(Point a, Point b, double t) {
//        return new PointD(
//                a.x + (b.x - a.x) * t,
//                a.y + (b.y - a.y) * t
//        );
//    }
//    private PointD lerpD(PointD a, PointD b, double t) {
//        return new PointD(
//                a.x + (b.x - a.x) * t,
//                a.y + (b.y - a.y) * t
//        );
//    }
//    private double lerp(double a, double b, double t) {
//        return a + (b - a) * t;
//    }
//    private static class PointD {
//        final double x, y;
//        PointD(double x, double y) { this.x = x; this.y = y; }
//        Point toPoint() { return new Point((int)Math.round(x), (int)Math.round(y)); }
//    }

    // --- Krzywa Béziera (forward differencing) --- // forward differencing - to je podobno szybsze, ale jak nie zadziała to powyżej zakomentowane może zadziałą zamiast tego
//    public void drawBezierCurve(Point p0, double z0,
//                                Point p1, double z1,
//                                Point p2, double z2,
//                                Point p3, double z3,
//                                Color color, int steps) {
//        // krok parametru
//        double h = 1.0 / steps;
//        double h2 = h * h;
//        double h3 = h2 * h;
//
//        // współczynniki dla osi X
//        double ax = -p0.x + 3*p1.x - 3*p2.x + p3.x;
//        double bx = 3*p0.x - 6*p1.x + 3*p2.x;
//        double cx = -3*p0.x + 3*p1.x;
//        double dx = p0.x;
//
//        // współczynniki dla osi Y
//        double ay = -p0.y + 3*p1.y - 3*p2.y + p3.y;
//        double by = 3*p0.y - 6*p1.y + 3*p2.y;
//        double cy = -3*p0.y + 3*p1.y;
//        double dy = p0.y;
//
//        // współczynniki dla Z
//        double az = -z0 + 3*z1 - 3*z2 + z3;
//        double bz = 3*z0 - 6*z1 + 3*z2;
//        double cz = -3*z0 + 3*z1;
//        double dz = z0;
//
//        // wartości początkowe
//        double x = dx;
//        double y = dy;
//        double z = dz;
//
//        // różnice (delta1, delta2, delta3)
//        double dx1 = ax*h3 + bx*h2 + cx*h;
//        double dy1 = ay*h3 + by*h2 + cy*h;
//        double dz1 = az*h3 + bz*h2 + cz*h;
//
//        double dx2 = 6*ax*h3 + 2*bx*h2;
//        double dy2 = 6*ay*h3 + 2*by*h2;
//        double dz2 = 6*az*h3 + 2*bz*h2;
//
//        double dx3 = 6*ax*h3;
//        double dy3 = 6*ay*h3;
//        double dz3 = 6*az*h3;
//
//        int[] fb = ((java.awt.image.DataBufferInt) frameBuffer.getRaster().getDataBuffer()).getData();
//        // rysowanie krok po kroku
//        for (int i = 0; i <= steps; i++) {
//            int xi = (int)Math.round(x);
//            int yi = (int)Math.round(y);
//
//            if (xi >= 0 && xi < bufferWidth && yi >= 0 && yi < bufferHeight) {
//                int idx = yi * bufferWidth + xi;
//                if (z < zBuffer[idx]) {
//                    fb[idx] = color.getRGB();
//                    zBuffer[idx] = z;
//                }
//            }
//
//            // aktualizacja różnic
//            x += dx1; dx1 += dx2; dx2 += dx3;
//            y += dy1; dy1 += dy2; dy2 += dy3;
//            z += dz1; dz1 += dz2; dz2 += dz3;
//        }
//    }





}

