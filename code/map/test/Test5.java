package map.test;

import map.data.User;
import map.window.UploadData;

/**
 * Created by computer on 2017/5/4.
 */
public class Test5 {
    public static void main(String[] args) {
//        EventQueue.invokeLater(MainWindow::new);
        new UploadData(null, new User(0, null, null, true)).setVisible(true);
    }
}
