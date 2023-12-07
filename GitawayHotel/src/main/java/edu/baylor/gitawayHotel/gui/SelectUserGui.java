package edu.baylor.gitawayHotel.gui;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import edu.baylor.gitawayHotel.user.User;
import edu.baylor.gitawayHotel.user.UserServices;
import edu.baylor.gitawayHotel.user.UserType;

/**Allows a user to select other users
 * @author nwoolley
 *
 */
public class SelectUserGui implements IGui {
	private static final User SELECT_USER = new User("--Select User--");
	private JPanel panel;
	private UserServices userServices;
	
	private JButton selectButton;
	private JButton backButton;
	private JComboBox<User> userChoose;
	private JLabel topLabel;
	private List<UserType> userTypes = List.of();
	
	public SelectUserGui(UserServices userServices) {
		this.userServices = userServices;
		doLayout();
	}
	
	public void setTopText(String topText) {
		this.topLabel.setText(topText);
	}
	
	public void setUserFilterList(List<UserType> userTypes) {
		this.userTypes = userTypes;
	}
	
	/**Performs the layout of the components on the panel
	 * 
	 */
	private void doLayout() {
		panel = new JPanel(new BorderLayout());
		
		topLabel = new JLabel("Choose a Guest to make a reservation for");
		
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
		Set<User> rawUsers = userServices.getAllUsers();
		rawUsers = rawUsers.stream()
				.filter(user -> userTypes.contains(user.getUserType()))
				.collect(Collectors.toSet());
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

	public void selectUser(User user) {
		for (int i = 0; i < userChoose.getItemCount(); i++) {
			User current = userChoose.getItemAt(i);
			if (current.getUsername().equals(user.getUsername())) {
				userChoose.setSelectedItem(current);
				break;
			}
		}
	}

}
