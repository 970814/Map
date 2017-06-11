package map.window;

import map.data.User;
import map.db.MysqlDB;
import map.staticthing.StaticThing;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Enumeration;
import java.util.Objects;
import java.util.Vector;

import static map.staticthing.StaticThing.SOFTWARE_NAME;
import static map.staticthing.StaticThing.USER_COLUMN;
import static map.staticthing.StaticThing.USER_TABLE_NAME;

/**
 * Created by E on 2017/5/14.
 */
@SuppressWarnings("Duplicates")
public class UserWindow extends JFrame {
    JFrame parent;
    User holder;
    JPanel userPanel;
    public UserWindow(JFrame parent, User holder) throws HeadlessException {
        setTitle(SOFTWARE_NAME + Objects.toString(holder));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.parent = parent;
        this.holder = holder;
        setSize(600, 400);
        setLocationRelativeTo(null);
        add(userPanel = new JPanel(new BorderLayout()), BorderLayout.EAST);
        setJMenuBar(new JMenuBar() {
            {
                add(new JMenu("Action") {
                    {
                        setMnemonic('A');
                        add(new JMenuItem("show on table") {
                            {
                                setMnemonic('S');
                                setAccelerator(KeyStroke.getKeyStroke("ctrl T"));
                                addActionListener(e -> {
                                    setUsersTable(showUsersOnTable());
                                });
                            }
                        });
                        add(new JMenuItem("commit") {
                            {
                                setMnemonic('C');
                                setAccelerator(KeyStroke.getKeyStroke("ctrl C"));
                                addActionListener(e -> {
                                    MysqlDB.updateUser(holder, USER_TABLE_NAME);
                                    setTitle(SOFTWARE_NAME + Objects.toString(holder));
                                });
                            }
                        });
                    }
                });
                add(new JMenu("Search"){
                    {
                        setMnemonic('S');
                        add(new JMenuItem("Point Query"){
                            {
                                setAccelerator(KeyStroke.getKeyStroke("ctrl P"));
                                setMnemonic('P');
                            }
                        });
                        add(new JMenuItem("Range query"){
                            {
                                setMnemonic('R');
                                setAccelerator(KeyStroke.getKeyStroke("ctrl R"));
                            }
                        });
                        add(new JMenuItem("Close query"){
                            {
                                setMnemonic('C');
                                setAccelerator(KeyStroke.getKeyStroke("ctrl C"));
                            }
                        });
                    }
                });
            }
        });
    }
    {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                super.windowClosed(e);
                parent.setVisible(true);
                parent.requestFocus();
            }
        });
    }
    JTable userTable = new JTable(0, StaticThing.USER_COLUMN.length + 1){
        @Override
        public boolean isCellEditable(int row, int column) {
            if (column == 0 || column == 1 || column == 4) return false;
            return true;
        }
    };
    {
        Enumeration<TableColumn> tableColumns = userTable.getTableHeader().getColumnModel().getColumns();
        final int[] i = {0};
        tableColumns.nextElement().setHeaderValue("/");
        while (tableColumns.hasMoreElements()) tableColumns.nextElement().setHeaderValue(USER_COLUMN[i[0]++]);
    }
    DefaultTableModel userModel = (DefaultTableModel) userTable.getModel();
    {
        userTable.setFillsViewportHeight(true);
        userModel.addTableModelListener(e -> {
            int r = e.getFirstRow();
            int c = e.getColumn();
            if (r < 0 || c < 0) return;
            String str = userModel.getValueAt(r, c).toString();
            User user = holder;

            switch (c) {
                case 1:
                    user.id = Integer.valueOf(str);
                    break;
                case 2:
                    user.name = str;
                    break;
                case 3:
                    user.password = str;
                    break;
                case 4:
                    user.isAdmin = Boolean.parseBoolean(str);
                    break;
                default:
                    return;
            }
        });
    }
    public JTable showUsersOnTable() {
        clearTableData(userModel);
        int[] i = {0};
//        userManager.forEach(user -> {
//            Vector<Object> row = user.toVector();
//            row.add(0, i[0]++);
//            userModel.addRow(row);
//        });
        Vector<Object> row = holder.toVector();
        row.add(0, i[0]++);
        userModel.addRow(row);
        return userTable;
    }

    private void clearTableData(DefaultTableModel tm) {
        int size = tm.getRowCount();
        for (int i = 0; i < size; i++) tm.removeRow(tm.getRowCount() - 1);
    }
    private void setUsersTable(JTable table) {
        new Thread() {
            @Override
            public void run() {
                if (!isShowUserTable) {
                    userPanel.add(table.getTableHeader(), BorderLayout.NORTH);
                    userPanel.add(new JScrollPane(table));
                }
                isShowUserTable = true;
                updateUi();
            }
        }.start();
    }
    boolean isShowUserTable = false;
    private void updateUi() {
        setVisible(false);
        setVisible(true);
    }
}
