package edu.baylor.gitawayHotel.controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.management.InstanceAlreadyExistsException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import edu.baylor.gitawayHotel.gui.AdminGui;
import edu.baylor.gitawayHotel.gui.AuthenticatedGui;
import edu.baylor.gitawayHotel.gui.ClerkGui;
import edu.baylor.gitawayHotel.gui.GuestGui;
import edu.baylor.gitawayHotel.gui.IGui;
import edu.baylor.gitawayHotel.gui.LoginGui;
import edu.baylor.gitawayHotel.gui.MainFrame;
import edu.baylor.gitawayHotel.gui.SplashScreen;
import edu.baylor.gitawayHotel.user.UserServices;
import edu.baylor.gitawayHotel.user.UserType;

/**A class that manages the UI screen paging and handling from one screen to the next
 * @author Nathan
 *
 */
public class MainController {
	private final SplashScreen splashScreen;
	private final LoginGui loginGui;
	private final JFrame mainFrame;
	private final AdminGui adminGui;
	private final UserServices userServices;
	private final ClerkGui clerkGui;
	private final GuestGui guestGui;

	public MainController(
			JFrame mainFrame, 
			SplashScreen splashScreen, 
			LoginGui loginGui, 
			AdminGui adminGui, 
			ClerkGui clerkGui, 
			GuestGui guestGui, 
			UserServices userServices
			) {
		this.mainFrame = mainFrame;
		this.splashScreen = splashScreen;
		this.loginGui = loginGui;
		this.adminGui = adminGui;
		this.userServices = userServices;
		this.clerkGui = clerkGui;
		this.guestGui = guestGui;
		
		mainFrame.add(splashScreen.getPanel());
		
		setupPaging();
	}

	/**Sets up the handling from clicking finish on one page
	 * 
	 */
	private void setupPaging() {
		setupSplashscreenActions();
		
		setupLoginActions();
		
		setupAdminActions();
		
		setupClerkActions();
		
		setupGuestActions();
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
				SwingUtilities.invokeLater(() -> {
					mainFrame.remove(splashScreen.getPanel());
					mainFrame.add(loginGui.getFullPanel());
					mainFrame.revalidate();
					mainFrame.repaint();
				});
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
				String username = loginGui.getUsername();
				String password = loginGui.getPassword();
				boolean authenticated = userServices.isSuccessfulLogin(username, password);
				
				if (!authenticated) {
					JOptionPane.showMessageDialog(mainFrame, "Invalid credentials. Please try again.", "Authentication Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				SwingUtilities.invokeLater(() -> {
					mainFrame.remove(loginGui.getFullPanel());
					UserType userType = userServices.getUserType(username);
					
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
					
					mainFrame.revalidate();
					mainFrame.repaint();
				});
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
					JOptionPane.showMessageDialog(mainFrame, "Username already exists.\nPlease choose another username", "Creation Error", JOptionPane.ERROR_MESSAGE);
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
	}
	
	/**Sets up the clerk actions
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
	}
	
	/**Logs the active user out
	 * @param activePanel the current panel this was called from
	 */
	private void logoutUser(IGui iGui) {
		mainFrame.remove(iGui.getFullPanel());
		mainFrame.add(loginGui.getFullPanel());
		mainFrame.revalidate();
		mainFrame.repaint();
	}
}
