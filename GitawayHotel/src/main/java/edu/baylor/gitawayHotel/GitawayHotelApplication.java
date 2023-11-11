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
import edu.baylor.gitawayHotel.gui.ViewRoomsGui;
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
			
			try {
				performTest(reservationServices, roomServices);
			} catch (RuntimeException e) {
				System.out.println("Reservations already exist for test cases");
//				e.printStackTrace();
			}
			
			ViewRoomsGui viewRoomsGui = new ViewRoomsGui(roomServices);
			
			MainController mainController = new MainController(mainFrame, splash, loginGui, userServices, roomServices, changeCredentialGui, viewRoomsGui, reservationServices);
		});
		
		
		
	}
	
	/**performs some requests to ensure that reservation correctly works
	 * TODO extract to Junit test
	 * @param reservationServices
	 */
 	private static void performTest(ReservationService reservationServices, RoomServices roomServices) {
 		User user = new User("Joe");
 		
 		Room room = roomServices.getRooms().iterator().next();
		
 		LocalDate now = LocalDate.now();
 		LocalDate startDate= now;
 		LocalDate endDate = now.plusDays(1);
		
 		Set<Room> available = reservationServices.getAvailableRooms(startDate, endDate);
 		available.stream().forEach(r -> System.out.print(r.getRoom() + " "));
 		System.out.println();
		
 		Reservation currentRes = new Reservation(startDate, endDate, user, room);
 		reservationServices.addReservation(currentRes);

 		available = reservationServices.getAvailableRooms(startDate, endDate);
 		available.stream().forEach(r -> System.out.print(r.getRoom() + " "));
 		System.out.println();
		
 		startDate = now.plusDays(3);
 		endDate= now.plusDays(4);
		
 		available = reservationServices.getAvailableRooms(startDate, endDate);
 		available.stream().forEach(r -> System.out.print(r.getRoom() + " "));
 		System.out.println();
		
 		currentRes = new Reservation(startDate, endDate, user, room);
 		reservationServices.addReservation(currentRes);
 		available = reservationServices.getAvailableRooms(startDate, endDate);
 		available.stream().forEach(r -> System.out.print(r.getRoom() + " "));
 		System.out.println();
		
 		startDate = now.plusDays(2);
 		endDate = now.plusDays(3);
 		available = reservationServices.getAvailableRooms(startDate, endDate);
 		available.stream().forEach(r -> System.out.print(r.getRoom() + " "));
 		System.out.println();
		
 		currentRes = new Reservation(startDate, endDate, user, room);
 		reservationServices.addReservation(currentRes);
 		available = reservationServices.getAvailableRooms(startDate, endDate);
 		available.stream().forEach(r -> System.out.print(r.getRoom() + " "));
 		System.out.println();
 	}
}
