package edu.baylor.gitawayHotel.gui;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import edu.baylor.gitawayHotel.user.UserType;

public class CreateClerkGui implements IGui {
	private CredentialGui createGui;
	
	private JPanel panel;
	private JButton backButton;
	
	public CreateClerkGui() {
		panel = layoutMainArea();
	}
	
	protected JPanel layoutMainArea() {
		JPanel mainPanel = new JPanel();
		
		JLabel label = new JLabel("Add a hotel clerk below");
		
		mainPanel.add(label);
		createGui = new CredentialGui("", "Create Hotel Clerk");
		
		mainPanel.add(createGui.getFullPanel());
		
		backButton = new JButton("Back to Previous");
		mainPanel.add(backButton);
		
		return mainPanel;
	}
	
	/**Gets the button the admin clicks when they've created an admin
	 * @return the createUserButton
	 */
	public JButton getCreateUserButton() {
		return createGui.getLoginButton();
	}
	
	/**Gets the username the admin has entered for the clerk
	 * @return the username
	 */
	public String getUsername() {
		return createGui.getUsername();
	}
	
	/**Gets the password the admin has entered for the clerk
	 * @return the password
	 */
	public String getPassword() {
		return createGui.getPassword();
	}

	protected UserType getUserType() {
		return UserType.ADMIN;
	}

	public void setClerkUsername(String string) {
		this.createGui.setUsername(string);
	}
	
	public void setClerkPassword(String string) {
		this.createGui.setPassword(string);
	}

	@Override
	public JPanel getFullPanel() {
		return this.panel;
	}

	public JButton getBackButton() {
		return this.backButton;
	}

}
