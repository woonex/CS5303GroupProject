package edu.baylor.gitawayHotel;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import edu.baylor.gitawayHotel.gui.MainGui;
import edu.baylor.gitawayHotel.login.UserServices;

public class GitawayHotelApplication {
	public static void main(String[] args) {
		new UserServices();
		MainGui gui = new MainGui();
		
		SwingUtilities.invokeLater(() -> {
			JFrame frame = new JFrame("Gitaway Hotel");
			frame.add(gui.getPanel());
			
			frame.setSize(400, 300);
			frame.setLocationRelativeTo(null);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			
//			frame.pack();
			frame.setVisible(true);
			
		});
	}
}
