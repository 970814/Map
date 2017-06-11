package map.data;

import map.db.MysqlDB;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.function.Consumer;

import static map.staticthing.StaticThing.DATA_TABLE_NAME;

/**
 * Created by pc on 2017/5/6.
 */
@SuppressWarnings("Duplicates")
public class DataManager {

    Random random = new Random();
    boolean changed = true;
    public java.util.List<City> cities = new ArrayList<>();
    public java.util.List<Integer> normal = new ArrayList<>();
    public java.util.List<Integer> selected = new ArrayList<>();



    public void query() {
        if (changed) {
            changed = false;
            cities.clear();
            MysqlDB.readAll(cities, DATA_TABLE_NAME);
//            for (int i = 0; i < 10; i++)
//                cities.add(new City(i, String.valueOf(i * 2),
//                        String.valueOf(random.nextBoolean()), (random.nextBoolean() ? 1 : -1) * random.nextInt(90),
//                        (random.nextBoolean() ? 1 : -1) * random.nextInt(180)));
            adjust();
        }
    }

    public void forEach(Consumer<City> action) {
        cities.forEach(action);
    }


    public void outAdd(float x, float y) {
        normal.add(cities.size());
        cities.add(new City(random.nextInt(), "new", "new", y, x));
    }

    public int select(JTable table, BoundsParser parser, boolean isShowTable) {

        if (isShowTable)
            table.getSelectionModel().removeListSelectionListener(listener);
        int c = normal.size();
        Iterator<Integer> each = normal.iterator();
        while (each.hasNext()) {
            int x = each.next();
            City city = cities.get(x);
            if (parser.withinBounds(city.LNG, city.LAT)) {
                selected.add(x);
                each.remove();
                if (isShowTable)
                    table.addRowSelectionInterval(x, x);
            }
        }
        if (isShowTable)
            table.getSelectionModel().addListSelectionListener(listener);
        return c - normal.size();
    }

    public void deselectAll() {
        Iterator<Integer> each = selected.iterator();
        while (each.hasNext()) {
            normal.add(each.next());
            each.remove();
        }
    }

    public DataManager(ListSelectionListener listener, boolean query) {
        if (query) query();
        this.listener = listener;
    }
    public DataManager(ListSelectionListener listener) {
        this.listener = listener;
        {
            query();
        }
    }

    ListSelectionListener listener;
    public void selectTableData(JTable table) {
        table.getSelectionModel().removeListSelectionListener(listener);
        for (Integer row : selected) table.addRowSelectionInterval(row, row);
        table.getSelectionModel().addListSelectionListener(listener);
    }

    public int deselect(JTable table, BoundsParser parser, boolean isShowTable) {
        if (isShowTable)
            table.getSelectionModel().removeListSelectionListener(listener);
        int c = selected.size();
        Iterator<Integer> each = selected.iterator();
        while (each.hasNext()) {
            int x = each.next();
            City city = cities.get(x);
            if (parser.withinBounds(city.LNG, city.LAT)) {
                normal.add(x);
                each.remove();
                if (isShowTable)
                    table.removeRowSelectionInterval(x, x);
            }
        }
        if (isShowTable)
            table.getSelectionModel().addListSelectionListener(listener);
        return c - selected.size();
    }

    public void select(JTable table,int... ss) {
//        System.out.println(Arrays.toString(ss));
        table.getSelectionModel().removeListSelectionListener(listener);
        deselectAll();
        int size = cities.size();
        for (int i = 0; i <size; i++) {
            if (table.isRowSelected(i)) {
                int finalI = i;
                normal.removeIf(v -> v == finalI);
                selected.add(finalI);
                table.addRowSelectionInterval(i, i);
            }
        }
        table.getSelectionModel().addListSelectionListener(listener);
    }

    public void deleteAll() {
        selected.sort(Integer::compareTo);
        Iterator<Integer> each = selected.iterator();
        int i = 0;
        while (each.hasNext()) {
            int x = each.next();
            cities.remove(x - i);
            each.remove();
            i++;
        }
        adjust();
    }

    private void adjust() {
        normal.clear();
        int size = cities.size();
        for (int i = 0; i < size; i++) normal.add(i);
    }
}
