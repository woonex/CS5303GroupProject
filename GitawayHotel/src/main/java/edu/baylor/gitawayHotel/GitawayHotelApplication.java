package edu.baylor.gitawayHotel;

import javax.management.InstanceAlreadyExistsException;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import edu.baylor.gitawayHotel.controllers.MainController;
import edu.baylor.gitawayHotel.gui.AdminGui;
import edu.baylor.gitawayHotel.gui.ClerkGui;
import edu.baylor.gitawayHotel.gui.GuestGui;
import edu.baylor.gitawayHotel.gui.LoginGui;
import edu.baylor.gitawayHotel.gui.MainFrame;
import edu.baylor.gitawayHotel.gui.SplashScreen;
import edu.baylor.gitawayHotel.user.UserServices;
import edu.baylor.gitawayHotel.user.UserType;

public class GitawayHotelApplication {
	
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			MainFrame mainFrame = new MainFrame();
			SplashScreen splash = new SplashScreen();
			LoginGui loginGui = new LoginGui();
			AdminGui adminGui = new AdminGui();
			ClerkGui clerkGui = new ClerkGui();
			GuestGui guestGui = new GuestGui();
			UserServices userServices = new UserServices();
			
			MainController mainController = new MainController(mainFrame, splash, loginGui, adminGui, clerkGui, guestGui, userServices);
		});
		
	}
}