package map.data;

import map.db.MysqlDB;
import map.staticthing.StaticThing;

import java.util.ArrayList;
import java.util.function.Consumer;

/**
 * Created by T on 2017/5/14.
 */
public class UserManager {
    public ArrayList<User> users = new ArrayList<>();

    public UserManager(boolean query) {
        if (query) {

                MysqlDB.readAll(users, StaticThing.USER_TABLE_NAME);

        }
    }

    public void forEach(Consumer<User> action) {
        users.forEach(action);
    }

    public void add(User user) {
        users.add(user);
    }
}
