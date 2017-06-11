package map.test;

/**
 * Created by E on 2017/5/8.
 */
public class Test4 {
    public static void main(String[] args) {
        int i = 0;
        int from = 800;
        while (from < 45000) {
            from /= 0.9;
            i++;
        }
        System.out.println(i);
    }
}
