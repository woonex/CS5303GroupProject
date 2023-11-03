package edu.baylor.gitawayHotel.gui;

import javax.swing.JButton;
import javax.swing.JPanel;

import java.awt.BorderLayout;

import edu.baylor.gitawayHotel.user.UserType;

public class GuestGui extends AuthenticatedGui {
	private JButton viewRoomsButton;
	private JPanel panel;

	public GuestGui() {
		super();
	}

	@Override
	protected JPanel layoutMainArea() {
		panel = new JPanel();
		viewRoomsButton = new JButton("View Rooms");
		panel.add(viewRoomsButton, BorderLayout.CENTER);
		
		return panel;
	}

	/**Gets the view rooms button 
	 * @return the view rooms button
	 */
	public JButton getViewRoomsButton() {
		return this.viewRoomsButton;
	}
	
	@Override
	protected UserType getUserType() {
		return UserType.GUEST;
	}
	
}
