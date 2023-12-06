package edu.baylor.gitawayHotel.controllers;

import edu.baylor.gitawayHotel.Room.Room;
import edu.baylor.gitawayHotel.Room.RoomServices;
import edu.baylor.gitawayHotel.reservation.Reservation;
import edu.baylor.gitawayHotel.reservation.ReservationService;
import edu.baylor.gitawayHotel.gui.AdminGui;
import edu.baylor.gitawayHotel.gui.ChangeCredentialGui;
import edu.baylor.gitawayHotel.gui.ClerkGui;
import edu.baylor.gitawayHotel.gui.CredentialGui;
import edu.baylor.gitawayHotel.gui.GuestMakeReservationGui;
import edu.baylor.gitawayHotel.gui.ViewReservationGui;
import edu.baylor.gitawayHotel.gui.ViewRoomStateGui;
import edu.baylor.gitawayHotel.gui.ClerkChangeRoomsGui;
import edu.baylor.gitawayHotel.reservation.Reservation;
import edu.baylor.gitawayHotel.user.User;
import edu.baylor.gitawayHotel.user.UserServices;
import edu.baylor.gitawayHotel.user.UserType;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;

import javax.management.InstanceAlreadyExistsException;
import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.time.LocalDate;

//@Execution(ExecutionMode.CONCURRENT) //disabled to allow the passwords to be modified
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MakeReservationTest {
	private static final String DEFAULT_PW = "password";
	private static RoomServices roomServices = new RoomServices();
	private static User ADMIN = new User("admin", DEFAULT_PW, UserType.ADMIN);
	private static User CLERK = new User("testclerk", DEFAULT_PW, UserType.HOTEL_CLERK);
	private static User GUEST = new User("Guest", DEFAULT_PW, UserType.GUEST);
	LocalDate today = LocalDate.now();
	private static Room TEST_ROOM = new Room();

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
		
		TEST_ROOM.setRoom(999);
		TEST_ROOM.setBedQty(1);
		TEST_ROOM.setBedType("Queen");
		TEST_ROOM.setNoSmoking(true);
		roomServices.addRoom(TEST_ROOM);
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
	@Order(1)
	void testGuestMakeReservation() {
		MainController mainController = new MainController(roomServices);
		mainController.getSplashScreen().getNextButton().doClick();
		login(mainController, GUEST);

		mainController.getGuestGui().getViewRoomsButton().doClick();
		GuestMakeReservationGui makeReservationGui = mainController.getGuestMakeReservationGui();

		NotificationWindowLaunch listener = new NotificationWindowLaunch();
		Toolkit.getDefaultToolkit().addAWTEventListener(listener, AWTEvent.WINDOW_EVENT_MASK);

		try {
			SwingUtilities.invokeAndWait(() -> {
				JPanel activePanel = mainController.getViewRoomsGui().getFullPanel();

				makeReservationGui.setStartDate(today.plusDays(1));
				makeReservationGui.setEndDate(today.plusDays(2));
				makeReservationGui.getSearchButton().doClick();
				makeReservationGui.selectTableRowByIndex(0);
				makeReservationGui.selectTableRowByRoomNum(TEST_ROOM.getRoom());
				makeReservationGui.getReserveRoomButton().doClick();
			
				mainController.getMainFrame().getFrame().dispose();

				Reservation newReservation = mainController.getReservationService().getReservationsByUser(GUEST).get(0);
				Assertions.assertNotNull(newReservation);
			});

		} catch (InvocationTargetException | InterruptedException e) {
			e.printStackTrace();
			Assertions.fail("Exception thrown during execution");
		}

		closeApp(mainController);
	}

	@Test
	@Order(2)
	void testGuestChangeReservation() {
		MainController mainController = new MainController(roomServices);
		mainController.getSplashScreen().getNextButton().doClick();
		login(mainController, GUEST);

		mainController.getGuestGui().getViewReservationsButton().doClick();
		ViewReservationGui reservationGui = mainController.getReservationGui();

		NotificationWindowLaunch listener = new NotificationWindowLaunch();
		Toolkit.getDefaultToolkit().addAWTEventListener(listener, AWTEvent.WINDOW_EVENT_MASK);

		try {
			SwingUtilities.invokeAndWait(() -> {
				JPanel activePanel = mainController.getViewRoomsGui().getFullPanel();

				reservationGui.selectTableRowByRoomNum(TEST_ROOM.getRoom());
				reservationGui.getModifyReservationButton().doClick();

				GuestMakeReservationGui makeReservationGui = mainController.getGuestMakeReservationGui();


				makeReservationGui.setStartDate(today.plusDays(1));
				makeReservationGui.setEndDate(today.plusDays(2));
				makeReservationGui.getSearchButton().doClick();
				makeReservationGui.selectTableRowByRoomNum(TEST_ROOM.getRoom());
				makeReservationGui.getReserveRoomButton().doClick();

				mainController.getMainFrame().getFrame().dispose();

				Reservation newReservation = mainController.getReservationService().getReservationsByUser(GUEST).get(0);
				Assertions.assertNotNull(newReservation);
			});

		} catch (InvocationTargetException | InterruptedException e) {
			e.printStackTrace();
			Assertions.fail("Exception thrown during execution");
		}


		closeApp(mainController);
	}
	
	@Test
	@Order(3)
	void testClerkCheckinReservation() {
		MainController mainController = new MainController(roomServices);
		mainController.getSplashScreen().getNextButton().doClick();
		login(mainController, CLERK);

		List<Reservation> reservations = mainController.getReservationService().getReservationsForRoom(TEST_ROOM);
		Reservation expected = new Reservation(today.plusDays(1), today.plusDays(2), GUEST, TEST_ROOM);
		
		Assertions.assertIterableEquals(List.of(expected), reservations);
		Assertions.assertFalse(reservations.get(0).getCheckinStatus());
		
		ClerkGui clerkGui = mainController.getClerkGui();
		clerkGui.getViewRoomStatusButton().doClick();
		
		ViewRoomStateGui stateGui = mainController.getViewRoomStateGui();
		//check in
		stateGui.selectTableRowByRoomNum(TEST_ROOM.getRoom());
		stateGui.getCheckInButton().doClick();
		reservations = mainController.getReservationService().getReservationsForRoom(TEST_ROOM);
		Assertions.assertTrue(reservations.get(0).getCheckinStatus());
		
		//check out
		stateGui.selectTableRowByRoomNum(TEST_ROOM.getRoom());
		stateGui.getCheckOutButton().doClick();
		reservations = mainController.getReservationService().getReservationsForRoom(TEST_ROOM);
		Assertions.assertFalse(reservations.get(0).getCheckinStatus());
		
		closeApp(mainController);
	}
	
	@Test
	@Order(4)
	void testGuestRemoveReservation() {
		MainController mainController = new MainController(roomServices);
		mainController.getSplashScreen().getNextButton().doClick();
		login(mainController, GUEST);

		mainController.getGuestGui().getViewReservationsButton().doClick();
		ViewReservationGui reservationGui = mainController.getReservationGui();

		NotificationWindowLaunch listener = new NotificationWindowLaunch();
		Toolkit.getDefaultToolkit().addAWTEventListener(listener, AWTEvent.WINDOW_EVENT_MASK);
		
		try {
			SwingUtilities.invokeAndWait(() -> {
				reservationGui.selectTableRowByRoomNum(TEST_ROOM.getRoom());
				
				Reservation selectedRes = reservationGui.getSelectedReservation();
				Assertions.assertNotNull(selectedRes);
				
				reservationGui.getCancelReservationButton().doClick();
				ReservationService resService = mainController.getReservationService();
				List<Reservation> reservations = resService.getReservations();
				
				boolean foundReservation = false;
				for (Reservation res : reservations) {
					if (Objects.equals(res, selectedRes)) {
						foundReservation = true;
					}
				}
				
				Assertions.assertFalse(foundReservation);
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
		
		roomServices.removeRoom(TEST_ROOM.getRoom());
	}
	
}
