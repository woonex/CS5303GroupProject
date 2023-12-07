package edu.baylor.gitawayHotel.gui;

import javax.swing.JButton;
import javax.swing.JPanel;

import edu.baylor.gitawayHotel.user.UserType;

/**
 * The clerk is logged in and can either change their password or create or
 * modify room information
 * 
 * @author Nathan
 *
 */
public class ClerkGui extends AuthenticatedGui {
	private JButton modifyRoomsButton;
	

	public ClerkGui() {
		super();
	}

	private JPanel panel;
	private JButton viewRoomStatusButton;
	private JButton viewReservationsButton;
	private JButton makeReservationButton;
	

	@Override
	protected JPanel layoutMainArea() {
		panel = new JPanel();
		modifyRoomsButton = new JButton("Modify Room Information");
		viewRoomStatusButton = new JButton("View Room Status / Manage Check In");
		viewReservationsButton = new JButton("View Reservations");
		makeReservationButton = new JButton("Make Reservations");
		
		panel.add(modifyRoomsButton);//, BorderLayout.CENTER);
		panel.add(viewRoomStatusButton);
		panel.add(viewReservationsButton);
		panel.add(makeReservationButton);

		return panel;
	}

	/**Gets the view rooms button 
	 * @return the view rooms button
	 */
	public JButton getModifyRoomsButton() {
		return this.modifyRoomsButton;
	}
	
	public JButton getViewRoomStatusButton() {
		return this.viewRoomStatusButton;
	}
	
	public JButton getViewReservationsButton() {
		return this.viewReservationsButton;
	}
	
	public JButton getMakeReservationButton() {
		return this.makeReservationButton;
	}

	@Override
	protected UserType getUserType() {
		return UserType.HOTEL_CLERK;
	}

}
