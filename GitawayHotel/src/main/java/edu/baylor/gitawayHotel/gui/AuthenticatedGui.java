package edu.baylor.gitawayHotel.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import edu.baylor.gitawayHotel.user.UserType;

/**A class that contains GUI components for logged in users. 
 * It conceptually has a change account details and a logout button
 * It is an abstract class to enforce implementation for different logged in user types
 * @author Nathan
 *
 */
public abstract class AuthenticatedGui implements IGui {
	private JPanel fullPanel; //the full panel for this area
	private JPanel topPanel; //the top panel for this area
	
	private JButton logoutButton;
	private JButton modifyButton;
	
	private JLabel usernameLabel;
	
	AuthenticatedGui() {
		doLayout();
	}
	
	/**Performs the layout of the component
	 * 
	 */
	private void doLayout() {
		this.fullPanel = new JPanel();
		
		fullPanel.setLayout(new BorderLayout());
		addTopBar();
		
		JPanel mainArea = layoutMainArea();
		fullPanel.add(mainArea);
	}
	
	/**Adds the top bar to the layout
	 * 
	 */
	private void addTopBar() {
		topPanel = new JPanel();
		topPanel.setLayout(new BorderLayout());
		
		JLabel userTypeLabel = new JLabel(getUserType().toString());
		usernameLabel = new JLabel("");
		
		//create a left panel for aligning to the left for some display parameters
		JPanel leftTopPanel = new JPanel();
		leftTopPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		
		leftTopPanel.add(userTypeLabel);
		leftTopPanel.add(usernameLabel);
		
		modifyButton = new JButton("Modify Account Details");
		logoutButton = new JButton("Logout");
		
		//create a right panel for aligning to right for buttons
		JPanel rightTopPanel = new JPanel();
		rightTopPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		
		rightTopPanel.add(modifyButton);
		rightTopPanel.add(logoutButton);
	
		//add the panels left and right to the top panel
		topPanel.add(leftTopPanel, BorderLayout.WEST);
		topPanel.add(rightTopPanel, BorderLayout.EAST);
		
		fullPanel.add(topPanel, BorderLayout.NORTH);
		
	}
	
	/**The method to override to layout the main area
	 * @return the JPanel to add to the rest of the area
	 */
	protected abstract JPanel layoutMainArea();
	
	/**The method used to determine what type of user the authorized screen is for
	 * @return the userType
	 */
	protected abstract UserType getUserType();
	
	/**Gets the full panel (used to add all components to another panel or frame)
	 * @return
	 */
	@Override
	public JPanel getFullPanel() {
		return this.fullPanel;
	}
	
	/**Gets the logout button
	 * @return the logout button
	 */
	public JButton getLogoutButton() {
		return this.logoutButton;
	}
	
	/**Gets the modify button to indicate the user wants to modify their account
	 * @return the modify button 
	 */
	public JButton getModifyButton() {
		return this.modifyButton;
	}
	
	/**Sets the username for the display at the top
	 * @param username the username
	 */
	public void setUsername(String username) {
		this.usernameLabel.setText(username);
	}
}
