package edu.baylor.gitawayHotel.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
	private JButton viewRoomsButton;
	

	public ClerkGui() {
		super();
	}

	private JPanel panel;

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
		return UserType.HOTEL_CLERK;
	}

}
