package map.y;

import map.staticthing.StaticThing;

import java.awt.geom.Point2D;

import static map.staticthing.StaticThing.ε;

/**
 * Created by T on 2017/5/12.
 */
@SuppressWarnings("Duplicates")
public class PointConverter {
    public static HSDSTAR.Point convert(HSDSTAR.Node root, HSDSTAR.Point p, int E) {
        double[][] A = deepClone(StaticThing.DEFAULT_SPACE);
        double[][] A2 = deepClone(StaticThing.DEFAULT_SPACE);
        Point2D.Double p2 = new Point2D.Double(p.x, p.y);
        search(root, p2, A, E);
        search2(root, p2, A2, E);
        double x = A2[0][0] + (A2[0][1] - A2[0][0])
                * ((1 - ε) * (p.x - A[0][0]) / (A[0][1] - A[0][0])
                + ε * SHA512Converter.convert(String.valueOf(p.id)));
        double y = A2[1][0] + (A2[1][1] - A2[1][0])
                * ((1 - ε) * (p.y - A[1][0]) / (A[1][1] - A[1][0])
                + ε * SHA512Converter.convert(String.valueOf(p.id)));
        return new HSDSTAR.Point(p.id, x, y);
    }
    public static HSDSTAR.Point deConvert(HSDSTAR.Node root, HSDSTAR.Point p, int E) {
        double[][] A = deepClone(StaticThing.DEFAULT_SPACE);
        double[][] A2 = deepClone(StaticThing.DEFAULT_SPACE);
        Point2D.Double p2 = new Point2D.Double(p.x, p.y);
        search(root, p2, A, E);
        search2(root, p2, A2, E);
        double x = A[0][0] + (A[0][1] - A[0][0])
                * ((1 - ε) * (p.x - A2[0][0]) / (A2[0][1] - A2[0][0])
                + ε * SHA512Converter.convert(String.valueOf(p.id)));
        double y = A[1][0] + (A[1][1] - A[1][0])
                * ((1 - ε) * (p.y - A2[1][0]) / (A2[1][1] - A2[1][0])
                + ε * SHA512Converter.convert(String.valueOf(p.id)));
        return new HSDSTAR.Point(p.id, x, y);
    }
    public static double[][] deepClone(double[][] s) {
        double[][] ns = new double[s.length][];
        for (int i = 0; i < s.length; i++) ns[i] = s[i].clone();
        return ns;
    }

    public static void search(HSDSTAR.Node root, Point2D.Double p, double[][] bounds, int E) {
        dfs(root, p, bounds, E % 2 == 0);// E % 2 == 0 is always true.
    }//which 为真用v导出，否则用v2导出

    private static void dfs(HSDSTAR.Node entry, Point2D.Double p, double[][] bounds, boolean flag) {
        if (entry.left == null) return;
        if (flag) {
            if ( p.x <= entry.v) {
                bounds[0][1] = entry.v;
                dfs(entry.left, p, bounds, false);
            } else {
                bounds[0][0] = entry.v;
                dfs(entry.right, p, bounds, false);
            }
        } else {
            if (p.y <= entry.v) {
                bounds[1][1] = entry.v;
                dfs(entry.left, p, bounds, true);
            } else {
                bounds[1][0] = entry.v;
                dfs(entry.right, p, bounds, true);
            }
        }
    }

    public static void search2(HSDSTAR.Node root, Point2D.Double p, double[][] bounds, int E) {
        dfs2(root, p, bounds, E % 2 == 0);// E % 2 == 0 is always true.
    }//which 为真用v导出，否则用v2导出

    private static void dfs2(HSDSTAR.Node entry, Point2D.Double p, double[][] bounds, boolean flag) {
        if (entry.left == null) return;
        if (flag) {
            if (p.x <= entry.v2) {
                bounds[0][1] = entry.v2;
                dfs2(entry.left, p, bounds, false);
            } else {
                bounds[0][0] = entry.v2;
                dfs2(entry.right, p, bounds, false);
            }
        } else {
            if (p.y <= entry.v2) {
                bounds[1][1] = entry.v2;
                dfs2(entry.left, p, bounds, true);
            } else {
                bounds[1][0] = entry.v2;
                dfs2(entry.right, p, bounds, true);
            }
        }
    }
}
