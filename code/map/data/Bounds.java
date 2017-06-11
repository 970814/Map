package map.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by E on 2017/5/10.
 */
public class Bounds {
    List<float[]> xs = new ArrayList<>();
    List<float[]> ys = new ArrayList<>();

    public void addX(float... X) {
        xs.add(X);
    }
    public void addY(float... Y) {
        ys.add(Y);
    }

    public boolean withinBounds(float x, float y) {
        boolean flag = false;
        for (float[] X : xs)
            if (withinBoundsX(x, X)) {
                flag = true;
                break;
            }
        if (!flag) return false;
        for (float[] Y : ys)
            if (withinBoundsY(y, Y)) return true;
        return false;
    }
    private boolean withinBoundsX(float x, float[] X) {
        return X[0] <= x && x <= X[1];
    }
    private boolean withinBoundsY(float y, float[] Y) {
        return Y[0] <= y && y <= Y[1];
    }

    @Override
    public String toString() {
        return toString(xs) + "x\tmap.y" + toString(ys);
    }

    private String toString(List<float[]> A) {
        StringBuilder builder = new StringBuilder();
        for (float[] a : A) {
            builder.append(Arrays.toString(a))
                    .append(',');
        }
        return builder.toString();
    }
}
