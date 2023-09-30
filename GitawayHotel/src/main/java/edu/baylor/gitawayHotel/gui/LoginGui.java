package edu.baylor.gitawayHotel.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class LoginGui {
	private JPanel panel;
	private JButton loginButton;
	private JTextField usernameField;
	private JPasswordField pwField;
	
	private static class NonVerticalExpanding extends JPanel {
		NonVerticalExpanding(JComponent component) {
			setLayout(new FlowLayout(FlowLayout.CENTER));
			add(component);
		}
	}
	
	public LoginGui() {
		doLayout();
	}
	
	private void doLayout() {
		panel = new JPanel();
		panel.setLayout(new BorderLayout());
		
		JLabel topDisplay = new JLabel("Please login to the Gitaway Hotel");
		panel.add(topDisplay, BorderLayout.NORTH);
		
		JPanel authPanel = new JPanel();
		authPanel.setLayout(new GridLayout(2,3));
		setupUsernameArea(authPanel);
		authPanel.add(Box.createGlue());
		
		setupPasswordArea(authPanel);
		panel.add(authPanel, BorderLayout.CENTER);
		
		loginButton = new JButton("Login");
		panel.add(loginButton, BorderLayout.SOUTH);
	}
	
	private void setupUsernameArea(JPanel panel) {
		JLabel usernameLabel = new JLabel("Username");
		usernameField = new JTextField(10);
		
		panel.add(new NonVerticalExpanding(usernameLabel));
		panel.add(new NonVerticalExpanding(usernameField));
	}
	
	private void setupPasswordArea(JPanel panel) {
		JLabel passwordLabel = new JLabel("Password");
		pwField = new JPasswordField(10);
		JButton showPwButton = new JButton("Show Password");
		
		//add an action listener to the show password button to either show or hide the password
		showPwButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (showPwButton.getText().equals("Show Password")) {
                	pwField.setEchoChar((char) 0);
                	showPwButton.setText("Hide Password");
                } else {
                	pwField.setEchoChar('*');
                	showPwButton.setText("Show Password");
                }
            }
        });
		
		panel.add(new NonVerticalExpanding(passwordLabel));
		panel.add(new NonVerticalExpanding(pwField));
		panel.add(new NonVerticalExpanding(showPwButton));
	}
	
	public JButton getLoginButton() {
		return this.loginButton;
	}
	
	public String getUsername() {
		return this.usernameField.getText();
	}
	
	public String getPassword() {
		String pw = new String(pwField.getPassword());
		pwField.setText("");
		return new String(pw);
	}

	/**Gets the panel
	 * @return
	 */
	public JPanel getPanel() {
		return this.panel;
	}
}
