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

/**A class that lays out the login gui section the user interacts with
 * @author Nathan
 *
 */
public class LoginGui {
	private JPanel panel;
	private JButton loginButton;
	private JTextField usernameField;
	private JPasswordField pwField;
	
	/**Internal class to prevent vertically expanding components
	 * @author Nathan
	 *
	 */
	private static class NonVerticalExpanding extends JPanel {
		NonVerticalExpanding(JComponent component) {
			setLayout(new FlowLayout(FlowLayout.CENTER));
			add(component);
		}
	}
	
	public LoginGui() {
		doLayout();
	}
	
	/**Creates and lays out all components on the panel
	 * 
	 */
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
	
	/**Sets up the username area
	 * @param panel the panel to add the username area to
	 */
	private void setupUsernameArea(JPanel panel) {
		JLabel usernameLabel = new JLabel("Username");
		usernameField = new JTextField(10);
		
		panel.add(new NonVerticalExpanding(usernameLabel));
		panel.add(new NonVerticalExpanding(usernameField));
	}
	
	/**Sets up the password area
	 * @param panel the panel to add the password area to
	 */
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
	
	/**Gets the login button 
	 * @return the login button
	 */
	public JButton getLoginButton() {
		return this.loginButton;
	}
	
	/**Gets the username provided by input
	 * @return the username provided
	 */
	public String getUsername() {
		return this.usernameField.getText();
	}
	
	/**Gets the password provided by input
	 * @return the password provided
	 */
	public String getPassword() {
		String pw = new String(pwField.getPassword());
		pwField.setText("");
		return new String(pw);
	}

	/**Gets the panel containing all interactable components
	 * @return the panel
	 */
	public JPanel getPanel() {
		return this.panel;
	}
}
