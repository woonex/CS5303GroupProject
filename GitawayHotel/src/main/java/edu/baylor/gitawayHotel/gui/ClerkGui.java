package edu.baylor.gitawayHotel.gui;

import javax.swing.JLabel;
import javax.swing.JPanel;

/**The clerk is logged in and can either change their password or create or modify room information
 * @author Nathan
 *
 */
public class ClerkGui extends AuthenticatedGui {
	private JPanel panel;
	
	@Override
	protected JPanel layoutMainArea() {
		panel = new JPanel();
		
		panel.add(new JLabel("TODO WIP"));
		return panel;
	}

}
