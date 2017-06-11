package map.test;

import map.data.User;
import map.window.MainWindow;

/**
 * Created by computer on 2017/5/4.
 */
public class Test {
    public static void main(String[] args) {

        new MainWindow(null, new User(0, "admin", null, true)).setVisible(true);

    }
}
