package edu.baylor.gitawayHotel.gui;

import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import edu.baylor.gitawayHotel.user.UserType;

/**The admin GUI allows the logged in admin to select from the possibility to either create a new clerk or to reset passwords for any user
 * @author Nathan
 *
 */
public class AdminGui extends AuthenticatedGui {
	private JButton createClerkButton;
	private JButton resetUserPasswordButton;
	
	public AdminGui() {
		super();
	}
	
	/**Lays out the main area for the admin to view
	 *
	 */
	@Override
	protected JPanel layoutMainArea() {
		JPanel mainPanel = new JPanel(new GridLayout(2, 0));
		
		JLabel label = new JLabel("Select your choice from the actions below");
		
		mainPanel.add(label);
		
		JPanel bottomPanel = new JPanel();
		
		createClerkButton = new JButton("Create Clerk");
		resetUserPasswordButton = new JButton("Reset User Password");
		
		bottomPanel.add(createClerkButton);
		bottomPanel.add(resetUserPasswordButton);
		mainPanel.add(bottomPanel);
		
		return mainPanel;
	}
	
	@Override
	protected UserType getUserType() {
		return UserType.ADMIN;
	}

	public JButton getCreateClerkButton() {
		return createClerkButton;
	}

	public JButton getResetUserPasswordButton() {
		return resetUserPasswordButton;
	}
}
