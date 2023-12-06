package edu.baylor.gitawayHotel.controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.Set;

import javax.management.InstanceAlreadyExistsException;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import edu.baylor.gitawayHotel.Room.Room;
import edu.baylor.gitawayHotel.Room.RoomServices;
import edu.baylor.gitawayHotel.gui.AdminGui;
import edu.baylor.gitawayHotel.gui.AuthenticatedGui;
import edu.baylor.gitawayHotel.gui.ChangeCredentialGui;
import edu.baylor.gitawayHotel.gui.ClerkGui;
import edu.baylor.gitawayHotel.gui.CredentialGui;
import edu.baylor.gitawayHotel.gui.GuestGui;
import edu.baylor.gitawayHotel.gui.GuestMakeReservationGui;
import edu.baylor.gitawayHotel.gui.IGui;
import edu.baylor.gitawayHotel.gui.MainFrame;
import edu.baylor.gitawayHotel.gui.GuestViewReservationGui;
import edu.baylor.gitawayHotel.gui.SplashScreen;
import edu.baylor.gitawayHotel.gui.ViewRoomStateGui;
import edu.baylor.gitawayHotel.gui.ClerkChangeRoomsGui;
import edu.baylor.gitawayHotel.reservation.Reservation;
import edu.baylor.gitawayHotel.reservation.ReservationService;
import edu.baylor.gitawayHotel.user.User;
import edu.baylor.gitawayHotel.user.UserServices;
import edu.baylor.gitawayHotel.user.UserType;

/**
 * A class that manages the UI screen paging and handling from one screen to the
 * next
 * 
 * @author Nathan
 *
 */
public class MainController {
	static final String GUEST_CREATED = "Guest user account successfully created with username: ";
	static final String INVALID_CREDENTIALS_NOTIFICATION = "Invalid credentials. Please try again.";
	static final String USERNAME_EXISTS = "Username already exists.\nPlease choose another username";
	static final int RESERVATION_GRACE_DAYS = 2;
	static final String FEE_TO_CANCEL = "The reservation was created more than " + RESERVATION_GRACE_DAYS
			+ " days ago and will incure a fee for cancellation.\nPlease click yes to accept the charge and finalize your cancellation.";
	private final SplashScreen splashScreen;
	private final CredentialGui loginGui;
	private final MainFrame mainFrame;
	private final UserServices userServices;
	private final ChangeCredentialGui changeCredentialGui;

	private final ReservationService reservationService;
	private final GuestViewReservationGui reservationGui;

	private final AdminGui adminGui;
	private final ClerkGui clerkGui;
	private final GuestGui guestGui;
	private final RoomServices roomServices;

	private Reservation lastReservation;
	private ViewRoomStateGui viewRoomStateGui;

	private final ClerkChangeRoomsGui clerkChangeRoomsGui;
	private GuestMakeReservationGui guestMakeReservationGui;
	private User user;

	public MainController(RoomServices roomServices) {
		this(new MainFrame(), new SplashScreen(), new CredentialGui(), new UserServices(), roomServices,
				new ChangeCredentialGui(), new ClerkChangeRoomsGui(roomServices), new ReservationService(roomServices));
	}

	public MainController(MainFrame mainFrame, SplashScreen splashScreen, CredentialGui loginGui,
			UserServices userServices, RoomServices roomServices, ChangeCredentialGui changeCredentialGui,
			ClerkChangeRoomsGui viewRoomsGui, ReservationService reservationService) {
		this.mainFrame = mainFrame;
		this.splashScreen = splashScreen;
		this.loginGui = loginGui;
		this.changeCredentialGui = changeCredentialGui;
		this.clerkChangeRoomsGui = viewRoomsGui;
		this.reservationService = reservationService;

		this.adminGui = new AdminGui();
		this.clerkGui = new ClerkGui();
		this.guestGui = new GuestGui();
		this.reservationGui = new GuestViewReservationGui(reservationService);

		this.userServices = userServices;
		this.roomServices = roomServices;
		this.viewRoomStateGui = new ViewRoomStateGui(roomServices, reservationService);

		this.guestMakeReservationGui = new GuestMakeReservationGui(roomServices);

		mainFrame.add(splashScreen.getPanel());
		SwingUtilities.invokeLater(() -> {
			splashScreen.getNextButton().requestFocus();
		});

		setupLoginPaging();

		setupLoggedInPaging();
	}

	/**
	 * Sets up the handling from clicking finish on one page
	 * 
	 */
	private void setupLoginPaging() {
		setupSplashscreenActions();

		setupLoginActions();
	}

	/**
	 * Sets up the paging for logged in pages
	 * 
	 */
	private void setupLoggedInPaging() {
		setupModificationActions();
	}

	/**
	 * Adds action handling for buttons on the splash screen
	 * 
	 */
	private void setupSplashscreenActions() {
		// when the user is on the main screen and clicks next
		JButton mainNext = splashScreen.getNextButton();
		mainNext.addActionListener(e -> {
			// splash screen redirects to the login gui
			mainFrame.add(loginGui.getFullPanel());
			SwingUtilities.invokeLater(() -> {
				loginGui.setTextFieldFocused();
			});
		});
	}

	/**
	 * Adds actions for the login action section
	 * 
	 */
	private void setupLoginActions() {
		// when the user is on the login screen and clicks login
		JButton loginNext = loginGui.getLoginButton();
		loginNext.addActionListener(e -> {
			// get the credentials from the gui
			String username = loginGui.getUsername();
			String password = loginGui.getPassword();
			boolean authenticated = userServices.isSuccessfulLogin(username, password);

			if (!authenticated) {
				JOptionPane.showMessageDialog(mainFrame.getFrame(), INVALID_CREDENTIALS_NOTIFICATION,
						"Authentication Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			UserType userType = userServices.getUserType(username);
			
			// construct guis for the user
			adminGui.setUsername(username);
			clerkGui.setUsername(username);
			guestGui.setUsername(username);
			guestMakeReservationGui.setUserType(userType);
			
			// login redirects to the specific user pages
			switch (userType) {
			case ADMIN:
				setupAdminActions();
				mainFrame.add(adminGui.getFullPanel());
				break;
			case HOTEL_CLERK:
				setupClerkActions();
				mainFrame.add(clerkGui.getFullPanel());
				reservationGui.setUser(userServices.getUser(username));
				break;
			case GUEST:
				setupGuestActions();
				reservationGui.setUser(new User(username));
				mainFrame.add(guestGui.getFullPanel());
				break;
			}
			setupRoomsActions();
		});

		// When the user is on the login screen and clicks Create Guest Account
		JButton createGuest = loginGui.getCreateGuestButton();
		createGuest.addActionListener(e -> {
			createGuestAccount();
		});
	}

	/**
	 * Creates a clerk from the admin
	 * 
	 */
	private void createGuestAccount() {
		String username = loginGui.getUsername();
		String password = loginGui.getPassword();

		boolean usernameAvailable = userServices.isUsernameAvailable(username);
		if (!usernameAvailable) {
			JOptionPane.showMessageDialog(mainFrame.getFrame(), USERNAME_EXISTS, "Creation Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		try {
			userServices.addUser(username, password, UserType.GUEST);
		} catch (InstanceAlreadyExistsException e1) {
			e1.printStackTrace();
		}
		JOptionPane.showMessageDialog(mainFrame.getFrame(), GUEST_CREATED + username, "Successful Account Creation",
				JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	 * Sets up the admin action event handling
	 * 
	 */
	private void setupAdminActions() {
		JButton adminAddClerk = adminGui.getCreateUserButton();
		adminAddClerk.addActionListener(e -> {
			createClerk();
		});

		JButton adminLogout = adminGui.getLogoutButton();
		adminLogout.addActionListener(e -> {
			logoutUser(adminGui);
		});

		JButton adminModify = adminGui.getModifyButton();
		adminModify.addActionListener(e -> {
			modifyCredentials(adminGui);
		});
	}

	/**
	 * Creates a clerk
	 * 
	 */
	private void createClerk() {
		String username = adminGui.getUsername();
		String password = adminGui.getPassword();

		boolean usernameAvailable = userServices.isUsernameAvailable(username);
		if (!usernameAvailable) {
			JOptionPane.showMessageDialog(mainFrame.getFrame(), USERNAME_EXISTS, "Creation Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		try {
			userServices.addUser(username, password, UserType.HOTEL_CLERK);
		} catch (InstanceAlreadyExistsException e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * Sets up the clerk actions
	 * 
	 */
	private void setupClerkActions() {
		JButton clerkLogout = clerkGui.getLogoutButton();
		clerkLogout.addActionListener(e -> {
			logoutUser(clerkGui);
		});

		JButton clerkModify = clerkGui.getModifyButton();
		clerkModify.addActionListener(e -> {
			modifyCredentials(clerkGui);
		});

		JButton viewRoomsButton = clerkGui.getModifyRoomsButton();
		viewRoomsButton.addActionListener(e -> {
			mainFrame.add(clerkChangeRoomsGui.getFullPanel());
		});

		clerkGui.getViewRoomStatusButton().addActionListener(e -> {
			mainFrame.add(viewRoomStateGui.getFullPanel());
		});

		viewRoomStateGui.getBackButton().addActionListener(e -> {
			mainFrame.add(clerkGui.getFullPanel());
		});

		clerkGui.getViewReservationsButton().addActionListener(l -> {
			mainFrame.add(reservationGui.getFullPanel());
		});
		
		JButton back = reservationGui.getBackButton();
		for (ActionListener listener : back.getActionListeners()) {
			back.removeActionListener(listener);
		}
		back.addActionListener(l -> {
			mainFrame.add(clerkGui.getFullPanel());
		});
	}

	/**
	 * Sets up the guest actions
	 * 
	 */
	private void setupGuestActions() {
		JButton guestLogout = guestGui.getLogoutButton();
		guestLogout.addActionListener(e -> {
			logoutUser(guestGui);
		});

		JButton guestModify = guestGui.getModifyButton();
		guestModify.addActionListener(e -> {
			modifyCredentials(guestGui);
		});

		JButton viewRoomsButton = guestGui.getViewRoomsButton();
		viewRoomsButton.addActionListener(e -> {
			guestMakeReservationGui.setStartDate(null);
			guestMakeReservationGui.setEndDate(null);
			mainFrame.add(guestMakeReservationGui.getFullPanel());
		});

		JButton viewReservations = guestGui.getViewReservationsButton();
		viewReservations.addActionListener(e -> {
			mainFrame.add(reservationGui.getFullPanel());
		});

		JButton modifyReservations = reservationGui.getModifyReservationButton();
		modifyReservations.addActionListener(e -> {

			// save the last reservation and provide it as modification for the user
			Reservation reservation = reservationGui.getSelectedReservation();
			lastReservation = reservation;
			guestMakeReservationGui.setStartDate(reservation.getStartDate());
			guestMakeReservationGui.setEndDate(reservation.getEndDate());
			reservationService.removeReservation(reservation);
			guestMakeReservationGui.getSearchButton().doClick();

			mainFrame.add(guestMakeReservationGui.getFullPanel());
		});

		JButton cancelReservation = reservationGui.getCancelReservationButton();
		cancelReservation.addActionListener(e -> {

			Reservation reservation = reservationGui.getSelectedReservation();
			if (LocalDate.now().minusDays(RESERVATION_GRACE_DAYS).isAfter(reservation.getDateReservationMade())) {
				int selected = JOptionPane.showConfirmDialog(mainFrame.getFrame(), FEE_TO_CANCEL,
						"Cancellation Warning", JOptionPane.WARNING_MESSAGE + JOptionPane.YES_NO_OPTION);
				if (!(JOptionPane.OK_OPTION == selected)) {
					return;
				}
			}
			reservationService.removeReservation(reservation);
			mainFrame.add(reservationGui.getFullPanel());
		});
		
		JButton back = reservationGui.getBackButton();
		for (ActionListener listener : back.getActionListeners()) {
			back.removeActionListener(listener);
		}
		back.addActionListener(l -> {
			mainFrame.add(guestGui.getFullPanel());
		});
	}

	/**
	 * Logs the active user out
	 * 
	 * @param activePanel the current panel this was called from
	 */
	private void logoutUser(IGui iGui) {
		// logout redirects to the login screen
		mainFrame.add(loginGui.getFullPanel());
		user = null;
	}

	// Redirects to ChangeCredentialsGui.java
	private void modifyCredentials(IGui iGui) {
		changeCredentialGui.setUsername(loginGui.getUsername());
		mainFrame.add(changeCredentialGui.getFullPanel());

	}

	private void setupModificationActions() {
		JButton changePassword = changeCredentialGui.getLoginButton();
		changePassword.addActionListener(e -> {
			String username = changeCredentialGui.getUsername();
			String password = changeCredentialGui.getPassword();
			String newPassword = changeCredentialGui.getNewPassword();
			boolean authenticated = userServices.isSuccessfulLogin(username, password);

			if (!authenticated) {
				JOptionPane.showMessageDialog(mainFrame.getFrame(), INVALID_CREDENTIALS_NOTIFICATION,
						"Authentication Error", JOptionPane.ERROR_MESSAGE);
				return;
			}

			userServices.updateUser(username, newPassword);

			UserType userType = userServices.getUserType(username);

			// construct guis for the user
			adminGui.setUsername(username);
			clerkGui.setUsername(username);
			guestGui.setUsername(username);

			// login redirects to the specific user pages
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
		});
	}

	/**
	 * Action handling for the room modification gui
	 * 
	 */
	private void setupRoomsActions() {
		JButton saveRoomsButton = clerkChangeRoomsGui.getSaveRoomsButton();
		saveRoomsButton.addActionListener(e -> {
			saveRooms();
		});

		JButton backButton = clerkChangeRoomsGui.getBackButton();
		backButton.addActionListener(e -> {
			backFromRoomGui();
		});
		
		backButton = guestMakeReservationGui.getBackButton();
		backButton.addActionListener(l -> {
			backFromRoomGui();
		});

		JButton searchButton = guestMakeReservationGui.getSearchButton();
		searchButton.addActionListener(e -> {
			handleSearch();
		});

		JButton reserveButton = guestMakeReservationGui.getReserveRoomButton();
		reserveButton.addActionListener(e -> {
			handleReservationRequest();
		});

		JButton remove = clerkChangeRoomsGui.getRemoveRoomButton();
		remove.addActionListener(e -> {
			handleRemoveRoom();
		});

		JButton add = clerkChangeRoomsGui.getAddRoomButton();
		add.addActionListener(e -> {
			handleAddRoom();
		});
	}

	/**
	 * saves the rooms
	 * 
	 */
	private void saveRooms() {
		roomServices.saveRooms(clerkChangeRoomsGui.getRoomsInTable());
	}

	/**
	 * handles the back from rooms gui screen
	 * 
	 */
	private void backFromRoomGui() {
		UserType userType = guestMakeReservationGui.getUserType();
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
			if (lastReservation != null) {
				reservationService.addReservation(lastReservation);
				lastReservation = null;
			}
			break;
		}
	}

	/**
	 * Handles the search by refreshing the table
	 * 
	 */
	private void handleSearch() {
		Set<Room> availableRooms = reservationService.getAvailableRooms(guestMakeReservationGui.getStartDate(),
				guestMakeReservationGui.getEndDate());
		guestMakeReservationGui.setFilteredRooms(availableRooms);
	}

	/**
	 * Handles the request to create a reservation
	 * 
	 */
	private void handleReservationRequest() {
		int desiredRoomNum = guestMakeReservationGui.getDesiredRoomReservation();
		LocalDate startDate = guestMakeReservationGui.getStartDate();
		LocalDate endDate = guestMakeReservationGui.getEndDate();

		String username = loginGui.getUsername();
		User user = new User(username);
		Room room = roomServices.getRoomByNumber(desiredRoomNum);
		Reservation res = new Reservation(startDate, endDate, user, room);
		res.setDateReservationMade(LocalDate.now());

		reservationService.addReservation(res);
		lastReservation = null;

		// go back to the previous screen after this
		JOptionPane.showMessageDialog(mainFrame.getFrame(), "Reservation Successfully Created", "Reservation Created",
				JOptionPane.INFORMATION_MESSAGE);
		JButton backButton = guestMakeReservationGui.getBackButton();
		backButton.doClick();
	}

	/**
	 * Handles the room removal request by the clerk
	 * 
	 */
	private void handleRemoveRoom() {
		JTextField field = clerkChangeRoomsGui.getRoomUpdateField();
		roomServices.removeRoom(Integer.parseInt(field.getText()));
		clerkChangeRoomsGui.updateModel();
	}

	/**
	 * Handles the room add request by the clerk
	 * 
	 */
	private void handleAddRoom() {
		JTextField field = clerkChangeRoomsGui.getRoomUpdateField();
		Room defaultRoom = new Room();
		defaultRoom.setBedQty(1);
		defaultRoom.setBedType("queen");
		defaultRoom.setNoSmoking(true);
		defaultRoom.setRoom(Integer.parseInt(field.getText()));
		roomServices.addRoom(defaultRoom);
		clerkChangeRoomsGui.updateModel();
	}

	/**
	 * @return the splashScreen
	 */
	SplashScreen getSplashScreen() {
		return splashScreen;
	}

	/**
	 * @return the loginGui
	 */
	CredentialGui getLoginGui() {
		return loginGui;
	}

	/**
	 * @return the mainFrame
	 */
	MainFrame getMainFrame() {
		return mainFrame;
	}

	/**
	 * @return the userServices
	 */
	UserServices getUserServices() {
		return userServices;
	}

	/**
	 * @return the changeCredentialGui
	 */
	ChangeCredentialGui getChangeCredentialGui() {
		return changeCredentialGui;
	}

	/**
	 * @return the viewRoomsGui
	 */
	ClerkChangeRoomsGui getViewRoomsGui() {
		return clerkChangeRoomsGui;
	}

	/**
	 * @return the reservationService
	 */
	ReservationService getReservationService() {
		return reservationService;
	}

	/**
	 * @return the reservationGui
	 */
	GuestViewReservationGui getReservationGui() {
		return reservationGui;
	}

	/**
	 * @return the adminGui
	 */
	AdminGui getAdminGui() {
		return adminGui;
	}

	/**
	 * @return the clerkGui
	 */
	ClerkGui getClerkGui() {
		return clerkGui;
	}

	/**
	 * @return the guestGui
	 */
	GuestGui getGuestGui() {
		return guestGui;
	}

	/**
	 * @return the lastReservation
	 */
	Reservation getLastReservation() {
		return lastReservation;
	}

	ViewRoomStateGui getViewRoomStateGui() {
		return viewRoomStateGui;
	}

	GuestMakeReservationGui getGuestMakeReservationGui() {
		return guestMakeReservationGui;
	}
}
