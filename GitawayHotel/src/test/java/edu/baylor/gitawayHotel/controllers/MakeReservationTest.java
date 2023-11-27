package edu.baylor.gitawayHotel.controllers;

import edu.baylor.gitawayHotel.Room.Room;
import edu.baylor.gitawayHotel.Room.RoomServices;
import edu.baylor.gitawayHotel.reservation.Reservation;
import edu.baylor.gitawayHotel.reservation.ReservationService;
import edu.baylor.gitawayHotel.gui.AdminGui;
import edu.baylor.gitawayHotel.gui.ChangeCredentialGui;
import edu.baylor.gitawayHotel.gui.CredentialGui;
import edu.baylor.gitawayHotel.gui.ReservationGui;
import edu.baylor.gitawayHotel.gui.ViewRoomsGui;
import edu.baylor.gitawayHotel.reservation.Reservation;
import edu.baylor.gitawayHotel.user.User;
import edu.baylor.gitawayHotel.user.UserServices;
import edu.baylor.gitawayHotel.user.UserType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.management.InstanceAlreadyExistsException;
import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;
import java.time.LocalDate;

//@Execution(ExecutionMode.CONCURRENT) //disabled to allow the passwords to be modified
public class MakeReservationTest {
	private static final String DEFAULT_PW = "password";
	private static RoomServices roomServices = new RoomServices();
	private static ReservationService reservationService = new ReservationService(roomServices);
	private static User ADMIN = new User("admin", DEFAULT_PW, UserType.ADMIN);
	private static User CLERK = new User("clerk", DEFAULT_PW, UserType.HOTEL_CLERK);
	private static User GUEST = new User("Guest", DEFAULT_PW, UserType.GUEST);
	LocalDate today = LocalDate.now();

	private static final Set<User> TEST_USERS= Set.of(ADMIN, CLERK, GUEST);
	
	@BeforeAll
	static void addUsersIfNotPresent() {
		UserServices userServices = new UserServices();
		for (User user : TEST_USERS) {
			if (!userServices.isUsernameValid(user.getUsername())) {
				try {
					userServices.addUser(user.getUsername(), user.getPassword(), user.getUserType());
				} catch (InstanceAlreadyExistsException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	@Test
	void testMainConstructor() {
		MainController mainController = new MainController(roomServices);
		
		Assertions.assertNotNull(mainController);
		
		closeApp(mainController);
	}

	static void login(MainController mainController, User user) {
		mainController.getSplashScreen().getNextButton().doClick();
		mainController.getLoginGui().setUsername(user.getUsername());
		mainController.getLoginGui().setPassword(user.getPassword());
		mainController.getLoginGui().getLoginButton().doClick();
	}

	@Test
	void testGuestRoomView() {
		MainController mainController = new MainController(roomServices);
		login(mainController, GUEST);

		mainController.getGuestGui().getViewRoomsButton().doClick();
		try {
			SwingUtilities.invokeAndWait(() -> {
				JPanel activePanel = mainController.getViewRoomsGui().getFullPanel();
				Assertions.assertEquals(activePanel, mainController.getMainFrame().getActivePanel());
				mainController.getViewRoomsGui().getBackButton().doClick();
			});

			SwingUtilities.invokeAndWait(() -> {
				JPanel activePanel = mainController.getGuestGui().getFullPanel();
				Assertions.assertEquals(activePanel, mainController.getMainFrame().getActivePanel());
			});
		} catch (InvocationTargetException | InterruptedException e) {
			e.printStackTrace();
			Assertions.fail("Exception thrown during execution");
		}

		closeApp(mainController);
	}

	@Test
	void testGuestMakeReservation() {
		MainController mainController = new MainController(roomServices);
		mainController.getSplashScreen().getNextButton().doClick();
		login(mainController, GUEST);

		mainController.getGuestGui().getViewRoomsButton().doClick();
		ViewRoomsGui viewRoomsGui = mainController.getViewRoomsGui();

		NotificationWindowLaunch listener = new NotificationWindowLaunch();
		Toolkit.getDefaultToolkit().addAWTEventListener(listener, AWTEvent.WINDOW_EVENT_MASK);

		try {
			SwingUtilities.invokeAndWait(() -> {
				JPanel activePanel = mainController.getViewRoomsGui().getFullPanel();

				viewRoomsGui.setStartDate(today.plusDays(1));
				viewRoomsGui.setEndDate(today.plusDays(2));
				viewRoomsGui.getSearchButton().doClick();
				viewRoomsGui.selectTableRowByIndex(0);
				viewRoomsGui.getReserveRoomButton().doClick();

				mainController.getMainFrame().getFrame().dispose();

				Reservation newReservation = reservationService.getReservationsByUser(GUEST).get(0);
				Assertions.assertNotNull(newReservation);
			});

		} catch (InvocationTargetException | InterruptedException e) {
			e.printStackTrace();
			Assertions.fail("Exception thrown during execution");
		}

		closeApp(mainController);
	}

	@Test
	void testGuestChangeReservation() {
		MainController mainController = new MainController(roomServices);
		mainController.getSplashScreen().getNextButton().doClick();
		login(mainController, GUEST);

		mainController.getGuestGui().getViewReservationsButton().doClick();
		ReservationGui reservationGui = mainController.getReservationGui();

		NotificationWindowLaunch listener = new NotificationWindowLaunch();
		Toolkit.getDefaultToolkit().addAWTEventListener(listener, AWTEvent.WINDOW_EVENT_MASK);

		try {
			SwingUtilities.invokeAndWait(() -> {
				JPanel activePanel = mainController.getViewRoomsGui().getFullPanel();

				reservationGui.selectTableRowByIndex(0);
				reservationGui.getModifyReservationButton().doClick();

				ViewRoomsGui viewRoomsGui = mainController.getViewRoomsGui();

				viewRoomsGui.setStartDate(today.plusDays(1));
				viewRoomsGui.setEndDate(today.plusDays(2));
				viewRoomsGui.getSearchButton().doClick();
				viewRoomsGui.selectTableRowByIndex(0);
				viewRoomsGui.getReserveRoomButton().doClick();

				mainController.getMainFrame().getFrame().dispose();

				Reservation newReservation = reservationService.getReservationsByUser(GUEST).get(0);
				Assertions.assertNotNull(newReservation);
			});

		} catch (InvocationTargetException | InterruptedException e) {
			e.printStackTrace();
			Assertions.fail("Exception thrown during execution");
		}


		closeApp(mainController);
	}


	private static void closeApp(MainController main) {
		main.getMainFrame().getFrame().dispose();
	}
	
	@AfterAll
	static void resetPasswords() {
		MainController mainController = new MainController(roomServices);
		UserServices userServices = mainController.getUserServices();
		
		ChangeCredentialGui credentialGui = mainController.getChangeCredentialGui();
		Set<User> users = Set.of(ADMIN, CLERK, GUEST);
		
		for (User user : users) {
			credentialGui.setUsername(user.getUsername());
			credentialGui.setCurrentPassword(user.getPassword());
			credentialGui.setNewPassword(DEFAULT_PW);
			try {
				SwingUtilities.invokeAndWait(() -> {
					credentialGui.getLoginButton().doClick();
					User diskuser = userServices.getUser(user.getUsername());
					Assertions.assertEquals(DEFAULT_PW, diskuser.getPassword());
				});
			} catch (InvocationTargetException | InterruptedException e) {
				e.printStackTrace();
			} 
		}
	}

	@AfterAll
	static void resetReservations() {
		MainController mainController = new MainController(roomServices);
		UserServices userServices = mainController.getUserServices();

		ReservationService reservationService = mainController.getReservationService();

		for (Reservation reservation : reservationService.getReservationsByUser(GUEST)) {
			reservationService.removeReservation(reservation);
		}
	}
	
}
