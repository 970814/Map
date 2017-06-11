package map.data;

import java.util.Vector;

/**
 * Created by pc on 2017/5/5.
 */
public class City {
    public final int id;
    public String P_INDEX;
    public String P_NAMES;
    public float LAT;
    public float LNG;

    public City(int id, String p_INDEX, String p_NAMES, float LAT, float LNG) {
        this.id = id;
        P_INDEX = p_INDEX;
        P_NAMES = p_NAMES;
        this.LAT = LAT;
        this.LNG = LNG;
    }

    float x;
    float y;

    public Vector<Object> toVector() {
        Vector<Object> row = new Vector<>();
        row.add(id);
        row.add(P_INDEX);
        row.add(P_NAMES);
        row.add(LAT);
        row.add(LNG);
        return row;
    }
}
