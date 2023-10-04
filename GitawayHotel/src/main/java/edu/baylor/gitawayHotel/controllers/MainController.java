package edu.baylor.gitawayHotel.controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import edu.baylor.gitawayHotel.gui.LoginGui;
import edu.baylor.gitawayHotel.gui.MainFrame;
import edu.baylor.gitawayHotel.gui.SplashScreen;
import edu.baylor.gitawayHotel.login.UserServices;

/**A class that manages the UI screen paging and handling from one screen to the next
 * @author Nathan
 *
 */
public class MainController {
	private SplashScreen splashScreen;
	private LoginGui loginGui;
	private JFrame mainFrame;
	private UserServices userServices;

	public MainController(JFrame mainFrame, SplashScreen splashScreen, LoginGui loginGui, UserServices userServices) {
		this.mainFrame = mainFrame;
		this.splashScreen = splashScreen;
		this.loginGui = loginGui;
		this.userServices = userServices;
		
		mainFrame.add(splashScreen.getPanel());
		
		setupPaging();
	}

	/**Sets up the handling from clicking finish on one page
	 * 
	 */
	private void setupPaging() {
		
		//when the user is on the main screen and clicks next
		JButton mainNext = splashScreen.getNextButton();
		mainNext.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				SwingUtilities.invokeLater(() -> {
					mainFrame.remove(splashScreen.getPanel());
					mainFrame.add(loginGui.getPanel());
					mainFrame.revalidate();
					mainFrame.repaint();
				});
			}
			
		});
		
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
					mainFrame.remove(loginGui.getPanel());
					mainFrame.add(new JLabel("USER AUTHENTICATED AS A " + userServices.getUserType(username)));
//					mainFrame.add() //TODO implement the next page after the user is logged in
					mainFrame.revalidate();
					mainFrame.repaint();
				});
			}
			
		});
	}
}
