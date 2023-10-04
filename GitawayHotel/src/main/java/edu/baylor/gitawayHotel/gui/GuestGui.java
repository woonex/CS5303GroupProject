package edu.baylor.gitawayHotel.gui;

import javax.swing.JLabel;
import javax.swing.JPanel;

import edu.baylor.gitawayHotel.user.UserType;

public class GuestGui extends AuthenticatedGui {
	
	@Override
	protected JPanel layoutMainArea() {
		JPanel panel = new JPanel();
		
		panel.add(new JLabel("TODO WIP"));
		
		return panel;
	}
	
}
