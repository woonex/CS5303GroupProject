package edu.baylor.gitawayHotel.controllers;

import java.awt.AWTEvent;
import java.awt.Toolkit;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;

import javax.management.InstanceAlreadyExistsException;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import edu.baylor.gitawayHotel.Room.Room;
import edu.baylor.gitawayHotel.Room.RoomServices;
import edu.baylor.gitawayHotel.gui.AdminGui;
import edu.baylor.gitawayHotel.gui.ChangeCredentialGui;
import edu.baylor.gitawayHotel.gui.CredentialGui;
import edu.baylor.gitawayHotel.gui.SelectUserGui;
import edu.baylor.gitawayHotel.gui.ClerkChangeRoomsGui;
import edu.baylor.gitawayHotel.gui.CreateClerkGui;
import edu.baylor.gitawayHotel.user.User;
import edu.baylor.gitawayHotel.user.UserServices;
import edu.baylor.gitawayHotel.user.UserType;
 
//@Execution(ExecutionMode.CONCURRENT) //disabled to allow the passwords to be modified
public class MainControllerTest {
	private static final String DEFAULT_PW = "password";
	
	private static RoomServices roomServices = new RoomServices();
	private static User ADMIN = new User("TestAdmin", DEFAULT_PW, UserType.ADMIN);
	private static User CLERK = new User("TestClerk", DEFAULT_PW, UserType.HOTEL_CLERK);
	private static User GUEST = new User("TestGuest", DEFAULT_PW, UserType.GUEST);
	private static final Set<User> TEST_USERS = Set.of(ADMIN, CLERK, GUEST);
	
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
	
	@Test
	void testNextOnSplash() {
		MainController mainController = new MainController(roomServices);
		
		mainController.getSplashScreen().getNextButton().doClick();
		
		try {
			SwingUtilities.invokeAndWait(() -> {
				JPanel activePanel = mainController.getLoginGui().getFullPanel();
				Assertions.assertEquals(activePanel, mainController.getMainFrame().getActivePanel());
			});
		} catch (InvocationTargetException | InterruptedException e) {
			e.printStackTrace();
			Assertions.fail("Exception thrown during execution");
		}
		
		closeApp(mainController);
	}
	
	static void login(MainController mainController, User user) {
		mainController.getSplashScreen().getNextButton().doClick();
		mainController.getLoginGui().setUsername(user.getUsername());
		mainController.getLoginGui().setPassword(user.getPassword());
		mainController.getLoginGui().getLoginButton().doClick();
	}
	
	@Test
	void testBadLogin() {
		MainController mainController = new MainController(roomServices);
		mainController.getSplashScreen().getNextButton().doClick();
		
		NotificationWindowLaunch listener = new NotificationWindowLaunch();
		Toolkit.getDefaultToolkit().addAWTEventListener(listener, AWTEvent.WINDOW_EVENT_MASK);
		login(mainController, new User("foo_bar_test_does_not_exist", "pw", UserType.ADMIN));
		
		try {
			SwingUtilities.invokeAndWait(() -> {
				boolean matches = listener.windowLaunchedMatchesString(MainController.INVALID_CREDENTIALS_NOTIFICATION);
				
				Assertions.assertTrue(matches);
			});
		} catch (InvocationTargetException | InterruptedException e) {
			e.printStackTrace();
			Assertions.fail("Exception thrown during execution");
		}
		
		closeApp(mainController);
	}
	
	@Test
	void testAdminLogin() {
		MainController mainController = new MainController(roomServices);
		mainController.getSplashScreen().getNextButton().doClick();
		login(mainController, ADMIN);
		
		try {
			SwingUtilities.invokeAndWait(() -> {
				JPanel activePanel = mainController.getAdminGui().getFullPanel();
				Assertions.assertEquals(activePanel, mainController.getMainFrame().getActivePanel());
			});
		} catch (InvocationTargetException | InterruptedException e) {
			e.printStackTrace();
			Assertions.fail("Exception thrown during execution");
		}
		
		closeApp(mainController);
	}
	
	@Test
	void testAdminLoginAndLogout() {
		MainController mainController = new MainController(roomServices);
		mainController.getSplashScreen().getNextButton().doClick();
		login(mainController, ADMIN);
		
		mainController.getAdminGui().getLogoutButton().doClick();
		
		try {
			SwingUtilities.invokeAndWait(() -> {
				JPanel activePanel = mainController.getLoginGui().getFullPanel();
				Assertions.assertEquals(activePanel, mainController.getMainFrame().getActivePanel());
			});
		} catch (InvocationTargetException | InterruptedException e) {
			e.printStackTrace();
			Assertions.fail("Exception thrown during execution");
		}
		
		closeApp(mainController);
	}
	
	@Test
	void testAdminLoginAndChangeAccount() {
		MainController mainController = new MainController(roomServices);
		login(mainController, ADMIN);
		
		String newPw = DEFAULT_PW.repeat(2);
		
		mainController.getAdminGui().getModifyButton().doClick();
		mainController.getChangeCredentialGui().setUsername(ADMIN.getUsername());
		mainController.getChangeCredentialGui().setCurrentPassword(ADMIN.getPassword());
		mainController.getChangeCredentialGui().setNewPassword(newPw);
		
		try {
			SwingUtilities.invokeAndWait(() -> {
				mainController.getChangeCredentialGui().getLoginButton().doClick();
				User current = mainController.getUserServices().getUser(ADMIN.getUsername());
				Assertions.assertEquals(newPw, current.getPassword());
				ADMIN = new User(ADMIN.getUsername(), newPw, ADMIN.getUserType());
			});
		} catch (InvocationTargetException | InterruptedException e) {
			e.printStackTrace();
			Assertions.fail("Exception thrown during execution");
		}
		
		closeApp(mainController);
	}
	
	@Test
	void testAdminCreateNewClerk() {
		MainController mainController = new MainController(roomServices);
		login(mainController, ADMIN);
		
		AdminGui adminGui = mainController.getAdminGui();
		adminGui.getCreateClerkButton().doClick();
		
		CreateClerkGui createClerkGui = mainController.getCreateClerkGui();
		
		String clerkUsername = "TEST CLERK THAT WILL BE DELETED";
		createClerkGui.setClerkUsername(clerkUsername);
		createClerkGui.setClerkPassword(DEFAULT_PW);
		createClerkGui.getCreateUserButton().doClick();
		
		try {
			SwingUtilities.invokeAndWait(() -> {
				UserServices userServices = mainController.getUserServices();
				User createdClerk = userServices.getUser(clerkUsername);
				Assertions.assertNotNull(createdClerk);
				
				userServices.removeUserByUsername(clerkUsername);
				
				createdClerk = userServices.getUser(clerkUsername);
				Assertions.assertNull(createdClerk);
			});
		} catch (InvocationTargetException | InterruptedException e) {
			e.printStackTrace();
			Assertions.fail("Exception thrown during execution");
		}
		
		closeApp(mainController);
	}
	
	@Test
	void testAdminAddExistingClerk() {
		MainController mainController = new MainController(roomServices);
		login(mainController, ADMIN);
		
		AdminGui adminGui = mainController.getAdminGui();
		adminGui.getCreateClerkButton().doClick();
		
		CreateClerkGui createClerkGui = mainController.getCreateClerkGui();
		
		String clerkUsername = CLERK.getUsername();
		createClerkGui.setClerkUsername(clerkUsername);
		createClerkGui.setClerkPassword(DEFAULT_PW);
		
		
		NotificationWindowLaunch listener = new NotificationWindowLaunch();
		Toolkit.getDefaultToolkit().addAWTEventListener(listener, AWTEvent.WINDOW_EVENT_MASK);

		createClerkGui.getCreateUserButton().doClick();
		
		try {
			SwingUtilities.invokeAndWait(() -> {
				boolean matches = listener.windowLaunchedMatchesString(MainController.USERNAME_EXISTS);
			
				Assertions.assertTrue(matches);
			});
		} catch (InvocationTargetException | InterruptedException e) {
			e.printStackTrace();
			Assertions.fail("Exception thrown during execution");
		}
		
		closeApp(mainController);
	}
	
	@Test
	void testAdminResetPasswordOfUser() {
		MainController mainController = new MainController(roomServices);
		mainController.getSplashScreen().getNextButton().doClick();
		login(mainController, ADMIN);
		
		//pre-update password to ensure it can be changed
		String tmpPass = DEFAULT_PW.repeat(2);
		mainController.getUserServices().updateUser(GUEST.getUsername(), tmpPass);
		User tmp = mainController.getUserServices().getUser(GUEST.getUsername());
		Assertions.assertEquals(tmp.getPassword(), tmpPass);
		
		//get the admin gui
		AdminGui adminGui = mainController.getAdminGui();
		adminGui.getResetUserPasswordButton().doClick();
		
		//selec the user to reset password for
		SelectUserGui sug = mainController.getSelectUserGui();
		sug.selectUser(GUEST);
		
		NotificationWindowLaunch listener = new NotificationWindowLaunch();
		Toolkit.getDefaultToolkit().addAWTEventListener(listener, AWTEvent.WINDOW_EVENT_MASK);
		
		try {
			SwingUtilities.invokeAndWait(() -> {
				sug.getSelectButton().doClick();
				User user = mainController.getUserServices().getUser(GUEST.getUsername());
				
				Assertions.assertNotEquals(tmpPass, user.getPassword());
			});
		} catch (Exception e) {
			e.printStackTrace();
			Assertions.fail("Exception thrown during execution");
		}
		
		closeApp(mainController);
	}
	
	@Test
	void testClerkLogin() {
		MainController mainController = new MainController(roomServices);
		mainController.getSplashScreen().getNextButton().doClick();
		login(mainController, CLERK);
		
		try {
			SwingUtilities.invokeAndWait(() -> {
				JPanel activePanel = mainController.getClerkGui().getFullPanel();
				Assertions.assertEquals(activePanel, mainController.getMainFrame().getActivePanel());
			});
		} catch (InvocationTargetException | InterruptedException e) {
			e.printStackTrace();
			Assertions.fail("Exception thrown during execution");
		}
		
		closeApp(mainController);
	}
	
	@Test
	void testClerkLoginAndLogout() {
		MainController mainController = new MainController(roomServices);
		mainController.getSplashScreen().getNextButton().doClick();
		login(mainController, CLERK);
		
		mainController.getClerkGui().getLogoutButton().doClick();
		
		try {
			SwingUtilities.invokeAndWait(() -> {
				JPanel activePanel = mainController.getLoginGui().getFullPanel();
				Assertions.assertEquals(activePanel, mainController.getMainFrame().getActivePanel());
			});
		} catch (InvocationTargetException | InterruptedException e) {
			e.printStackTrace();
			Assertions.fail("Exception thrown during execution");
		}
		
		closeApp(mainController);
	}
	
	@Test
	void testClerkViewRooms() {
		MainController mainController = new MainController(roomServices);
		mainController.getSplashScreen().getNextButton().doClick();
		login(mainController, CLERK);
		
		mainController.getClerkGui().getModifyRoomsButton().doClick();
		try {
			SwingUtilities.invokeAndWait(() -> {
				JPanel activePanel = mainController.getViewRoomsGui().getFullPanel();
				Assertions.assertEquals(activePanel, mainController.getMainFrame().getActivePanel());
				mainController.getViewRoomsGui().getBackButton().doClick();
			});
			
			SwingUtilities.invokeAndWait(() -> {
				JPanel activePanel = mainController.getClerkGui().getFullPanel();
				Assertions.assertEquals(activePanel, mainController.getMainFrame().getActivePanel());
			});
		} catch (InvocationTargetException | InterruptedException e) {
			e.printStackTrace();
			Assertions.fail("Exception thrown during execution");
		}
		
		closeApp(mainController);
	}
	
	@Test
	void testClerkAddAndRemoveRoom() {
		MainController mainController = new MainController(roomServices);
		mainController.getSplashScreen().getNextButton().doClick();
		login(mainController, CLERK);
		
		mainController.getClerkGui().getModifyRoomsButton().doClick();
		ClerkChangeRoomsGui viewRoomsGui = mainController.getViewRoomsGui();
		
		int newRoomNum = 9001;
		viewRoomsGui.setRoomField(newRoomNum);
		viewRoomsGui.getAddRoomButton().doClick();
		try {
			SwingUtilities.invokeAndWait(() -> {
				Room newRoom = roomServices.getRoomByNumber(newRoomNum);
				Assertions.assertNotNull(newRoom);
				
				viewRoomsGui.setRoomField(newRoomNum);
				viewRoomsGui.getRemoveRoomButton().doClick();
				newRoom = roomServices.getRoomByNumber(newRoomNum);
				Assertions.assertNull(newRoom);
			});
		} catch (InvocationTargetException | InterruptedException e) {
			e.printStackTrace();
			Assertions.fail("Exception thrown during execution");
		}
		
		closeApp(mainController);
	}
	
	@Test
	void addRoomAndModifyAndSave() {
		MainController mainController = new MainController(roomServices);
		mainController.getSplashScreen().getNextButton().doClick();
		login(mainController, CLERK);
		
		mainController.getClerkGui().getModifyRoomsButton().doClick();
		ClerkChangeRoomsGui viewRoomsGui = mainController.getViewRoomsGui();
		
		int newRoomNum = 505050;
		viewRoomsGui.setRoomField(newRoomNum);
		viewRoomsGui.getAddRoomButton().doClick();
		Room newRoom = roomServices.getRoomByNumber(newRoomNum);
		Assertions.assertNotNull(newRoom);
		
		viewRoomsGui.selectTableRowByRoomNum(newRoomNum);
		newRoom.setBedQty(5);
		newRoom.setBedType("King");
		viewRoomsGui.setTableDataInSelectedRow(newRoom);
		viewRoomsGui.getSaveRoomsButton().doClick();
		
		Room modRoom = roomServices.getRoomByNumber(newRoomNum);
		Assertions.assertEquals(newRoom, modRoom);
		
		viewRoomsGui.setRoomField(newRoomNum);
		viewRoomsGui.getRemoveRoomButton().doClick();
		newRoom = roomServices.getRoomByNumber(newRoomNum);
		Assertions.assertNull(newRoom);
		
		closeApp(mainController);
	}
	
	@Test
	void testCreateGuestExisting() {
		MainController mainController = new MainController(roomServices);
		mainController.getSplashScreen().getNextButton().doClick();
		
		CredentialGui loginGui = mainController.getLoginGui();
		loginGui.setUsername(GUEST.getUsername());
		loginGui.setPassword(DEFAULT_PW);
		
		NotificationWindowLaunch listener = new NotificationWindowLaunch();
		Toolkit.getDefaultToolkit().addAWTEventListener(listener, AWTEvent.WINDOW_EVENT_MASK);

		loginGui.getCreateGuestButton().doClick();
		
		try {
			SwingUtilities.invokeAndWait(() -> {
				boolean matches = listener.windowLaunchedMatchesString(MainController.USERNAME_EXISTS);
			
				Assertions.assertTrue(matches);
			});
		} catch (InvocationTargetException | InterruptedException e) {
			e.printStackTrace();
			Assertions.fail("Exception thrown during execution");
		}
		
		closeApp(mainController);
	}
	
	@Test
	void testCreateGuest() {
		MainController mainController = new MainController(roomServices);
		mainController.getSplashScreen().getNextButton().doClick();
		
		CredentialGui loginGui = mainController.getLoginGui();
		String guestUsername = "testAddGuestUsername";
		loginGui.setUsername(guestUsername);
		loginGui.setPassword(DEFAULT_PW);
		
		NotificationWindowLaunch listener = new NotificationWindowLaunch();
		Toolkit.getDefaultToolkit().addAWTEventListener(listener, AWTEvent.WINDOW_EVENT_MASK);
		try {
			SwingUtilities.invokeAndWait(() -> {
				loginGui.getCreateGuestButton().doClick();

				boolean matches = listener.windowLaunchedMatchesString(MainController.GUEST_CREATED + guestUsername);

				Assertions.assertTrue(matches);

				UserServices userServices = mainController.getUserServices();
				User user = userServices.getUser(guestUsername);
				Assertions.assertNotNull(user);

				userServices.removeUserByUsername(guestUsername);
				user = userServices.getUser(guestUsername);
				Assertions.assertNull(user);
			});
		} catch (InvocationTargetException | InterruptedException e) {
			e.printStackTrace();
			Assertions.fail("Exception thrown during execution");
		}
		
		closeApp(mainController);
	}
	
	@Test
	void testGuestLogin() {
		MainController mainController = new MainController(roomServices);
		login(mainController, GUEST);
		
		try {
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
	void testGuestLoginAndLogout() {
		MainController mainController = new MainController(roomServices);
		login(mainController, GUEST);
		
		mainController.getGuestGui().getLogoutButton().doClick();
		
		try {
			SwingUtilities.invokeAndWait(() -> {
				JPanel activePanel = mainController.getLoginGui().getFullPanel();
				Assertions.assertEquals(activePanel, mainController.getMainFrame().getActivePanel());
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
	static void removeTestUsers() {
		MainController mainController = new MainController(roomServices);
		Set<User> users = Set.of(ADMIN, CLERK, GUEST);
		for (User user : users) {
			mainController.getUserServices().removeUserByUsername(user.getUsername());
		}
		closeApp(mainController);
	}
	
}
