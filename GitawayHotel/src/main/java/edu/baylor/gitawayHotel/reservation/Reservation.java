package edu.baylor.gitawayHotel.reservation;

import java.time.LocalDate;
import java.util.Objects;

import edu.baylor.gitawayHotel.Room.Room;
import edu.baylor.gitawayHotel.user.User;

public class Reservation implements Comparable<Reservation> {
	private LocalDate startDate;
	private LocalDate endDate;
	private User guest;
	private LocalDate dateReservationMade = LocalDate.now();
	private Room room;
	private boolean isCheckedIn = false;
	

	public Reservation(LocalDate startDate, LocalDate endDate, User guest, Room room) {
		this.startDate = startDate;
		this.endDate = endDate;
		this.guest = guest;
		this.room = room;
	}
	
	/**Marks the reservation as having been checked into the hotel
	 * @return
	 */
	public void setCheckinStatus(boolean checkInStatus) {
		this.isCheckedIn = checkInStatus;
	}
	
	/**Gets if the guest is checked in or out
	 * @return true if the guest is checked in and false if they are not
	 */
	public boolean getCheckinStatus() {
		return this.isCheckedIn;
	}

	/**
	 * @return the startDate
	 */
	public LocalDate getStartDate() {
		return startDate;
	}

	/**
	 * @param startDate the startDate to set
	 */
	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	/**
	 * @return the endDate
	 */
	public LocalDate getEndDate() {
		return endDate;
	}

	/**
	 * @param endDate the endDate to set
	 */
	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}

	/**
	 * @return the guest
	 */
	public User getGuest() {
		return guest;
	}

	/**
	 * @param guest the guest to set
	 */
	public void setGuest(User guest) {
		this.guest = guest;
	}

	/**
	 * @return the room
	 */
	public Room getRoom() {
		return room;
	}

	/**
	 * @param room the room to set
	 */
	public void setRoom(Room room) {
		this.room = room;
	}
	
	/**
	 * @return the dateReservationMade
	 */
	public LocalDate getDateReservationMade() {
		return dateReservationMade;
	}

	/**
	 * @param dateReservationMade the dateReservationMade to set
	 */
	public void setDateReservationMade(LocalDate dateReservationMade) {
		this.dateReservationMade = dateReservationMade;
	}
	
	@Override
	public String toString() {
		return "Room " + room.getRoom() + " is reserved by " + guest.getUsername() + " from " + startDate + " to " + endDate;
	}

	@Override
	public int hashCode() {
		return Objects.hash(endDate, guest, room, startDate);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Reservation))
			return false;
		Reservation other = (Reservation) obj;
		return Objects.equals(endDate, other.endDate) && Objects.equals(guest, other.guest)
				&& Objects.equals(room, other.room) && Objects.equals(startDate, other.startDate);
	}

	@Override
	public int compareTo(Reservation o) {
		return this.startDate.compareTo(o.startDate);
	}
	
	public boolean isCurrentlyActive() {
		LocalDate now = LocalDate.now();
		boolean reservationIsOverTodayDate = 
				((startDate.isAfter(now) || startDate.equals(now))
				&& (now.equals(endDate) || now.isBefore(endDate)));

		return reservationIsOverTodayDate && isCheckedIn;
	}

}
