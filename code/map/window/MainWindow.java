package map.window;

import map.data.*;
import map.db.MysqlDB;
import map.staticthing.StaticThing;
import map.y.Splitter;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.*;

import static map.staticthing.StaticThing.*;

/**
 * Created by computer on 2017/5/4.
 */
@SuppressWarnings("Duplicates")
public class MainWindow extends JFrame implements ListSelectionListener {
    private final Object lock = new Object();

    JFrame parent;
    MainWindow this0 = this;
    User holder;
    JPanel tablePanel;
    JPanel userPanel;
    boolean drawInterface = false;
    /////////////////////////////////////////////////////////////
    boolean addMode = false;
    boolean deselectMode = false;
    boolean normalMode = true;
    boolean selectMode = false;
    boolean movedMode = false;
    //////////////////////////////////////
    /////////////////////////////////////////
    JPopupMenu popupMenu = new JPopupMenu();
    String[] str = {"show number", "show location", "show linkLines", "show length"};
    String[] str2 = {"normal", "add", "deselect", "select", "moveTo"};
    JCheckBoxMenuItem[] items = new JCheckBoxMenuItem[str.length];
    JRadioButtonMenuItem[] radioItems = new JRadioButtonMenuItem[str2.length];
    ///////////////////////////////////////
    private boolean paintPoint = false;
    private boolean paintLocation = false;
    private boolean paintLines = false;
    private boolean paintLength = false;
    public boolean paintSlope = false;
    public boolean paintMainSlope = false;
    ///////////////////////////////////////
    Point point = new Point(0, 0);
    Point start = null;
    Stack<Point2D.Float> movePoints = new Stack<>();
    Double dist = null;
    ///////////////////////////////////////////
    public boolean runFlag = false;
    Rectangle rectangle = null;

    ////////////////////////////////////////////
    {
        setSize(W, H);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

    }

    boolean isShowUserTable = false;
    public MainWindow(JFrame parent, User user) throws HeadlessException {
        this.parent = parent;
        holder = user;
        setTitle(SOFTWARE_NAME + Objects.toString(holder));
        {
//            drawInterface = true;
            setJMenuBar(new JMenuBar() {
                {
                    add(new JMenu("Action") {
                        {
                            if (!user.isAdmin) setEnabled(false);
                            setMnemonic('A');
                            add(new JMenu("Show...") {
                                {
                                    setMnemonic('S');
                                    add(new JMenuItem("Show on Table") {
                                        {
                                            setMnemonic('T');
                                            setAccelerator(KeyStroke.getKeyStroke("ctrl T"));
                                        }
                                    }).addActionListener((l) -> {
                                        setTable(showCitiesOnTable());
                                    });
                                    add(new JMenuItem("Show on Interface") {
                                        {
                                            setMnemonic('I');
                                            setAccelerator(KeyStroke.getKeyStroke("ctrl I"));
                                        }
                                    }).addActionListener((l) -> {
                                        if (!drawInterface) {
                                            drawInterface = true;
                                            this0.repaint();
                                        }
                                    });
                                }
                            });
                            add(new JMenu("Close...") {
                                {
                                    setMnemonic('C');
                                    add(new JMenuItem("Close Table") {
                                        {
                                            setMnemonic('L');
                                            setAccelerator(KeyStroke.getKeyStroke("ctrl L"));
                                        }
                                    }).addActionListener((l) -> {
                                        closeTable();
                                    });
                                    add(new JMenuItem("Close Interface") {
                                        {
                                            setMnemonic('N');
                                            setAccelerator(KeyStroke.getKeyStroke("ctrl N"));
                                        }
                                    }).addActionListener((l) -> {
                                        if (drawInterface) {
                                            drawInterface = false;
                                            this0.repaint();
                                        }
                                    });
                                }
                            });
                        }
                    });
                    add(new JMenu("Users") {
                        {
                            setMnemonic('U');

                            add(new JMenuItem("Show All") {
                                {
                                    setMnemonic('L');
                                    addActionListener(e -> {
                                        setUsersTable(showUsersOnTable());
                                    });
                                }
                            });
                            add(new JMenuItem("Add") {
                                {
                                    setMnemonic('A');
                                    addActionListener(e -> {
                                        if (!isShowUserTable) return;
                                        User newUser = User.randomUser();
                                        userManager.add(newUser);
                                        Vector<Object> row = newUser.toVector();
                                        row.add(0, userModel.getRowCount());
                                        userModel.addRow(row);
                                    });
                                }
                            });
                            add(new JMenuItem("Delete") {
                                {
                                    setMnemonic('D');
                                    addActionListener(e -> {
                                        if (!isShowUserTable)return;
                                        ArrayList<Integer> list = new ArrayList<>();
                                        int size = userTable.getRowCount();
                                        for (int i = 0; i < size; i++)
                                            if (userTable.isRowSelected(i)) list.add(i);
                                        for (int i = 0; i < list.size(); i++) {
                                            int where = list.get(i) - i;
                                            userModel.removeRow(where);
                                            userManager.users.remove(where);
                                        }
                                    });
                                }
                            });
                            add(new JMenuItem("Close") {
                                {
                                    setMnemonic('C');
                                    addActionListener(e -> {
                                        closeUserTable();
                                    });
                                }
                            });
                            add(new JMenuItem("Commit"){
                                {
                                    setMnemonic('M');
                                    addActionListener(e -> {
                                        if (!isShowUserTable) return;
                                        MysqlDB.writeToUser(USER_TABLE_NAME, userManager);
                                        showUsersOnTable();
                                    });
                                }
                            });
                        }
                    });
                    add(new JToggleButton(active) {

                        {
                            setFocusPainted(false);
                            addActionListener(e -> {
                                if (isSelected()) {
                                    setText(active);
                                    setBackground(activeColor);
                                    readyMove = true;
                                } else {
                                    setText(silent);
                                    setBackground(Color.gray);
                                    readyMove = false;
                                }
                            });
                            doClick();
                        }
                    });
                    add(new JMenu("Delete") {
                        {
                            setMnemonic('D');
                            add(new JMenuItem("DeselectedAll") {
                                {
                                    setMnemonic('A');
                                    addActionListener(e -> {
                                        dataManager.deselectAll();
                                        if (isShowTable)
                                            citiesTable.clearSelection();
                                        update = true;
                                        this0.repaint();
                                    });
                                }
                            });
                            add(new JMenuItem("Delete") {
                                {
                                    setMnemonic('D');
                                    addActionListener(e -> {
                                        synchronized (lock) {
                                            dataManager.deleteAll();
                                        }
                                        if (isShowTable) showCitiesOnTable();
                                        update = true;
                                        this0.repaint();
                                    });
                                }
                            });
                        }
                    });
                    add(new JMenu("Splitter") {
                            {
                                setMnemonic('S');
                                add(new JMenuItem("Split") {
                                    {
                                        setMnemonic('S');
                                        addActionListener(e -> {
                                            splitLines = Splitter.split(dataManager, DEFAULT_SPACE, E);
                                            isConvert = false;
                                            this0.repaint();
                                        });
                                    }
                                });
                                add(new JMenuItem("clear") {
                                    {
                                        setMnemonic('C');
                                        addActionListener(e -> {
                                            splitLines = null;
                                            this0.repaint();
                                        });
                                    }
                                });

                            }
                    });
//                    add(new JToggleButton(U_P) {
//
//                        {
//                            setFocusPainted(false);
//                            addActionListener(e -> {
//                                if (isSelected()) {
//                                    setText(U_P);
//                                    setBackground(activeColor);
//
//                                } else {
//                                    setText(U_T);
//                                    setBackground(Color.gray);
//
//                                }
//                            });
//                            doClick();
//                        }
//                    });
                    add(new JMenu("Upload data") {
                        {
                            setMnemonic('U');
                            add(new JMenuItem("upload"){
                                {
                                    setMnemonic('U');
                                    addActionListener(e -> {
                                        new UploadData(this0, user);
                                        this0.setVisible(false);
                                    });
                                }
                            });
                        }
                    });

                    add(new JMenu("Set") {
                        {
                            setMnemonic('S');
                            add(new JMenuItem("set E") {
                                {
                                    setMnemonic('E');
                                    addActionListener(e -> {
                                        String str = JOptionPane.showInputDialog(null, "the value of the E(should be odd): ");
                                        try {
                                            E = Integer.parseInt(str);
                                            splitLines = null;
                                            this0.repaint();
                                        } catch (Exception e1) {
                                            e1.printStackTrace();
                                        }
                                    });
                                }
                            });
                            add(new JMenuItem("set Kx") {
                                {
                                    setMnemonic('X');
                                    addActionListener(e -> {
                                        Kx = JOptionPane.showInputDialog(null, "the value of Kx: ");

                                    });
                                }
                            });

                            add(new JMenuItem("set Ky") {
                                {
                                    setMnemonic('Y');
                                    addActionListener(e -> {
                                        Ky = JOptionPane.showInputDialog(null, "the value of Ky: ");

                                    });
                                }
                            });
                        }

                    });
                }
            });
        }
    }



    ArrayDeque<Line2D.Float> splitLines = null;

    {
        for (int i = 0; i < items.length; i++) {
            items[i] = new JCheckBoxMenuItem(str[i]);
            popupMenu.add(items[i]);
        }
        items[0].addActionListener((l) -> {
            paintPoint = !paintPoint;
            this0.repaint();
        });
        items[1].addActionListener((l) -> {
            paintLocation = !paintLocation;
            this0.repaint();
        });
        items[2].addActionListener((l) -> {
            paintLines = !paintLines;
            this0.repaint();
        });
        items[3].addActionListener((l) -> {
            paintLength = !paintLength;
            this0.repaint();
        });
        ButtonGroup group = new ButtonGroup();
        for (int i = 0; i < radioItems.length; i++) {
            radioItems[i] = new JRadioButtonMenuItem(str2[i]);
            popupMenu.add(radioItems[i]);
            group.add(radioItems[i]);
        }
        radioItems[0].setSelected(true);
        radioItems[0].addActionListener((l) -> {
            normalMode = true;
            addMode = false;
            deselectMode = false;
            selectMode = false;
            movedMode = false;
            movePoints.clear();
        });
        radioItems[1].addActionListener((l) -> {
//            if (current < 0) {
//                if (groups.isEmpty()) groups.add(new PointGroup());
//                current = 0;
//            }
            normalMode = false;
            addMode = true;
            deselectMode = false;
            selectMode = false;
            movedMode = false;
            movePoints.clear();
        });
        radioItems[2].addActionListener((l) -> {
            normalMode = false;
            addMode = false;
            deselectMode = true;
            movedMode = false;
            selectMode = false;
            movePoints.clear();
        });
        radioItems[3].addActionListener((l) -> {
            normalMode = false;
            addMode = false;
            deselectMode = false;
            selectMode = true;
            movedMode = false;
        });
        radioItems[4].addActionListener((l) -> {
            normalMode = false;
            addMode = false;
            deselectMode = false;
            selectMode = false;
            movedMode = true;
        });
    }

    private DataManager dataManager = new DataManager(this0);

    private final Line2D.Float line = new Line2D.Float();

    private Point mouseLocation = new Point(0, 0);

    {
        add(tablePanel = new JPanel(new BorderLayout()), BorderLayout.WEST);
        add(userPanel = new JPanel(new BorderLayout()), BorderLayout.EAST);
        add(new JComponent() {
            float dx = 0f;
            float dy = 0f;
            float tmpX = 0f;
            float tmpY = 0f;

            Point src;
            Point now;

            {
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        if (!drawInterface) return;
                        if (readyMove) {
                            src = e.getPoint();
                            return;
                        }
                        if ((deselectMode || selectMode || movedMode) && e.getButton() == MouseEvent.BUTTON1)
                            start = e.getPoint();
                        else start = null;
                    }

                    public void mouseReleased(MouseEvent event) {
                        if (!drawInterface) return;
                        if (readyMove && src != null) {
                            dx += tmpX;
                            dy += tmpY;
                            tmpX = 0f;
                            tmpY = 0f;
                            src = now;
                            return;
                        }
                        if (event.isPopupTrigger()) {
                            popupMenu.show(event.getComponent(), event.getX(), event.getY());
                        } else if (normalMode) {
                            return;
                        } else if (addMode) {
//                            float x = (event.getX() - w2 - dx) / marginX;
//                            float map.y = (h2 - event.getY() + dy) / marginY;
//                            if (x < -180) x += 360;
//                            else if (x > 180) x -= 360;
//                            if (map.y < -90) map.y += 180;
//                            else if (map.y > 90) map.y -= 180;
                            Point2D.Float p = locateXY(event.getX(), event.getY());
                            dataManager.outAdd(p.x, p.y);
                            int last = dataManager.cities.size() - 1;
                            Vector<Object> row = dataManager.cities.get(last).toVector();
                            row.add(0, last);
                            tableModel.addRow(row);
                            update = true;
                            dist = null;
                            this0.repaint();
                        } else if (deselectMode) {
                            if (runFlag) {
                                rectangle = null;
                                this0.repaint();
                                return;
                            }
                            if (rectangle == null) return;
                            Dimension size = rectangle.getSize();
                            Point2D.Float p = locateXY(rectangle.x, rectangle.y, new Point2D.Float());
                            Point2D.Float p2 = locateXY(size.width + rectangle.x, size.height + rectangle.y
                                    , new Point2D.Float());
                            float tmp = p2.y;
                            p2.y = p.y;
                            p.y = tmp;
                            BoundsParser parser = new BoundsParser(p, p2);
                            int n = dataManager.deselect(citiesTable, parser, isShowTable);
                            rectangle = null;
                            if (n > 0) update = true;
                             this0.repaint();
                        } else if (selectMode) {
                            if (runFlag) {
                                rectangle = null;
                                this0.repaint();
                                return;
                            }
                            if (rectangle == null) return;
                            Dimension size = rectangle.getSize();
                            Point2D.Float p = locateXY(rectangle.x, rectangle.y, new Point2D.Float());
                            Point2D.Float p2 = locateXY(size.width + rectangle.x, size.height + rectangle.y
                                    , new Point2D.Float());
                            float tmp = p2.y;
                            p2.y = p.y;
                            p.y = tmp;
                            BoundsParser parser = new BoundsParser(p, p2);
//                            System.out.println("parser: " + parser);
                            int n = dataManager.select(citiesTable, parser, isShowTable);
                            rectangle = null;
                            if (n > 0) update = true;
                            this0.repaint();
                        }
                    }




                });
                addMouseMotionListener(new MouseAdapter() {
                    @Override
                    public void mouseMoved(MouseEvent e) {
                        mouseLocation = e.getPoint();
                        repaint();
                    }

                    @Override
                    public void mouseDragged(MouseEvent e) {
                        if (!drawInterface) return;

                        point = now = e.getPoint();
                        if (readyMove && src != null) {
                            tmpX = (now.x - src.x) % _W;
                            tmpY = (now.y - src.y) % _H;
                            update = true;
                            this0.repaint();
                            return;
                        }
                        if ((deselectMode || selectMode) && start != null) {
                            Point end = e.getPoint();
                            rectangle = new Rectangle(Math.min(start.x, end.x), Math.min(start.y, end.y), Math.abs(end.x - start.x), Math.abs(end.y - start.y));
                            this0.repaint();
                        } else if (movedMode && start != null) {
                            Point end = e.getPoint();
                            int x = end.x - start.x;
                            int y = end.y - start.y;
                            start = end;
                            x /= marginY;
                            y /= marginX;
                            for (int i : dataManager.selected) {
                                City c = dataManager.cities.get(i);
                                c.LNG += x;
                                c.LAT -= y;
                                if (c.LNG < -180) c.LNG += 360;
                                if (c.LNG > 180) c.LNG -= 360;
                                if (c.LAT < -90) c.LAT += 180;
                                if (c.LAT > 90) c.LAT -= 180;
                                if (isShowTable){
                                    citiesTable.setValueAt(c.LAT, i, 4);
                                    citiesTable.setValueAt(c.LNG, i, 5);
                                }
                            }
                            update = true;
                            this0.repaint();
                        }
                    }
                });
                addMouseWheelListener(new MouseAdapter() {
                    @Override
                    public void mouseWheelMoved(MouseWheelEvent e) {
                        if (!drawInterface) return;
                        if (e.isControlDown()) {
                            if (e.getWheelRotation() > 0) {
                                if (DD >= 1f) return;
//                                DD += 0.1;
                                DD /= 0.9;
                                if (DD > 1f) DD = 1f;
                                set();
                            } else {
//                                DD -= 0.1;
                                DD *= 0.9;
                                set();
                            }
                        }

                    }
                });
            }


            {
                addComponentListener(new ComponentAdapter() {
                    @Override
                    public void componentResized(ComponentEvent e) {
                        super.componentResized(e);
                        mW = getWidth();
                        mH = getHeight();
                        if (mW < 0) return;
                        w2 = mW / 2f;
                        h2 = mH / 2f;
                        set();
                    }
                });
            }

            Font defaultFont = new Font("Dialog", Font.PLAIN, 10);
            Stroke pointStoke = new BasicStroke(1.6f);
            int gap = StaticThing.gap;

            int maxDigitHigh = Integer.MIN_VALUE;
            char[] digit = "0123456789".toCharArray();
            int fH;
            FontMetrics metrics = new JLabel().getFontMetrics(defaultFont);
            String bounds0 = "0";
            String bounds90 = "90";
            String bounds180 = "180";
            int boundsW0;
            int boundsW90;
            int boundsW180;

            {
                for (char c : digit) {
                    int h = metrics.stringWidth(String.valueOf(c));
                    if (h > maxDigitHigh) maxDigitHigh = h;
                }
                fH = maxDigitHigh;
                boundsW0 = metrics.stringWidth(bounds0);
                boundsW90 = metrics.stringWidth(bounds90);
                boundsW180 = metrics.stringWidth(bounds180);
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D d = (Graphics2D) g;
                synchronized (lock) {
                    try {
                        if (drawInterface) drawInterface(d);
                    } catch (Exception e) {
                        System.out.println(e.getClass().getCanonicalName());
                    }
                }
            }


            private void drawInterface(Graphics2D d) {
                if (mW <= 0) return;
                float dX = dx + tmpX;
                if (Math.abs(dX) >= _W) {
                    dx = 0;
                    tmpX = 0;
                    dX %= _W;
                    src.x = now.x;
                }
                float dY = dy + tmpY;
                if (Math.abs(dY) >= _H) {
                    dy = 0;
                    tmpY = 0;
                    dY %= _H;
                    src.y = now.y;
                }

                if (update) {
                    drawMap(d, dX, dY);
                    update = false;
                }
                float x = _W - mW >>> 1;
                float y = _H - mH >>> 1;

                d.drawImage(img.getSubimage((int) x, (int) y, mW, mH), 0, 0, null);
                ////////////////////////////////////////////////////////////////////////////////////

                if (rectangle != null) {
                    if (deselectMode) d.setColor(Color.BLUE);
                    else d.setColor(Color.BLACK);
                    d.draw(rectangle);
                }

                float from = x / marginX - 180 - dX / marginX;
                float incX = gap / marginX;
                d.setFont(defaultFont);

                boolean s0 = false;
                boolean s180 = false;
                boolean b180 = false;
                for (int i = 0; i < mW + gap; i += gap) {
                    line.setLine(i, (mH - fH), i, mH);
                    d.draw(line);
                    if (from < -180) {
                        from += 360;
                        b180 = true;
                    } else if (from > 180) {
                        from -= 360;
                        s180 = true;
                    }
                    if (s180 || b180) {
                        b180 = false;
                        s180 = false;
                        float k = i - (180 - Math.abs(from)) * marginX;
                        Stroke stroke = d.getStroke();
                        d.setStroke(dash);
                        d.setColor(water);
                        d.drawString(bounds180, k - (boundsW180 >>> 1), fH + 2);
                        line.setLine(k, fH + 2, k, mH);
                        d.draw(line);
                        d.setColor(Color.BLACK);
                        d.setStroke(stroke);
                    }
                    if (s0 && from > 0) {
                        s0 = false;
                        float k = i - from * marginX;
                        Stroke stroke = d.getStroke();
                        d.setStroke(dash);
                        d.setColor(water);
                        d.drawString(bounds0, k - (boundsW0 >>> 1), fH + 2);
                        line.setLine(k, fH + 2, k, mH);
                        d.draw(line);
                        d.setColor(Color.BLACK);
                        d.setStroke(stroke);
                    }
                    if (from < 0) s0 = true;


                    if (i != 0 && i != mW) {
                        if (Math.abs(from - 180) > 1 && Math.abs(from) > 1) {
                            String str = String.format("%.1f", from);
                            int textW = metrics.stringWidth(str);
                            d.drawString(str, i - (textW >>> 1), (mH - fH));
                        }
                    }
                    from += incX;
                }


                from = 90 - y / marginY + dY / marginY;
                float incY = gap / marginY;


                s0 = false;
                boolean s90 = false;
                boolean b90 = false;

                for (int i = 0; i < mH + gap; i += gap) {
                    line.setLine((mW - fH), i, mW, i);
                    d.draw(line);
                    if (from < -90) {
                        from += 180;
                        b90 = true;
                    } else if (from > 90) {
                        from -= 180;
                        s90 = true;
                    }
                    if (s90 || b90) {
                        s90 = false;
                        b90 = false;
                        float k = i - (90 - Math.abs(from)) * marginY;
                        Stroke stroke = d.getStroke();
                        d.setStroke(dash);
                        d.setColor(water);
                        d.drawString(bounds90, 0, k + (maxDigitHigh >>> 1));
                        line.setLine(boundsW90, k, mW, k);
                        d.draw(line);
                        d.setColor(Color.BLACK);
                        d.setStroke(stroke);
                    }
                    if (s0 && from < 0) {
                        s0 = false;
                        float k = i + from * marginY;
                        Stroke stroke = d.getStroke();
                        d.setStroke(dash);
                        d.setColor(water);
                        d.drawString(bounds0, 0, k + (maxDigitHigh >>> 1));
                        line.setLine(boundsW0, k, mW, k);
                        d.draw(line);
                        d.setColor(Color.BLACK);
                        d.setStroke(stroke);
                    }
                    if (from > 0) s0 = true;
                    if (i != 0 && i != mH) {
                        if (Math.abs(from - 90) > 1 && Math.abs(from) > 1) {
                            String str = String.format("%.1f", from);
                            int textW = metrics.stringWidth(str);
                            d.drawString(str, mW - fH - textW, i + (fH >>> 1));
                        }
                    }
                    from -= incY;
                }
                if (splitLines != null) {
                    d.setColor(Color.BLACK);
                    if (!isConvert) {
                        isConvert = true;
                        float[] p = new float[2];
                        float[] p2 = new float[2];
                        for (Line2D.Float splitLine : splitLines) {
                            convert(splitLine.x1, splitLine.y1, p);
                            convert(splitLine.x2, splitLine.y2, p2);
//                            System.out.println("convert: " + splitLine.x1 + ", " + splitLine.y1 + "; " + splitLine.x2 + ", " + splitLine.y2);
                            splitLine.setLine(p[0], p[1], p2[0], p2[1]);
                            d.draw(splitLine);
                        }
                    } else {
                        for (Line2D.Float splitLine : splitLines) {
                            d.draw(splitLine);
//                            System.out.println(splitLine.x1 + ", " + splitLine.y1 + "; " + splitLine.x2 + ", " + splitLine.y2);
                        }
                    }
                }
                Point location = locateXY(mouseLocation);
                d.setColor(Color.BLUE);
                d.drawString(location.x + ", " + location.y, 0, 10);
            }
            Point2D.Float _p = new Point2D.Float();

            private Point locateXY(Point point) {
                int _x = point.x;
                int _y = point.y;
                int x = (int) ((_x - w2 - dx) / marginX);
                int y = (int) ((h2 - _y + dy) / marginY);
                if (x < -180) x += 360;
                else if (x > 180) x -= 360;
                if (y < -90) y += 180;
                else if (y > 90) y -= 180;
                return new Point(x, y);
            }
            private Point2D.Float locateXY(int x, int y) {
                return locateXY(x, y, _p);
            }

            private Point2D.Float locateXY(float _x, float _y, Point2D.Float p) {
                float x = (_x - w2 - dx) / marginX;
                float y = (h2 - _y + dy) / marginY;
                if (x < -180) x += 360;
                else if (x > 180) x -= 360;
                if (y < -90) y += 180;
                else if (y > 90) y -= 180;
                p.x = x;
                p.y = y;
                return p;
            }


            Stroke dash = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND,
                    3.5f, new float[]{15, 10,}, 0f);



            float[] convertPoint = new float[2];

            private void convert(float x, float y, float[] convertPoint) {
                convertPoint[0] = x * marginX + _w2;
                convertPoint[1] = -y * marginY + _h2;
            }

            private void convert2(float x, float y, float[] convertPoint) {
                convertPoint[0] = (x - w2) / marginX;
                convertPoint[1] = (h2 - y) / marginY;
            }

            private boolean withinBounds(Point2D.Float p, Point location, Dimension size) {
                return withinBounds(p, location.x, size.width + location.x, location.y, size.height + location.y);
            }

            private boolean withinBounds(Point2D.Float p, int x1, int x2, int y1, int y2) {
//                return (x1 <= _p.x * xd && _p.x * xd <= x2)
//                        && (y1 <= _p.map.y * yd && _p.map.y * yd <= y2);
                return (x1 <= p.x * DD && p.x * DD <= x2)
                        && (y1 <= p.y * DD && p.y * DD <= y2);
            }

            BufferedImage img;

            public void drawMap(Graphics2D d, float dX, float dY) {
                Graphics2D pen;
                pen = paper.createGraphics();
                pen.setStroke(pointStoke);
                pen.setColor(d.getBackground());
                pen.fill(new Rectangle2D.Float(0, 0, _W, _H));
                pen.setColor(myColor);
                pen.setStroke(fatterStroke);
                for (int i : dataManager.selected) {
                    City city = dataManager.cities.get(i);
                    float x = city.LNG * marginX + _W / 2;
                    float y = -city.LAT * marginY + _H / 2;
                    line.setLine(x, y, x, y);
                    pen.draw(line);
                }
                pen.setColor(Color.BLACK);
                pen.setStroke(fatStroke);

                for (int i : dataManager.normal) {
                    City city = dataManager.cities.get(i);
                    float x = city.LNG * marginX + _W / 2;
                    float y = -city.LAT * marginY + _H / 2;
                    line.setLine(x, y, x, y);
                    pen.draw(line);
                }
                pen.fillOval(0, 0, 50, 50);

                BufferedImage top;
                BufferedImage left;
                BufferedImage main;

                Graphics2D pen2 = paper2.createGraphics();
                ///////////////////////////////////////////////////////////////////////////////

                if (dX != 0) {
                    if (dX < 0) {
                        left = paper.getSubimage(0, 0, (int) -dX, _H);
                        pen2.drawImage(left, ((int) (_W + dX)), 0, null);
                        main = paper.getSubimage(-((int) dX), 0, ((int) (_W + dX)), _H);
                        pen2.drawImage(main, 0, 0, null);
                    } else {//dX > 0
                        left = paper.getSubimage(((int) (_W - dX)), 0, (int) dX, _H);
                        pen2.drawImage(left, 0, 0, null);
                        main = paper.getSubimage(0, 0, ((int) (_W - dX)), _H);
                        pen2.drawImage(main, ((int) dX), 0, null);
                    }
                    if (dY < 0) {
                        top = paper2.getSubimage(0, 0, _W, (int) -dY);
                        pen.drawImage(top, 0, ((int) (_H + dY)), null);
                        main = paper2.getSubimage(0, -((int) dY), _W, ((int) (_H + dY)));
                        pen.drawImage(main, 0, 0, null);
                        img = paper;
//                        d.drawImage(paper, 0, 0, null);
                    } else if (dY > 0) {
                        top = paper2.getSubimage(0, ((int) (_H - dY)), _W, (int) dY);
                        pen.drawImage(top, 0, 0, null);
                        main = paper2.getSubimage(0, 0, _W, ((int) (_H - dY)));
                        pen.drawImage(main, 0, ((int) dY), null);
                        img = paper;
//                        d.drawImage(paper, 0, 0, null);
                    } else {
                        img = paper2;
//                        d.drawImage(paper2, 0, 0, null);
                    }
                } else {
                    //dX = 0
                    if (dY < 0) {
                        top = paper.getSubimage(0, 0, _W, (int) -dY);
                        pen2.drawImage(top, 0, ((int) (_H + dY)), null);
                        main = paper.getSubimage(0, -((int) dY), _W, ((int) (_H + dY)));
                        pen2.drawImage(main, 0, 0, null);
                        img = paper2;
//                        d.drawImage(paper2, 0, 0, null);
                    } else if (dY > 0) {
                        top = paper.getSubimage(0, ((int) (_H - dY)), _W, (int) dY);
                        pen2.drawImage(top, 0, 0, null);
                        main = paper.getSubimage(0, 0, _W, ((int) (_H - dY)));
                        pen2.drawImage(main, 0, ((int) dY), null);
                        img = paper2;
//                        d.drawImage(paper2, 0, 0, null);
                    } else {
                        img = paper;
//                        d.drawImage(paper, 0, 0, null);
                    }
                }
            }
        });
    }
    boolean isConvert = false;
    boolean update = false;
    BufferedImage paper;
    BufferedImage paper2;
    int mW;
    int mH;
    int _W;
    int _H;
    float w2;
    float h2;
    float marginX;
    float marginY;
    float _w2;
    float _h2;

    public void set() {
        _W = (int) (mW / DD);
        _H = (int) (mH / DD);
        _w2 = _W >>> 1;
        _h2 = _H >>> 1;
        marginX = _W / 360f;
        marginY = _H / 180f;
        paper = new BufferedImage(_W, _H, BufferedImage.TYPE_INT_RGB);
        paper2 = new BufferedImage(_W, _H, BufferedImage.TYPE_INT_RGB);
        update = true;
        this0.repaint();
    }

    boolean readyMove = false;

    boolean isShowTable = false;

    private void setTable(JTable table) {
        new Thread() {
            @Override
            public void run() {
                if (!isShowTable) {//两次重新展示table等价更新显示数据
                    tablePanel.add(table.getTableHeader(), BorderLayout.NORTH);
                    tablePanel.add(new JScrollPane(table));
                }
                isShowTable = true;
                updateUi();
            }
        }.start();
    }
    private void setUsersTable(JTable table) {
        new Thread() {
            @Override
            public void run() {
                if (!isShowUserTable) {//两次重新展示table等价更新显示数据
                    userPanel.add(table.getTableHeader(), BorderLayout.NORTH);
                    userPanel.add(new JScrollPane(table));
                }
                isShowUserTable = true;
                updateUi();
            }
        }.start();
    }
    private void closeUserTable() {
        new Thread() {
            @Override
            public void run() {
                if (isShowUserTable) {
//                    remove(tablePanel);
                    userPanel.removeAll();
                    updateUi();
                    clearTableData(userModel);
                }
                isShowUserTable = false;
            }
        }.start();
    }
    private void closeTable() {
        new Thread() {
            @Override
            public void run() {
                if (isShowTable) {
//                    remove(tablePanel);
                    tablePanel.removeAll();
                    updateUi();
                    clearTableData(tableModel);
                }
                isShowTable = false;
            }
        }.start();
    }

    private void updateUi() {
        setVisible(false);
        setVisible(true);
    }

    {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                super.windowClosed(e);
                System.exit(0);
                parent.setVisible(true);
                parent.requestFocus();
            }
        });
    }

    JTable citiesTable = new JTable(0, StaticThing.CITY_COLUMN.length + 1){
        @Override
        public boolean isCellEditable(int row, int column) {
            if (column == 0) return false;
            return true;
        }
    };
    DefaultTableModel tableModel = (DefaultTableModel) citiesTable.getModel();

    {
        citiesTable.setFillsViewportHeight(true);
//        P.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        tableModel.addTableModelListener(e -> {
            int r = e.getFirstRow();
            int c = e.getColumn();
            if (r < 0 || c < 0) return;
            String str = tableModel.getValueAt(r, c).toString();
            City city = dataManager.cities.get(r);
            switch (c) {
                case 2:
                    city.P_INDEX = str;
                    break;
                case 3:
                    city.P_NAMES = str;
                    break;
                case 4:
                    city.LAT = Float.parseFloat(str);
                    break;
                case 5:
                    city.LNG = Float.parseFloat(str);
                    break;
                default:
                    return;
            }
            update = true;
            this0.repaint();
        });
        citiesTable.getSelectionModel().addListSelectionListener(this);
    }

    {
        Enumeration<TableColumn> tableColumns = citiesTable.getTableHeader().getColumnModel().getColumns();
        final int[] i = {0};
        tableColumns.nextElement().setHeaderValue("/");
        while (tableColumns.hasMoreElements()) tableColumns.nextElement().setHeaderValue(CITY_COLUMN[i[0]++]);
    }

    UserManager userManager = new UserManager(true);
    JTable userTable = new JTable(0, StaticThing.USER_COLUMN.length + 1){
        @Override
        public boolean isCellEditable(int row, int column) {
            if (column == 0 ) return false;
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
            User user = userManager.users.get(r);

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
        userManager.forEach(user -> {
            Vector<Object> row = user.toVector();
            row.add(0, i[0]++);
            userModel.addRow(row);
        });
        return userTable;
    }
    public JTable showCitiesOnTable() {
        citiesTable.getSelectionModel().removeListSelectionListener(this0);
        clearTableData(tableModel);
        int[] i = {0};
        dataManager.forEach(city -> {
            Vector<Object> row = city.toVector();
            row.add(0, i[0]++);
            tableModel.addRow(row);
        });
        citiesTable.getSelectionModel().addListSelectionListener(this0);
        dataManager.selectTableData(citiesTable);
        return citiesTable;
    }

    private void clearTableData(DefaultTableModel tm) {
        int size = tm.getRowCount();
        for (int i = 0; i < size; i++) tm.removeRow(tm.getRowCount() - 1);
    }


    @Override
    public void valueChanged(ListSelectionEvent e) {
        dataManager.select(citiesTable, e.getFirstIndex(), e.getLastIndex());
        update = true;
        this0.repaint();
    }


}
