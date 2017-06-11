package map.test;

import javax.swing.*;
import java.awt.*;

/**
 * Created by pc on 2017/5/7.
 */
public class Test3 {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->{
            new JFrame(){
                {
                    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                    setSize(1200, 600);
                    setLocationRelativeTo(null);
                }
                {

                    setContentPane(new JComponent() {

                        @Override
                        protected void paintComponent(Graphics g) {
                            Graphics2D d = (Graphics2D) g;
                            d.setClip(-100, -100, 700, 700);

                            d.drawString("这只是一个测试", 0, 0);
                            d.copyArea(-50, -50, 200, 200, 50, 50);
                            d.drawString("23", 50, 50);
                            super.paintComponent(g);

                        }

                    });
                }
            }.setVisible(true);
        });


    }
}
