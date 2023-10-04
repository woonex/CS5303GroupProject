package edu.baylor.gitawayHotel;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import edu.baylor.gitawayHotel.controllers.MainController;
import edu.baylor.gitawayHotel.gui.LoginGui;
import edu.baylor.gitawayHotel.gui.MainFrame;
import edu.baylor.gitawayHotel.gui.SplashScreen;
import edu.baylor.gitawayHotel.login.UserServices;

public class GitawayHotelApplication {
	public static void main(String[] args) {
		new UserServices();
		
		
		
		
		SwingUtilities.invokeLater(() -> {
			MainFrame mainFrame = new MainFrame();
			SplashScreen splash = new SplashScreen();
			LoginGui loginGui = new LoginGui();
			UserServices userServices = new UserServices();
			MainController mainController = new MainController(mainFrame, splash, loginGui, userServices);
		});
	}
}
