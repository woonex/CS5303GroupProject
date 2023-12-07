package edu.baylor.gitawayHotel.gui;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import edu.baylor.gitawayHotel.user.User;
import edu.baylor.gitawayHotel.user.UserServices;

/**Allows a clerk to select a guest to create a reservation in behalf of
 * @author nwoolley
 *
 */
public class ClerkMakeReservationGui implements IGui {
	private static final User SELECT_USER = new User("--Select User--");
	private JPanel panel;
	private UserServices userServices;
	
	private JButton selectButton;
	private JButton backButton;
	private JComboBox<User> userChoose;
	
	public ClerkMakeReservationGui(UserServices userServices) {
		this.userServices = userServices;
		doLayout();
	}
	
	/**Performs the layout of the components on the panel
	 * 
	 */
	private void doLayout() {
		panel = new JPanel(new BorderLayout());
		
		JLabel topLabel = new JLabel("Choose a Guest to make a reservation for");
		
		selectButton = new JButton("Select User");
		backButton = new JButton("Back to Previous");
		
		JPanel bottomPanel = new JPanel();
		bottomPanel.add(selectButton);
		bottomPanel.add(backButton);
		
		userChoose = new JComboBox<User>();
		JPanel middlePanel = new JPanel();
		middlePanel.add(userChoose);
		refreshUsers();
		
		userChoose.addActionListener(l -> {
			User firstItem = userChoose.getItemAt(0);
			if (firstItem == null) {
				selectButton.setEnabled(false);
			} else if (firstItem.getUsername().equals(SELECT_USER.getUsername())) {
				if (userChoose.getSelectedIndex() == 0) {
					selectButton.setEnabled(false);
				} else {
					selectButton.setEnabled(true);
					userChoose.removeItemAt(0);
				}
			} else {
				selectButton.setEnabled(true);
			}
		});
		
		panel.add(topLabel, BorderLayout.NORTH);
		panel.add(middlePanel, BorderLayout.CENTER);
		panel.add(bottomPanel, BorderLayout.SOUTH);
	}
	
	/**Refreshes the users based on the users in the user services
	 * 
	 */
	private void refreshUsers() {
		Set<User> rawUsers = userServices.getAllGuests();
		List<User> users = new ArrayList<User>(rawUsers);
		
		Collections.sort(users);
		
		userChoose.removeAllItems();
		
		userChoose.addItem(SELECT_USER);
		for (User user : users) {
			userChoose.addItem(user);
		}
		userChoose.revalidate();
		userChoose.repaint();
	}
	
	@Override
	public JPanel getFullPanel() {
		refreshUsers();
		return panel;
	}
	
	public JButton getBackButton() {
		return this.backButton;
	}
	
	public JButton getSelectButton() {
		return this.selectButton;
	}
	
	public User getSelectedUser() {
		return (User) userChoose.getSelectedItem();
	}

	public void selectGuest(User guest) {
		for (int i = 0; i < userChoose.getItemCount(); i++) {
			User user = userChoose.getItemAt(i);
			if (user.getUsername().equals(guest.getUsername())) {
				userChoose.setSelectedItem(user);
				break;
			}
		}
	}

}
