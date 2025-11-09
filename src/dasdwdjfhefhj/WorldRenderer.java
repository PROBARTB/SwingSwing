package dasdwdjfhefhj;

import java.awt.*;
import java.awt.geom.*;

import dasdwdjfhefhj.tracks.*;
import dasdwdjfhefhj.helpers.*;

public class WorldRenderer {
    public void drawTracks(Graphics2D g2, TrackSegment start) {
        // iteracja po segmentach, bez stałego kroku metrowego
        TrackSegment seg = start;
        int guard = 0;
        while (seg != null && guard++ < 1000) {
            if (seg instanceof StraightSegment s) {
                // prosta: narysuj cały odcinek
                Vec2 p0 = s.getStart();
                Vec2 p1 = s.posAt(s.getLength());
                Line2D line = new Line2D.Double(p0.x * Units.M_TO_PX, p0.z * Units.M_TO_PX,
                        p1.x * Units.M_TO_PX, p1.z * Units.M_TO_PX);
                g2.setColor(Color.DARK_GRAY);
                g2.setStroke(new BasicStroke(3));
                g2.draw(line);
            } else if (seg instanceof BezierArc b) {
                // próbkowanie gęste łuku
                Path2D path = new Path2D.Double();
                int N = 64;
                for (int i = 0; i <= N; i++) {
                    double sLocal = b.getLength() * (i / (double) N);
                    Vec2 p = b.posAt(sLocal);
                    double x = p.x * Units.M_TO_PX;
                    double z = p.z * Units.M_TO_PX;
                    if (i == 0) path.moveTo(x, z); else path.lineTo(x, z);
                }
                g2.setColor(Color.DARK_GRAY);
                g2.setStroke(new BasicStroke(3));
                g2.draw(path);
            } else if (seg instanceof SwitchSegment sw) {
                // lead
                StraightSegment lead = sw.getLead();
                Vec2 p0 = lead.getStart();
                Vec2 p1 = lead.posAt(lead.getLength());
                g2.setColor(new Color(80,80,80));
                g2.setStroke(new BasicStroke(3));
                g2.draw(new Line2D.Double(p0.x * Units.M_TO_PX, p0.z * Units.M_TO_PX,
                        p1.x * Units.M_TO_PX, p1.z * Units.M_TO_PX));
                // gałęzie jako informacyjnie cieniej (opcjonalnie)
                TrackSegment left = sw.getLeftBranch();
                TrackSegment right = sw.getRightBranch();
                drawBranch(g2, left, new Color(120,120,120));
                drawBranch(g2, right, new Color(120,120,120));
            }

            seg = seg.getNext();
        }
    }

    private void drawBranch(Graphics2D g2, TrackSegment seg, Color c) {
        g2.setColor(c);
        g2.setStroke(new BasicStroke(2));
        if (seg instanceof StraightSegment s) {
            Vec2 p0 = s.getStart();
            Vec2 p1 = s.posAt(s.getLength());
            g2.draw(new Line2D.Double(p0.x * Units.M_TO_PX, p0.z * Units.M_TO_PX,
                    p1.x * Units.M_TO_PX, p1.z * Units.M_TO_PX));
        } else if (seg instanceof BezierArc b) {
            Path2D path = new Path2D.Double();
            int N = 48;
            for (int i = 0; i <= N; i++) {
                double sLocal = b.getLength() * (i / (double) N);
                Vec2 p = b.posAt(sLocal);
                double x = p.x * Units.M_TO_PX;
                double z = p.z * Units.M_TO_PX;
                if (i == 0) path.moveTo(x, z); else path.lineTo(x, z);
            }
            g2.draw(path);
        }
    }
}