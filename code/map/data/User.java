package map.data;

import map.db.MysqlDB;
import map.staticthing.StaticThing;

import java.util.Vector;

/**
 * Created by computer on 2017/5/4.
 */
public class User {
    public  int id;
    public String name;
    public String password;
    public boolean isAdmin;

    public User(int id, String name, String password, boolean isAdmin) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.isAdmin = isAdmin;
    }

    public User(int id, String name, String password, int isAdmin) {
        this(id, name, password, isAdmin != 0);
    }

    public Object[] toArray(Object... args) {
        return args;
    }

    static RandomDataProducer producer = new RandomDataProducer();
    static int maxId = MysqlDB.getMax(StaticThing.USER_TABLE_NAME, "id");
    public static User randomUser() {
        return new User(++maxId
                , producer.nextName()
                , producer.nextName()
                , false);
    }

    public Object[] toArray(int i) {
        return toArray(i, name, password, id, isAdmin);
    }

    public Vector<Object> toVector() {
        Vector<Object> row = new Vector<>();
        row.add(id);
        row.add(name);
        row.add(password);
        row.add(isAdmin);
        return row;
    }

    @Override
    public String toString() {
        return "current user: " + name;
    }

}
