package map.login;

import map.data.User;
import map.db.MysqlDB;

import javax.swing.*;
import java.awt.*;

import static map.staticthing.StaticThing.H_L;
import static map.staticthing.StaticThing.W_L;

/**
 * Created by computer on 2017/5/4.
 */
public class MainWindow extends JFrame {
    MainWindow this0 = this;
    {
        setSize(W_L, H_L);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    JTextField username;
    JPasswordField password;
    {
        setLayout(new GridLayout(3, 2, 30, 20));

        add(new JLabel("username: "));
        add(username = new JTextField());
        add(new JLabel("password: "));
        add(password = new JPasswordField());
        add(new JButton("commit"){
            {
                addActionListener((l)-> {
                    String name = username.getText();
                    String psd = String.valueOf(password.getPassword());
                    User user = MysqlDB.findUserByNameOrPassword(name, psd);
                    if (user != null) {
                        new map.window.MainWindow(this0, user).setVisible(true);
                        this0.setVisible(false);
                    }

                });
            }
        });

    }
    {
        setVisible(true);
    }
}
