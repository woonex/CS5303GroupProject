package edu.baylor.gitawayHotel.gui;

import javax.swing.JLabel;
import javax.swing.JPanel;

/**Boilerplate gui template
 * @author Nathan
 *
 */
public class MainGui {
	private JPanel panel;
	
	public MainGui() {
		doLayout();
	}
	
	/**Performs the layout of the panel
	 * 
	 */
	private void doLayout() {
		panel = new JPanel();
		
		JLabel hello = new JLabel("Hello World!");
		
		panel.add(hello);
	}
	
	/**Gets the JPanel for the GUI
	 * @return created panel
	 */
	public JPanel getPanel() {
		return this.panel;
	}
}
