package edu.baylor.gitawayHotel.controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.Set;

import javax.management.InstanceAlreadyExistsException;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import edu.baylor.gitawayHotel.Room.Room;
import edu.baylor.gitawayHotel.Room.RoomServices;
import edu.baylor.gitawayHotel.gui.AdminGui;
import edu.baylor.gitawayHotel.gui.ChangeCredentialGui;
import edu.baylor.gitawayHotel.gui.ClerkGui;
import edu.baylor.gitawayHotel.gui.CredentialGui;
import edu.baylor.gitawayHotel.gui.ViewRoomsGui;
import edu.baylor.gitawayHotel.reservation.Reservation;
import edu.baylor.gitawayHotel.reservation.ReservationService;
import edu.baylor.gitawayHotel.gui.GuestGui;
import edu.baylor.gitawayHotel.gui.IGui;
import edu.baylor.gitawayHotel.gui.MainFrame;
import edu.baylor.gitawayHotel.gui.SplashScreen;
import edu.baylor.gitawayHotel.user.User;
import edu.baylor.gitawayHotel.user.UserServices;
import edu.baylor.gitawayHotel.user.UserType;


/**A class that manages the UI screen paging and handling from one screen to the next
 * @author Nathan
 *
 */
public class MainController {
	private final SplashScreen splashScreen;
	private final CredentialGui loginGui;
	private final MainFrame mainFrame;
	private final UserServices userServices;
	private final ChangeCredentialGui changeCredentialGui;
	private final ViewRoomsGui viewRoomsGui;
	private final ReservationService reservationService;
	
	private final AdminGui adminGui;
	private final ClerkGui clerkGui;
	private final GuestGui guestGui;
	private final RoomServices roomServices;

	public MainController(
			MainFrame mainFrame, 
			SplashScreen splashScreen, 
			CredentialGui loginGui, 
			UserServices userServices,
			RoomServices roomServices, 
			ChangeCredentialGui changeCredentialGui,
			ViewRoomsGui viewRoomsGui,
			ReservationService reservationService
			) {
		this.mainFrame = mainFrame;
		this.splashScreen = splashScreen;
		this.loginGui = loginGui;
		this.changeCredentialGui = changeCredentialGui;
		this.viewRoomsGui = viewRoomsGui;
		this.reservationService = reservationService;
		
		this.adminGui = new AdminGui();
		this.clerkGui = new ClerkGui();
		this.guestGui = new GuestGui();
		
		this.userServices = userServices;
		this.roomServices = roomServices;
		
		mainFrame.add(splashScreen.getPanel());
		
		setupLoginPaging();
		
		setupLoggedInPaging();
	}

	/**Sets up the handling from clicking finish on one page
	 * 
	 */
	private void setupLoginPaging() {
		setupSplashscreenActions();
		
		setupLoginActions();
	}
	
	/**Sets up the paging for logged in pages
	 * 
	 */
	private void setupLoggedInPaging() {
		setupModificationActions();
	}

	/**Adds action handling for buttons on the splash screen
	 * 
	 */
	private void setupSplashscreenActions() {
		//when the user is on the main screen and clicks next
		JButton mainNext = splashScreen.getNextButton();
		mainNext.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				//splash screen redirects to the login gui
				mainFrame.add(loginGui.getFullPanel());	
			}
			
		});
	}
	
	/**Adds actions for the login action section
	 * 
	 */
	private void setupLoginActions() {
		//when the user is on the login screen and clicks login
		JButton loginNext = loginGui.getLoginButton();
		loginNext.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				//get the credentials from the gui
				String username = loginGui.getUsername();
				String password = loginGui.getPassword();
				boolean authenticated = userServices.isSuccessfulLogin(username, password);
				
				if (!authenticated) {
					JOptionPane.showMessageDialog(mainFrame.getFrame(), "Invalid credentials. Please try again.", "Authentication Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				UserType userType = userServices.getUserType(username);
				
				//construct guis for the user
				adminGui.setUsername(username);
				clerkGui.setUsername(username);
				guestGui.setUsername(username);
				viewRoomsGui.setUserType(userType);
				
				//login redirects to the specific user pages
				switch (userType) {
					case ADMIN:
						setupAdminActions();
						mainFrame.add(adminGui.getFullPanel());
						break;
					case HOTEL_CLERK:
						setupClerkActions();
						mainFrame.add(clerkGui.getFullPanel());
						break;
					case GUEST:
						setupGuestActions();
						mainFrame.add(guestGui.getFullPanel());
						break;
				}
				setupRoomsActions();
			}
			
		});
		
		//When the user is on the login screen and clicks Create Guest Account
		JButton createGuest = loginGui.getCreateGuestButton();
		createGuest.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String username = loginGui.getUsername();
				String password = loginGui.getPassword();
				
				boolean usernameAvailable = userServices.isUsernameAvailable(username);
				if (!usernameAvailable) {
					JOptionPane.showMessageDialog(mainFrame.getFrame(), "Username already exists.\nPlease choose another username", "Creation Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				try {
					userServices.addUser(username, password, UserType.GUEST);
				} catch (InstanceAlreadyExistsException e1) {
					e1.printStackTrace();
				}
				JOptionPane.showMessageDialog(mainFrame.getFrame(), "Guest user account " + username + " successfully created", "Successful Account Creation", JOptionPane.INFORMATION_MESSAGE);
			}
		});
	}
	
	/**Sets up the admin action event handling
	 * 
	 */
	private void setupAdminActions() {
		JButton adminAddClerk = adminGui.getCreateUserButton();
		adminAddClerk.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String username = adminGui.getUsername();
				String password = adminGui.getPassword();
				
				boolean usernameAvailable = userServices.isUsernameAvailable(username);
				if (!usernameAvailable) {
					JOptionPane.showMessageDialog(mainFrame.getFrame(), "Username already exists.\nPlease choose another username", "Creation Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				try {
					userServices.addUser(username, password, UserType.HOTEL_CLERK);
				} catch (InstanceAlreadyExistsException e1) {
					e1.printStackTrace();
				}
			}
			
		});
		
		JButton adminLogout = adminGui.getLogoutButton();
		adminLogout.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				logoutUser(adminGui);
			}
			
		});
		
		JButton adminModify = adminGui.getModifyButton();
		adminModify.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				modifyCredentials(adminGui);
			}
			
		});
	}
	
	/**Sets up the clerk actions
	 * 
	 */
	private void setupClerkActions() {
		JButton clerkLogout = clerkGui.getLogoutButton();
		clerkLogout.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				logoutUser(clerkGui);
			}
			
		});
		
		JButton clerkModify = clerkGui.getModifyButton();
		clerkModify.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				modifyCredentials(clerkGui);
			}
			
		});

		JButton viewRoomsButton = clerkGui.getViewRoomsButton();
		viewRoomsButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e ) {
				mainFrame.add(viewRoomsGui.getFullPanel());
			}
		});
	}
	
	/**Sets up the guest actions
	 * 
	 */
	private void setupGuestActions() {
		JButton guestLogout = guestGui.getLogoutButton();
		guestLogout.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				logoutUser(guestGui);
			}
		});
		
		JButton guestModify = guestGui.getModifyButton();
		guestModify.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				modifyCredentials(guestGui);
			}
		});

		JButton viewRoomsButton = guestGui.getViewRoomsButton();
		viewRoomsButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e ) {
				mainFrame.add(viewRoomsGui.getFullPanel());
			}
		});
	}
	
	/**Logs the active user out
	 * @param activePanel the current panel this was called from
	 */
	private void logoutUser(IGui iGui) {
		//logout redirects to the login screen
		mainFrame.add(loginGui.getFullPanel());
	}
	
	// Redirects to ChangeCredentialsGui.java
	private void modifyCredentials(IGui iGui) {
		changeCredentialGui.setUsername(loginGui.getUsername());
		mainFrame.add(changeCredentialGui.getFullPanel());
		
	}
	
	private void setupModificationActions() {
		JButton changePassword = changeCredentialGui.getLoginButton();
		changePassword.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String username = changeCredentialGui.getUsername();
				String password = changeCredentialGui.getPassword();
				String newPassword = changeCredentialGui.getNewPassword();
				boolean authenticated = userServices.isSuccessfulLogin(username, password);
				
				if (!authenticated) {
					JOptionPane.showMessageDialog(mainFrame.getFrame(), "Invalid credentials. Please try again.", "Authentication Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				userServices.updateUser(username, newPassword);
				
				UserType userType = userServices.getUserType(username);
				
				//construct guis for the user
				adminGui.setUsername(username);
				clerkGui.setUsername(username);
				guestGui.setUsername(username);
						
				//login redirects to the specific user pages
				switch (userType) {
				case ADMIN:
					mainFrame.add(adminGui.getFullPanel());
					break;
				case HOTEL_CLERK:
					mainFrame.add(clerkGui.getFullPanel());
					break;
				case GUEST:
					mainFrame.add(guestGui.getFullPanel());
					break;
				}
			}
		});
	}
	
	/**Action handling for the room modification gui
	 * 
	 */
	private void setupRoomsActions() {
		JButton saveRoomsButton = viewRoomsGui.getSaveRoomsButton();
		saveRoomsButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				roomServices.saveRooms(viewRoomsGui.getRoomsInTable());
			} 
			
		});
		
		JButton backButton = viewRoomsGui.getBackButton();
		backButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				UserType userType = viewRoomsGui.getUserType();
				// back button redirects to the specific user pages
				switch (userType) {
				case ADMIN:
					mainFrame.add(adminGui.getFullPanel());
					break;
				case HOTEL_CLERK:
					mainFrame.add(clerkGui.getFullPanel());
					break;
				case GUEST:
					mainFrame.add(guestGui.getFullPanel());
					break;
				}
			}
		});

		JButton searchButton = viewRoomsGui.getSearchButton();
		searchButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Set<Room> availableRooms = reservationService.getAvailableRooms(viewRoomsGui.getStartDate(), viewRoomsGui.getEndDate());
				viewRoomsGui.setFilteredRooms(availableRooms);
			}
		});
		
		JButton reserveButton = viewRoomsGui.getReserveRoomButton();
		reserveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int desiredRoomNum = viewRoomsGui.getDesiredRoomReservation();
				LocalDate startDate = viewRoomsGui.getStartDate();
				LocalDate endDate = viewRoomsGui.getEndDate();
				
				String username = loginGui.getUsername();
				User user = new User(username);
				Room room = roomServices.getRoomByNumber(desiredRoomNum);
				Reservation res = new Reservation(startDate, endDate, user, room);
				
				reservationService.addReservation(res);
				
				//go back to the previous screen after this
				JOptionPane.showMessageDialog(mainFrame.getFrame(), "Reservation Successfully Created", "Reservation Created", JOptionPane.INFORMATION_MESSAGE);
				backButton.doClick();
			}
		});
		
		JTextField field = viewRoomsGui.getRoomUpdateField();
		
		JButton remove = viewRoomsGui.getRemoveRoomButton();
		remove.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				roomServices.removeRoom(Integer.parseInt(field.getText()));
				viewRoomsGui.updateModel();
			}
			
		});
		
		JButton add = viewRoomsGui.getAddRoomButton();
		add.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Room defaultRoom = new Room();
				defaultRoom.setBedQty(1);
				defaultRoom.setBedType("queen");
				defaultRoom.setNoSmoking(true);
				defaultRoom.setRoom(Integer.parseInt(field.getText()));
				roomServices.addRoom(defaultRoom);
				viewRoomsGui.updateModel();
			}
		});
		
		
	}
}
