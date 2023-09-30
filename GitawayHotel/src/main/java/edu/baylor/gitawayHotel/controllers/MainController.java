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

	private void setupPaging() {
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
					mainFrame.add(new JLabel("USER AUTHENTICATED AS " + userServices.getUserType(username)));
//					mainFrame.add() //TODO implement the next page after the user is logged in
					mainFrame.revalidate();
					mainFrame.repaint();
				});
			}
			
		});
	}
}
