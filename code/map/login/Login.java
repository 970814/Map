package map.login;

import map.data.User;
import map.db.MysqlDB;
import map.window.UserWindow;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import static map.staticthing.StaticThing.LOGIN;


public class Login extends JFrame implements ActionListener{
	Login this0 = this;
	JTextField username = new JTextField();
	JPasswordField password = new JPasswordField();
	JPanel panel = new JPanel();
	JButton login = new JButton();
	JCheckBox remberPassword = new JCheckBox();

	public Login() {
		setTitle(LOGIN);
		setSize(289, 400);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setContentPane(panel);
		setResizable(false);
		panel.setLayout(null);
		username.setBounds(55, 285, 170, 20);
		panel.add(username);
		password.setBounds(55, 332, 170, 20);
		panel.add(password);
		remberPassword.setBounds(117, 225, 20, 20);
		remberPassword.setBackground(Color.black);
		panel.add(remberPassword);
		ImageIcon icon = new ImageIcon(Login.class.getResource("bg.jpg"));
		JLabel img = new JLabel();
		img.setIcon(icon);
		img.setBounds(0, 0, 280, 380);
		panel.add(img);
		ImageIcon button = new ImageIcon(Login.class.getResource("button.jpg"));
		login.setIcon(button);
		login.setBounds(180, 213, 70, 40);

		login.setToolTipText("Login into new interface");
		panel.add(login);
		login.registerKeyboardAction(this, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
		login.addActionListener(this);


	}

	public static void main(String[] args) {
		new Login().setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String name = username.getText();
		String psd = String.valueOf(password.getPassword());
		User user = MysqlDB.findUserByNameOrPassword(name, psd);
		if (user != null) {
			if (user.isAdmin) new map.window.MainWindow(this0, user).setVisible(true);
			else new UserWindow(this0, user).setVisible(true);
			this0.setVisible(false);
		} else JOptionPane.showMessageDialog(null, "user name or password incorrect!");
		//noinspection StatementWithEmptyBody
		if (!remberPassword.isSelected()) {
			password.setText("");
			username.setText("");
		} else {

		}
	}
}
