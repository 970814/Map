package map.y;

import map.data.City;
import map.data.DataManager;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by T on 2017/5/11.
 */
public class Test {
    public static void main(String[] args) {
        DataManager dataManager = new DataManager(null);
        test(dataManager);
    }

    public static void test(DataManager dataManager) {
        List<HSDSTAR.Point> P = new ArrayList<>();
        List<HSDSTAR.Point> T = new ArrayList<>();
        for (City city : dataManager.cities) {
            System.out.println(city.LNG + ", " + city.LAT);
            P.add(new HSDSTAR.Point(city.id, city.LNG, city.LAT));
            T.add(new HSDSTAR.Point(city.id, Math.random(), Math.random()));
        }
        System.out.println(P);
        System.out.println(T);
        double[][] space = new double[][]{//空间定义
                {-180, 180},
                {-90, 90},
        };
        double[][] space2 = new double[][]{//空间定义
                {0, 1.0},
                {0, 1.0},
        };
        int E = 2;
        HSDSTAR.Node root = HSDSTAR.HSD_Key(E, P, T, space);
        HSDSTAR.traverse(root);
        double[][] bounds = new double[][]{
                space[0].clone(),
                space[1].clone(),
        };
        double[][] bounds2 = new double[][]{
                space2[0].clone(),
                space2[1].clone(),
        };
        Point2D.Double p = new Point2D.Double(116.470, 33.570);
        HSDSTAR.search(root, p, bounds, bounds2, E, true);
        System.out.println("点 " + String.format("(%.4f, %.4f) ", p.x, p.y)
                + " 所属空间: " + String.format("x[%.4f, %.4f] ", bounds[0][0], bounds[0][1])
                + String.format("map.y[%.4f, %.4f] ", bounds[1][0], bounds[1][1]));
        System.out.println("点 " + String.format("(%.4f, %.4f) ", p.x, p.y)
                + " 所属空间: " + String.format("x[%.4f, %.4f] ", bounds2[0][0], bounds2[0][1])
                + String.format("map.y[%.4f, %.4f] ", bounds2[1][0], bounds2[1][1]));
        ArrayDeque<Line2D.Float> lines = split(root, E, space);
        for (Line2D.Float line : lines) {
            System.out.println(line.x1 + ", " + line.y1 + "; " + line.x2 + ", " + line.y2);
        }
    }

    public static ArrayDeque<Line2D.Float> split(HSDSTAR.Node root, int E, double[][] space) {
        ArrayDeque<HSDSTAR.Node> q = new ArrayDeque<>();
        ArrayDeque<double[][]> p = new ArrayDeque<>();
        ArrayDeque<Line2D.Float> lines = new ArrayDeque<>();
        q.offer(root);
        p.offer(space);
        boolean flag = E % 2 == 0;
        while (!q.isEmpty()) {
            HSDSTAR.Node z = q.poll();
            double[][] s = p.poll();
            if (flag) lines.add(new Line2D.Float((float) z.v, (float) s[1][0], (float) z.v, (float) s[1][1]));
            else lines.add(new Line2D.Float((float) s[0][0], (float) z.v, (float) s[0][1], (float) z.v));
            if (z.left != null) {
                q.offer(z.left);
                q.offer(z.right);
                if (flag) {
                    p.offer(new double[][]{{s[0][0], z.v}, s[1].clone()});
                    p.offer(new double[][]{{z.v, s[0][1]}, s[1].clone()});
                } else {
                    p.offer(new double[][]{s[0].clone(), {s[1][0], z.v}});
                    p.offer(new double[][]{s[0].clone(), {s[1][1], z.v}});
                }
            }
        }
        return lines;
    }
}
