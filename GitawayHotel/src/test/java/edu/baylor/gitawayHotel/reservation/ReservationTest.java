package edu.baylor.gitawayHotel.reservation;

import java.time.LocalDate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import edu.baylor.gitawayHotel.Room.Room;
import edu.baylor.gitawayHotel.user.User;

public class ReservationTest {
	static Room room = new Room();
	static User user = new User("test");
	
	@BeforeAll
	static void setup() {
		room.setDailyCost(50.40);
	}
	
	@Test
	void testReservationCost() {
		int days = 5;
		Reservation res = new Reservation(LocalDate.now(), LocalDate.now().plusDays(days), user, room);
		
		Assertions.assertEquals(room.getDailyCost() * days, res.getFullCost());
	}
	
	@Test 
	void testReservationCancelCost() {
		int days = 6;
		Reservation res = new Reservation(LocalDate.now(), LocalDate.now().plusDays(days), user, room);
		res.setDateReservationMade(LocalDate.now().minusDays(Reservation.RESERVATION_GRACE_DAYS - 1));
		res.setCancelled();
		
		Assertions.assertEquals(room.getDailyCost() * days * .8, res.getFullCost());
	}
}
