package map.test;

import map.data.User;
import map.window.MainWindow;

import javax.swing.*;
import java.awt.*;

/**
 * Created by E on 2017/5/14.
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainWindow(null, new User(0, null, null, true)).setVisible(true));

    }
}
