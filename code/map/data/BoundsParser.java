package map.data;

import java.awt.geom.Point2D;

/**
 * Created by E on 2017/5/10.
 */
public class BoundsParser {
    Bounds bounds = new Bounds();
    public BoundsParser(Point2D.Float p, Point2D.Float p2) {
        float x = p.x;
        float x2 = p2.x;
        if (x * x2 > 0) {
            float min = Math.min(x, x2);
            x2 = Math.max(x, x2);
            x = min;
        }
        if (x > x2) {
            float _x2 = x2;
            float _x = -180;
            x2 = 180;
            bounds.addX(_x, _x2);
        }
        bounds.addX(x, x2);

        float y = p.y;
        float y2 = p2.y;
        if (y * y2 > 0) {
            float min = Math.min(y, y2);
            y2 = Math.max(y, y2);
            y = min;
        }
        if (y > y2) {
            float _y2 = y2;
            float _y = -90;
            y2 = 90;
            bounds.addY(_y, _y2);
        }
        bounds.addY(y, y2);
    }

    public boolean withinBounds(float x, float y) {
        return bounds.withinBounds(x, y);
    }

    @Override
    public String toString() {
        return bounds.toString();
    }
}

