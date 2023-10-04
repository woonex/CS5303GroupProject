package edu.baylor.gitawayHotel.gui;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**Boilerplate gui template
 * @author Nathan
 *
 */
public class SplashScreen {
	private JPanel panel;
	private JButton nextButton;
	
	public SplashScreen() {
		doLayout();
	}
	
	/**Performs the layout of the panel
	 * 
	 */
	private void doLayout() {
		panel = new JPanel();
		
		JLabel welcomeMessage = new JLabel("Welcome to the Gitaway Hotel reservation system!");
		
		JLabel nextMessage = new JLabel("Please click below to navigate to the login screen");
		nextButton = new JButton("Proceed to login");
		nextMessage.setLabelFor(nextButton);
		
		panel.add(welcomeMessage);
		
		panel.add(nextMessage);
		panel.add(nextButton);
	}
	
	/**Gets the button to move to the next screen
	 * @return
	 */
	public JButton getNextButton() {
		return this.nextButton;
	}
	
	/**Gets the JPanel for the GUI
	 * @return created panel
	 */
	public JPanel getPanel() {
		return this.panel;
	}
}
