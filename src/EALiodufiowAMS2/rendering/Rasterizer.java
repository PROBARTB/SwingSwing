package EALiodufiowAMS2.rendering;

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

    public Rasterizer(int bufferWidth, int bufferHeight) {
        setBufferSize(bufferWidth, bufferHeight);
    }

    public int getBufferWidth() { return bufferWidth; }
    public int getBufferHeight() { return bufferHeight; }
    public BufferedImage getFrameBuffer() { return frameBuffer; }

    /** Ustawia nowy rozmiar bufora */
    public void setBufferSize(int newWidth, int newHeight) {
        if (newWidth <= 0 || newHeight <= 0) {
            throw new IllegalArgumentException("Buffer size must be positive");
        }
        this.bufferWidth = newWidth;
        this.bufferHeight = newHeight;
        this.frameBuffer = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        this.zBuffer = new double[newWidth * newHeight];
        clearBuffers(0xFF000000);
    }

    /** Czyści bufor koloru i głębokości */
    public void clearBuffers(int argb) {
        int[] pixels = ((DataBufferInt) frameBuffer.getRaster().getDataBuffer()).getData();
        Arrays.fill(pixels, argb);
        Arrays.fill(zBuffer, Double.POSITIVE_INFINITY);
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

        // przygotuj wartości 1/z i u/z, v/z
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
                    if (texture != null) {
                        int tx = clamp((int)(tu * texture.getWidth()), 0, texture.getWidth() - 1);
                        int ty = clamp((int)((1.0 - tv) * texture.getHeight()), 0, texture.getHeight() - 1);

                        src = texture.getRGB(tx, ty);
                    } else {
                        src = (flatColor != null ? flatColor.getRGB() : 0xFFFFFFFF);
                    }

                    int srcA = (src >>> 24) & 0xFF;
                    if (srcA == 255) {
                        // tylko pełne krycie zapisujemy
                        zBuffer[idx] = z;
                        fb[idx] = src;
                    }
                    // jeśli srcA < 255 → ignorujemy, nie zapisujemy nic
                }
            }
        }
    }


    private static int blendColors(int src, int dst) {
        int srcA = (src >>> 24) & 0xFF;
        if (srcA == 255) return src; // pełna nieprzezroczystość
        if (srcA == 0) return dst;   // całkowita przezroczystość

        int srcR = (src >>> 16) & 0xFF;
        int srcG = (src >>> 8) & 0xFF;
        int srcB = src & 0xFF;

        int dstR = (dst >>> 16) & 0xFF;
        int dstG = (dst >>> 8) & 0xFF;
        int dstB = dst & 0xFF;

        double alpha = srcA / 255.0;

        int outR = (int)(srcR * alpha + dstR * (1 - alpha));
        int outG = (int)(srcG * alpha + dstG * (1 - alpha));
        int outB = (int)(srcB * alpha + dstB * (1 - alpha));

        return (0xFF << 24) | (outR << 16) | (outG << 8) | outB;
    }

    private static double edgeFunction(int ax, int ay, int bx, int by, int px, int py) {
        return (double)(px - ax) * (by - ay) - (double)(py - ay) * (bx - ax);
    }

    private static int clamp(int v, int lo, int hi) {
        return Math.max(lo, Math.min(hi, v));
    }






    /** LINES */
    public void drawLine(Point p0, double z0, Point p1, double z1, Color color) {
        if (p0 == null || p1 == null) return;

        int x0 = p0.x, y0 = p0.y;
        int x1 = p1.x, y1 = p1.y;

        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);
        int sx = x0 < x1 ? 1 : -1;
        int sy = y0 < y1 ? 1 : -1;
        int err = dx - dy;

        int[] fb = ((java.awt.image.DataBufferInt) frameBuffer.getRaster().getDataBuffer()).getData();

        while (true) {
            int idx = y0 * bufferWidth + x0;
            double z = (dx+dy==0) ? (z0+z1)/2.0 : (z0 + z1)/2.0; // prosta interpolacja
            if (z < zBuffer[idx]) {
                fb[idx] = color.getRGB();
                zBuffer[idx] = z;
            }
            if (x0 == x1 && y0 == y1) break;
            int e2 = 2 * err;
            if (e2 > -dy) { err -= dy; x0 += sx; }
            if (e2 < dx) { err += dx; y0 += sy; }
        }
    }

}
