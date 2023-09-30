package edu.baylor.gitawayHotel.gui;

import javax.swing.JFrame;

public class MainFrame extends JFrame {
	public MainFrame() {
		super("Gitaway Hotel");
		
		setSize(400, 300);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		setVisible(true);
	}
}
