package edu.baylor.gitawayHotel.gui;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import edu.baylor.gitawayHotel.user.UserType;

/**The admin GUI allows the logged in admin to create a hotel clerk with a username and a password
 * @author Nathan
 *
 */
public class AdminGui extends AuthenticatedGui {

	private CredentialGui createGui;

	public AdminGui() {
		super();
	}
	
	/**Lays out the main area for the admin to view
	 *
	 */
	@Override
	protected JPanel layoutMainArea() {
		JPanel mainPanel = new JPanel();
		
		JLabel label = new JLabel("Add a hotel clerk below");
		
		mainPanel.add(label);
		createGui = new CredentialGui("", "Create Hotel Clerk");
		
		mainPanel.add(createGui.getFullPanel());
		
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

	@Override
	protected UserType getUserType() {
		return UserType.ADMIN;
	}

	public void setClerkUsername(String string) {
		this.createGui.setUsername(string);
	}
	
	public void setClerkPassword(String string) {
		this.createGui.setPassword(string);
	}


	//create hotel clerk account
	
	
}
