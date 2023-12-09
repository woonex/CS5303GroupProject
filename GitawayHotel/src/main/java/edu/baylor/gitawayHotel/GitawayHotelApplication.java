package edu.baylor.gitawayHotel;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;

import javax.swing.SwingUtilities;

import edu.baylor.gitawayHotel.Room.Room;
import edu.baylor.gitawayHotel.Room.RoomServices;
import edu.baylor.gitawayHotel.controllers.MainController;
import edu.baylor.gitawayHotel.gui.ChangeCredentialGui;
import edu.baylor.gitawayHotel.gui.CredentialGui;
import edu.baylor.gitawayHotel.gui.ClerkChangeRoomsGui;
import edu.baylor.gitawayHotel.reservation.Reservation;
import edu.baylor.gitawayHotel.reservation.ReservationService;
import edu.baylor.gitawayHotel.gui.MainFrame;
import edu.baylor.gitawayHotel.gui.SplashScreen;
import edu.baylor.gitawayHotel.user.User;
import edu.baylor.gitawayHotel.user.UserServices;

public class GitawayHotelApplication {
	
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			MainFrame mainFrame = new MainFrame();
			SplashScreen splash = new SplashScreen();
			CredentialGui loginGui = new CredentialGui();
			ChangeCredentialGui changeCredentialGui = new ChangeCredentialGui();
			
			
			UserServices userServices = new UserServices();
			RoomServices roomServices = new RoomServices();
			
			ReservationService reservationServices = new ReservationService(roomServices);
			
			ClerkChangeRoomsGui viewRoomsGui = new ClerkChangeRoomsGui(roomServices);
			
			MainController mainController = new MainController(mainFrame, splash, loginGui, userServices, roomServices, changeCredentialGui, viewRoomsGui, reservationServices);
		});
	}
}
