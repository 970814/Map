package map.staticthing;

import java.awt.*;

/**
 * Created by computer on 2017/5/4.
 */
public class StaticThing {
    public static final int W_L = 600;
    public static final int H_L = 400;
    public static final int W = 1400;
    public static final int H = 700;
    public static final String DATA_TABLE_NAME = "city";
    public static final String USER_TABLE_NAME = "user";
    public static final String[] CITY_COLUMN = {
            "id",
            "P_INDEX",
            "P_NAMES",
            "LAT",
            "LNG",
    };
    public static final String[] USER_COLUMN = {
            "id",
            "name",
            "password",
            "authority",
    };

    public static float DD = 1f;
    public static float dd1 = 0.1f;
    public static float dd2 = 0.05f;
    public static final String active = "normal";
    public static final String silent = "silent ";
    public static final Color activeColor = new Color(117, 189, 247);
    public static final int gap = 160;
    public static final Color color = new Color(0, 0, 255, 48);
    public static final Color water = new Color(55, 168, 226);
    public static final Color myColor = Color.blue.darker();
    public static final Color myColor2 = Color.red.darker();
    public static final BasicStroke fatStroke = new BasicStroke(2.0f);
    public static final BasicStroke thinStroke = new BasicStroke(1.0f);
    public static final BasicStroke fatterStroke = new BasicStroke(4.0f);
    public static final double[][] DEFAULT_SPACE = new double[][]{//空间定义
            {-180, 180},
            {-90, 90},
    };
    public static final String SOFTWARE_NAME = "";
    public static final String LOGIN = "";
    public static int E = 2;
    public static String Kx = "";
    public static String Ky = "";
    public static final double[][] DEFAULT_SPACE2 = new double[][]{//空间定义
            {0, 1},
            {0, 1},
    };
    public static double ε = 0.5;
    public static final String U_P = "Upload P";
    public static final String U_T = "Upload T";
}
