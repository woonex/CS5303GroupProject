package edu.baylor.gitawayHotel.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JPanel;

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
		topPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		
		modifyButton = new JButton("Modify Account Details");
		logoutButton = new JButton("Logout");
		topPanel.add(modifyButton);
		topPanel.add(logoutButton);
		
		fullPanel.add(topPanel, BorderLayout.NORTH);
	}
	
	/**The method to override to layout the main area
	 * @return the JPanel to add to the rest of the area
	 */
	protected abstract JPanel layoutMainArea();
	
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
}
