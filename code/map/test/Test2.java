package map.test;

import map.data.User;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

/**
 * Created by computer on 2017/5/4.
 */
public class Test2 {
    public static void main(String[] args) {


        EventQueue.invokeLater(() -> {
            List<User> rows = new ArrayList<>();
            for (int i = 0; i < 40; i++) {
                rows.add(User.randomUser());
//                Vector<String> row = new Vector<>();
//                row.add(producer.nextName());
//                row.add(producer.nextName());
//                row.add(String.valueOf(producer.nextInt(100)));
//                row.add(String.valueOf(producer.nextBoolean()));
//                rows.add(row);
            }
            new JFrame() {
                {
                    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                    setSize(1200, 800);
                    setLocationRelativeTo(null);
                }

                {
                    String[] array = {"", "username", "password", "identify", "status","",""};

                    JPanel panel = new JPanel();
                    panel.setLayout(new BorderLayout());
                    JTable table = new JTable(0, 7);
                    DefaultTableModel defaultTableModel = ((DefaultTableModel) table.getModel());
//                    rows.forEach((u) -> defaultTableModel.addRow(u.toArray()));
                    Iterator<User> each = rows.iterator();
                    int i;
                    i = 0;
                    while (each.hasNext()) defaultTableModel.addRow(each.next().toArray(i++));
                    JTableHeader tableHeader = table.getTableHeader();
                    TableColumnModel columnModel = tableHeader.getColumnModel();
                    Enumeration<TableColumn> enumeration = columnModel.getColumns();

                    i = 0;
                    while (enumeration.hasMoreElements()) enumeration.nextElement().setHeaderValue(array[i++]);
                    DefaultTableCellRenderer hr = (DefaultTableCellRenderer) tableHeader.getDefaultRenderer();
//                    hr.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
                    panel.add(tableHeader, BorderLayout.NORTH);
                    panel.add(table);
                    JPanel tmp = new JPanel(new BorderLayout());
                    tmp.add(new JScrollPane(panel));

                    int W = getWidth();
                    int H = getHeight();
                    tmp.setBounds(0, 0,((int) (W / 2.5)), ((int) (H / 2.5)));
//                    setLayout(null);
                    add(tmp, BorderLayout.WEST);
                    add(new JPanel() {
                        {
                            setBounds(W >> 1, H >> 1, 100, 100);
                            add(new JButton("new Row") {
                                {
                                    addActionListener((l) -> defaultTableModel.addRow(User.randomUser().toArray(table.getRowCount())));
                                }
                            });
                        }
                    }, BorderLayout.EAST);

//                    columnModel.addColumn(new TableColumn(0));
                }
            }.setVisible(true);
        });
    }
}
