package test3d;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class FrontFace extends JPanel {

    private BufferedImage texture;

    public FrontFace() {
        try {
            // Wczytaj obrazek z pliku
            texture = ImageIO.read(new File("texture.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Point project(double x, double y, double z, double k) {
        double f = 1.0 / (1 + z / k);
        return new Point((int)(x * f), (int)(y * f));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (texture == null) return;

        Graphics2D g2 = (Graphics2D) g;

        // Parametry obiektu
        double xc = 500, yc = 300, zc = 50;
        double w = 480, h = 320;
        double k = 200;

        // Kierunek (lekko wzdłuż osi z)
        double dx = 1, dy = 0, dz = 0.3;

        // Normalizacja kierunku w płaszczyźnie x-z
        double len = Math.sqrt(dx*dx + dz*dz);
        double ux = dx/len, uz = dz/len; // szerokość
        double vy = 1, vz = 0;           // wysokość (tu pionowo w y)

        // Rogi trapezu w 3D
        double[][] corners = {
                {xc - w/2*ux, yc - h/2*vy, zc - w/2*uz}, // LD
                {xc + w/2*ux, yc - h/2*vy, zc + w/2*uz}, // PD
                {xc + w/2*ux, yc + h/2*vy, zc + w/2*uz}, // PG
                {xc - w/2*ux, yc + h/2*vy, zc - w/2*uz}  // LG
        };

        // Rzutowanie rogów
        Point[] pts = new Point[4];
        for (int i = 0; i < 4; i++) {
            pts[i] = project(corners[i][0], corners[i][1], corners[i][2], k);
        }

        // Paskowe rysowanie tekstury
//        for (int i = 0; i < texture.getWidth(); i++) {
//            double t = (double)i / texture.getWidth();
//
//            int xLeft = (int)(pts[0].x + t * (pts[3].x - pts[0].x));
//            int yLeft = (int)(pts[0].y + t * (pts[3].y - pts[0].y));
//            int xRight = (int)(pts[1].x + t * (pts[2].x - pts[1].x));
//            int yRight = (int)(pts[1].y + t * (pts[2].y - pts[1].y));
//
//            g2.drawImage(texture,
//                    xLeft, yLeft, xRight, yRight,
//                    0, i, texture.getHeight(), i + 1,
//                    null);
//        }

//        int topYDiff = Math.abs(pts[0].y - pts[1].y);
//        int bottomYDiff = Math.abs(pts[3].y - pts[2].y);
//        int rightXDiff = Math.abs(pts[1].x - pts[2].x);
//        int leftXYDiff = Math.abs(pts[0].x - pts[3].x);
//        int translatedWidth = pts[1].x - pts[0].x;
//
//        int acurracyPx = 1;
//
//        int columns = Math.min(Math.max(topYDiff, bottomYDiff) / acurracyPx, translatedWidth);
//        int imgColWidth = texture.getWidth() / columns;
//        int colWidth = translatedWidth / columns;
//        System.out.printf("%d %d %d %d %d %d %d\n", topYDiff, bottomYDiff, rightXDiff, leftXYDiff, translatedWidth, columns, colWidth);
//
//        for (int i = 0; i < columns; i++) {
//
//            g2.drawImage(texture,
//                    pts[0].x + i*colWidth, pts[0].y - i*(int)Math.round((double)topYDiff/columns), pts[0].x + (i+1)*colWidth, pts[3].y - i*(int)Math.round((double)bottomYDiff/columns),
//                    i*imgColWidth, 0, (i+1)*imgColWidth, texture.getHeight(),
//                    null);
//        }
        // Załóżmy: pts[0]=LT, pts[1]=RT, pts[2]=RB, pts[3]=LB
        int columns = texture.getWidth(); // 1 pasek = 1 źródłowa kolumna
        for (int i = 0; i < columns; i++) {
            double t = (double) i / (columns - 1);

            // Interpolacja punktów na górnej i dolnej krawędzi trapezu
            double xTopD    = pts[0].x + t * (pts[1].x - pts[0].x);
            double yTopD    = pts[0].y + t * (pts[1].y - pts[0].y);
            double xBottomD = pts[3].x + t * (pts[2].x - pts[3].x);
            double yBottomD = pts[3].y + t * (pts[2].y - pts[3].y);

            // Docelowy prostokąt dla jednego paska (szerokość 1 px)
            int dx1 = (int) Math.round(xTopD);
            int dy1 = (int) Math.round(yTopD);
            int dx2 = dx1 + 1; // jedna kolumna na ekranie
            int dy2 = (int) Math.round(yBottomD);

            // Źródłowa kolumna z tekstury
            int sx1 = i;
            int sy1 = 0;
            int sx2 = i + 1;
            int sy2 = texture.getHeight();

            // Rysowanie paska
            g2.drawImage(texture, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, null);
        }






        // Obrys trapezu
        g2.setColor(Color.RED);
        g2.drawPolygon(
                new int[]{pts[0].x, pts[1].x, pts[2].x, pts[3].x},
                new int[]{pts[0].y, pts[1].y, pts[2].y, pts[3].y},
                4
        );
    }

    public static void main(String[] args) {
        JFrame f = new JFrame("Front face with texture");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(600, 400);
        f.add(new FrontFace());
        f.setVisible(true);
    }
}
