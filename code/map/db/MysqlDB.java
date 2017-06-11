package map.db;


import map.data.City;
import map.data.User;
import map.data.UserManager;
import map.staticthing.StaticThing;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.sql.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;


/**
 * Created by L on 2016/12/18.
 */
public class MysqlDB {
    static final String url = "jdbc:mysql://localhost:3306/test?user=root&password=123456" +
            "&useServerPrepStmts=true&cachePrepStmts=true";
    static String driver = "com.mysql.jdbc.Driver";
    public static Connection connection;
    public static Statement statement;
    static {
        try {
            Class.forName(driver);
            connection = getConnection(url);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection(String url) throws SQLException {
        return connection = DriverManager.getConnection(url);
    }

    public static void main(String[] args) throws SQLException {
        statement = connection.createStatement();
        readAll("city");
        statement.close();
    }

    public static ResultSet readAll(String tableName) throws SQLException {
        return statement.executeQuery("select * from " + tableName);
    }

    public static JTable showOnTable(String tableName, String[] columnName) {
        try {
            statement = connection.createStatement();
            ResultSet set = readAll(tableName);
            JTable table = new JTable(0, columnName.length + 1);
            Enumeration<TableColumn> tableColumns = table.getTableHeader().getColumnModel().getColumns();
            int i = 0;
            tableColumns.nextElement().setHeaderValue("");
            while (tableColumns.hasMoreElements()) tableColumns.nextElement().setHeaderValue(columnName[i++]);
            DefaultTableModel tableModel = ((DefaultTableModel) table.getModel());
            i = 0;

            while (set.next()) {
                Vector<Object> row = new Vector<>();
                row.add(i++);
                for (int j = 1; j <= columnName.length; j++) row.add(set.getString(j));
                tableModel.addRow(row);
            }
            set.close();
            statement.close();
            return table;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static void readAll(ArrayList<User> users, String tableName) {
        try {
            statement = connection.createStatement();
            ResultSet set = readAll(tableName);
            while (set.next()) users.add(new User(set.getInt(1), set.getString(2), set.getString(3), set.getInt(4)));
            set.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void readAll(List<City> cities, String tableName) {
        try {
            statement = connection.createStatement();
            ResultSet set = readAll(tableName);
            while (set.next()) {
                cities.add(new City(set.getInt(1), set.getString(2),
                        set.getString(3), set.getFloat(4),
                        set.getFloat(5)));
            }
            set.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static int getMax(String table, String column) {
        int value = -1;
        try {
            statement = connection.createStatement();
            ResultSet set = statement.executeQuery("SELECT max(" + column + ") FROM " + table);
            if (set.next()) value = set.getInt(1);
            set.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return value;
    }

    public static void writeToUser(String userTableName, UserManager userManager) {
        try {
            statement = connection.createStatement();
            statement.execute("TRUNCATE TABLE " + userTableName);

            int id = 1;
            for (User user : userManager.users) insertUser(statement, user, userTableName, id++);
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateUser(User user, String tableName) {
        try {
            Statement statement = connection.createStatement();
            String sql = "UPDATE " + tableName + " SET name=" +
                    '\'' + user.name + '\'' + ',' + "password=" +
                    '\'' + user.password + '\'' + " WHERE id=" +
                    '\'' + user.id + '\'';
            statement.execute(sql);
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private static void insertUser(Statement statement, User user, String userTableName, int id) throws SQLException {
        String sql = "INSERT INTO " + userTableName +
                " VALUES (" +
                '\'' + id + '\'' + ',' +
                '\'' + user.name + '\'' + ',' +
                '\'' + user.password + '\'' + ',' +
                '\'' + (user.isAdmin ? 1 : 0) + '\'' +
                ")";

        statement.execute(sql);
    }

    public static User findUserByNameOrPassword(String name, String psd) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM " + StaticThing.USER_TABLE_NAME + " WHERE name='")
                .append(name)
                .append("' and password='")
                .append(psd).append("'");
        try {
            statement = connection.createStatement();
            ResultSet set = statement.executeQuery(sql.toString());
            User user = null;
            if (set.next())
                user = new User(set.getInt(1), set.getString(2), set.getString(3), set.getInt(4));
            set.close();
            statement.close();
            return user;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
