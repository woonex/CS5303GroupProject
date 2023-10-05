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

		// create an action panel to access common action buttons
		JPanel actionPanel = new JPanel();
		actionPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		viewRoomsButton = new JButton("View Rooms");
		viewRoomsButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Replace "NewPage" with the name of the new page you want to redirect to
				ViewRoomsGui viewRoomsGui = new ViewRoomsGui();
				//viewRoomsGui.setVisible(true);
			}
		});

		actionPanel.add(viewRoomsButton);



		panel.add(actionPanel, BorderLayout.NORTH);

		return panel;
	}

	@Override
	protected UserType getUserType() {
		return UserType.HOTEL_CLERK;
	}

}
