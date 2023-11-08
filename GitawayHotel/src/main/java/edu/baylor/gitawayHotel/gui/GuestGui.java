package edu.baylor.gitawayHotel.gui;

import javax.swing.JButton;
import javax.swing.JPanel;

import java.awt.BorderLayout;

import edu.baylor.gitawayHotel.user.UserType;

public class GuestGui extends AuthenticatedGui {
	private JButton viewRoomsButton;
	private JButton viewReservationsButton;
	private JPanel panel;

	public GuestGui() {
		super();
	}

	@Override
	protected JPanel layoutMainArea() {
		panel = new JPanel();
		viewRoomsButton = new JButton("View Rooms");
		viewReservationsButton = new JButton("View Reservations");
		
		panel.add(viewRoomsButton);
		panel.add(viewReservationsButton);
		return panel;
	}

	/**Gets the view rooms button 
	 * @return the view rooms button
	 */
	public JButton getViewRoomsButton() {
		return this.viewRoomsButton;
	}
	
	/**Gets the view reservations button
	 * @return the view reservations button
	 */
	public JButton getViewReservationsButton() {
		return this.viewReservationsButton;
	}
	
	@Override
	protected UserType getUserType() {
		return UserType.GUEST;
	}
	
}
