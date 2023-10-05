package edu.baylor.gitawayHotel.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import edu.baylor.gitawayHotel.user.UserServices;

/**A class that lays out the password change gui
 * @author Kevin
 *
 */
public class ChangeCredentialGui implements IGui {
	private JPanel panel;
	private JButton loginButton;
	private JTextField usernameField;
	private JPasswordField pwField;
	private JPasswordField newpwField;
	private boolean includeUsername;
	private UserServices userServices;
	
	/**Internal class to prevent vertically expanding components
	 * @author Kevin
	 *
	 */
	private static class NonVerticalExpanding extends JPanel {
		NonVerticalExpanding(JComponent component) {
			setLayout(new FlowLayout(FlowLayout.CENTER));
			add(component);
		}
	}
	
	/**Constructor with default names
	 * 
	 */
	public ChangeCredentialGui() {
		this("Please enter your username and a new password", "Change Password");
	}
	
	/**Constructor with configurable names and includes username
	 * @param topLabel
	 * @param bottomButton
	 */
	public ChangeCredentialGui(String topLabel, String bottomButton) {
		this(topLabel, bottomButton, true);
	}
	
	/**Constructor with configurable parameters
	 * @param topLabel
	 * @param bottomButton
	 * @param includeUsername
	 */
	public ChangeCredentialGui(String topLabel, String bottomButton, boolean includeUsername) {
		this.includeUsername = includeUsername;
		doLayout(topLabel, bottomButton);
	}
	
	/**Creates and lays out all components on the panel
	 * @param bottomButton the string to display for the bottom button
	 * @param topLabel the string to display along the top
	 * 
	 */
	private void doLayout(String topLabel, String bottomButton) {
		
		panel = new JPanel();
		panel.setLayout(new BorderLayout());
		
		JLabel topDisplay = new JLabel(topLabel);
		panel.add(topDisplay, BorderLayout.NORTH);
		
		JPanel authPanel = new JPanel();
		authPanel.setLayout(new GridLayout(3,3));
		
		if (includeUsername) {
			setupUsernameArea(authPanel);
			authPanel.add(Box.createGlue());
		}
		
		setupPasswordArea(authPanel);
		setupNewPasswordArea(authPanel);
		
		panel.add(authPanel, BorderLayout.CENTER);
		
		loginButton = new JButton(bottomButton);
		panel.add(loginButton, BorderLayout.SOUTH);
		
		setupEnterHandling();
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
	
	/**Sets up the current password area
	 * @param panel the panel to add the password area to
	 */
	private void setupPasswordArea(JPanel panel) {
		JLabel passwordLabel = new JLabel("Old Password");
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
	
	
	private void setupNewPasswordArea(JPanel panel) {
		JLabel passwordLabel = new JLabel("New Password");
		newpwField = new JPasswordField(10);
		JButton showPwButton = new JButton("Show Password");
		
		//add an action listener to the show password button to either show or hide the password
		showPwButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (showPwButton.getText().equals("Show Password")) {
                	newpwField.setEchoChar((char) 0);
                	showPwButton.setText("Hide Password");
                } else {
                	newpwField.setEchoChar('*');
                	showPwButton.setText("Show Password");
                }
            }
        });
		
		panel.add(new NonVerticalExpanding(passwordLabel));
		panel.add(new NonVerticalExpanding(newpwField));
		panel.add(new NonVerticalExpanding(showPwButton));
	}
	

	/**Sets up the enter key to default to login button
	 * 
	 */
	private void setupEnterHandling() {
		 // Create a KeyAdapter to handle the Enter key press
		 KeyAdapter enterKeyAdapter = new KeyAdapter() {
			 @Override
			 public void keyPressed(KeyEvent e) {
				 if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					 // Trigger the loginButton's action when Enter is pressed
					 loginButton.doClick();
				 }
			 }
		 };

		 // Add the KeyAdapter to the usernameField and pwField
		 usernameField.addKeyListener(enterKeyAdapter);
		 pwField.addKeyListener(enterKeyAdapter);
		 newpwField.addKeyListener(enterKeyAdapter);
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

	public String getNewPassword() {
		String pw = new String(newpwField.getPassword());
		pwField.setText("");
		return new String(pw);
	}
	/**Gets the panel containing all interactable components
	 * @return the panel
	 */
	@Override
	public JPanel getFullPanel() {
		return this.panel;
	}
}
