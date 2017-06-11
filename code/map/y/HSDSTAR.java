package map.y;

import map.staticthing.StaticThing;

import java.awt.geom.Point2D;
import java.util.*;

/**
 * Created by T on 2017/4/21.
 */

//            System.out.println(TL + "\t" + TH);
//范围查询变换
//            double xl = space[0][0] * (1 - ε) + ε * 0;
//            double xh = Z.v2 * (1 - ε) + ε * 1;
//
//            double yl = space[1][0] * (1 - ε) + ε * 0;
//            double yh = space[1][1] * (1 - ε) + ε * 1;
//
//            double xl2 = Z.v2 * (1 - ε) + ε * 0;
//            double xh2 = space[0][1] * (1 - ε) + ε * 1;

//            Z.left = HSD_Key(E - 1, PL, TL, new double[][]{
//                    {xl, xh},
//                    {yl, yh}
//            }, ε);
//            Z.right = HSD_Key(E - 1, PH, TH, new double[][]{
//                    {xl2, xh2},
//                    {yl, yh}
//            }, ε);
//一般查询
@SuppressWarnings("Duplicates")
public class HSDSTAR {
    public static Node HSD_Key(int E, List<Point> P, List<Point> T) {
        return HSD_Key(E, P, T, StaticThing.DEFAULT_SPACE2);
    }
    public static Node HSD_Key(int E, List<Point> P, List<Point> T, double[][] space) {
        Node Z = new Node();
        Point.flag = E % 2 == 0;//先确定是偶数还是奇数，并且设置flag
        P.sort(Point::compare);//如果为真就按x排序，否则就是y排序
        T.sort(Point::compare);
        int m = P.size() >> 1;//得到中间的坐标，
        int n = T.size() >> 1;
        Z.v = P.get(m).getXOrY();//得到中间坐标的x或y值，取决于flag
        Z.v2 = T.get(n).getXOrY();
        int whereP = search(P, m);//为了处理可能存在相同的x或者相同的y，我要找到一个最右边的该值
        int whereT = search(T, n);
        if (E > 0) {
            ArrayList<Point> PL = subList(P, 0, whereP + 1);//因为排序了，所以左边都是比中间值小于等于的
            ArrayList<Point> PH = subList(P, whereP + 1, P.size());//右边都是大于的
            ArrayList<Point> TL = subList(T, 0, whereT + 1);
            ArrayList<Point> TH = subList(T, whereT + 1, T.size());
            Z.left = HSD_Key(E - 1, PL, TL, new double[][]{
                    {space[0][0], Z.v2},
                    space[1]});
            Z.right = HSD_Key(E - 1, PH, TH, new double[][]{
                    {Z.v2, space[0][1]},
                    space[1]});
        }
        return Z;
    }

    public static void main(String[] args) {//测试函数
        double ε = 0.5;
        int E;
        Scanner scanner = new Scanner(System.in);
        Random random = new Random();
//        test(new double[][]{{2, 3}, {5, 4}, {9, 6}, {4, 7}, {8, 1}, {7, 2}, {1, 8}});
        test(new double[][]{{2, 3}, {5, 4}, {9, 6}, {4, 7}, {8, 1}, {7, 2}, });
        for (int i = 0; i < 0; i++) {//测试1次
            E = random.nextInt(13);
            ArrayList<Point> P = new ArrayList<>();
            ArrayList<Point> T = new ArrayList<>();
            int size = (int) (Math.pow(2, E + 1) - 1);

            for (int j = 0; j < size; j++) {
                P.add(new Point(j,Math.random(), Math.random()));//随机数据
                T.add(new Point(j,Math.random(), Math.random()));
            }
            System.out.println("E: " + E);
            System.out.println("原始数据P: " + P);
            System.out.println("原始数据T: " + T);

            double[][] space = new double[][]{//空间定义
                    {0, 1.0},
                    {0, 1.0},
            };
            Node root = HSD_Key(E, P, T, space);//真正使用
            traverse(root);

            System.out.println("你希望测试多少次:");
            int times = scanner.nextInt();
            for (int j = 0; j < times; j++) {
//                double[][] bounds = space.clone();
                double[][] bounds = {
                        space[0].clone(),
                        space[1].clone(),
                };
//                System.out.println("初始化clone: " + String.format("x[%.4f, %.4f] ", bounds[0][0], bounds[0][1])
//                        + String.format("map.y[%.4f, %.4f] ", bounds[1][0], bounds[1][1]));
//                Point2D.Double p = new Point2D.Double(Math.random(), Math.random());
                double[][] bounds2 = {
                        space[0].clone(),
                        space[1].clone(),
                };
                Point2D.Double p = new Point2D.Double(Math.random(), Math.random());
                search(root, p, bounds, bounds2, E, true);
                System.out.println("1点 " + String.format("(%.4f, %.4f) ", p.x, p.y)
                        + " 所属空间: " + String.format("x[%.4f, %.4f] ", bounds[0][0], bounds[0][1])
                        + String.format("map.y[%.4f, %.4f] ", bounds[1][0], bounds[1][1]));
                System.out.println("2点 " + String.format("(%.4f, %.4f) ", p.x, p.y)
                        + " 所属空间: " + String.format("x[%.4f, %.4f] ", bounds2[0][0], bounds2[0][1])
                        + String.format("map.y[%.4f, %.4f] ", bounds2[1][0], bounds2[1][1]));
            }
        }
    }

    public static void test(double[][] ps) {
        ArrayList<Point> P = new ArrayList<>();
        ArrayList<Point> T = new ArrayList<>();
        for (int i = 0; i < ps.length; i++) {
            P.add(new Point(i, ps[i][0], ps[i][1]));
            T.add(new Point(i, Math.random(), Math.random()));
        }
        System.out.println("P: " + P);
        System.out.println("T: " + T);
        double[][] space = new double[][]{//空间定义
                {0, 10.0},
                {0, 10.0},
        };
        double[][] space2 = new double[][]{//空间定义
                {0, 1.0},
                {0, 1.0},
        };
        int E =1;
        Node root = HSD_Key(E, P, T, space);
        System.out.println("构建的树");
        traverse(root);
        double[][] bounds = space.clone();
        double[][] bounds2 = space2.clone();
        Point2D.Double p = new Point2D.Double(8,1);
        search(root, p, bounds, bounds2, E, true);
        System.out.println("点 " + String.format("(%.4f, %.4f) ", p.x, p.y)
                + " 所属空间: " + String.format("x[%.4f, %.4f] ", bounds[0][0], bounds[0][1])
                + String.format("map.y[%.4f, %.4f] ", bounds[1][0], bounds[1][1]));
        System.out.println("点 " + String.format("(%.4f, %.4f) ", p.x, p.y)
                + " 所属空间: " + String.format("x[%.4f, %.4f] ", bounds2[0][0], bounds2[0][1])
                + String.format("map.y[%.4f, %.4f] ", bounds2[1][0], bounds2[1][1]));
    }

    public static void search(Node root, Point2D.Double p, double[][] bounds, double[][] bounds2, int E,final boolean which) {
        dfs(root, p, bounds, bounds2, E % 2 == 0, which);// E % 2 == 0 is always true.
    }//which 为真用v导出，否则用v2导出
//    点 (116.4700, 33.5700)  所属空间: x[117.2800, 180.0000] map.y[31.3600, 90.0000]
//    点 (116.4700, 33.5700)  所属空间: x[0.5635, 1.0000] map.y[0.6649, 1.0000]
    private static void dfs(Node entry, Point2D.Double p, double[][] bounds, double[][] bounds2, boolean flag, boolean which) {
        if (entry.left == null) return;
        if (flag) {
            if (which && p.x <= entry.v || !which && p.x <= entry.v2) {
                bounds[0][1] = entry.v;
                bounds2[0][1] = entry.v2;
                dfs(entry.left, p, bounds, bounds2, false, which);
            } else {
                bounds[0][0] = entry.v;
                bounds2[0][0] = entry.v2;
                dfs(entry.right, p, bounds, bounds2, false, which);
            }
        } else {
            if (which && p.y <= entry.v || !which && p.y <= entry.v2) {
                bounds[1][1] = entry.v;
                bounds2[1][1] = entry.v2;
                dfs(entry.left, p, bounds, bounds2, true, which);
            } else {
                bounds[1][0] = entry.v;
                bounds2[1][0] = entry.v2;
                dfs(entry.right, p, bounds, bounds2, true, which);
            }
        }
    }

    public static void traverse(Node root) {//测试数据用的，root进来，遍历全部子节点，广度遍历
        ArrayDeque<Node> q = new ArrayDeque<>();
        q.offer(root);
        int n = 1;
        int i = 0;
        while (!q.isEmpty()) {
            Node z = q.poll();
            i++;
            System.out.print(z);
            if (i == n) {
                System.out.println();
                i = 0;
                n *= 2;
            }
            if (z.left != null) {
                q.offer(z.left);
                q.offer(z.right);
            }
        }
    }

    public static class Node {//这个类就是节点
        double v;
        double v2;
        Node left;
        Node right;
        @Override
        public String toString() {
            return String.format("(%.4f, %.4f) ", v, v2);
        }
    }

    public static class Point {
        private static int newId = 0;
        @Override
        public String toString() {
            return String.format("(%.4f, %.4f) ", x, y);
        }
        static boolean flag;//使用这个就是为了更加方便写好算法，为真的时候操作x，为假的时候操作y
        final int id;
        double x;
        double y;
        public Point(int _id, double _x, double _y) {
            id = _id;
            x = _x;
            y = _y;
        }

        public int compare(Point o) {//比较两个点，是按照x还是y排序取决于flag
            return flag ? compareToX(o) : compareToY(o);
        }

        public int compareToX(Point o) {
            return Double.compare(x, o.x);
        }

        public int compareToY(Point o) {
            return Double.compare(y, o.y);
        }

        public double getXOrY() {//得到x还是y取决于flag
            return flag ? x : y;
        }
    }
    private static ArrayList<Point> subList(List<Point> list, int from, int to) {
        ArrayList<Point> R = new ArrayList<>();
        for (; from < to; from++) R.add(list.get(from));
        return R;
    }

    private static ArrayList<Point> subList(ArrayList<Point> list, int from, int to, double ε) {
        ArrayList<Point> R = new ArrayList<>();
        for (; from < to; from++) R.add(list.get(from));
        return R;
    }

    private static Point convert(Point z, double ε) {//数据点的变换
        z.x = (1 - ε) * z.x + ε * SHA512Converter.convert(String.valueOf(z.id));
        z.y = (1 - ε) * z.y + ε * SHA512Converter.convert(String.valueOf(z.id));
        return z;
    }

    private static int search(List<Point> P, int where) {
        while (where + 1 < P.size() && P.get(where).getXOrY() == P.get(where + 1).getXOrY()) where++;
        return where;
    }
}

