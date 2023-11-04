package edu.baylor.gitawayHotel.reservation;

import java.time.LocalDate;

import edu.baylor.gitawayHotel.Room.Room;
import edu.baylor.gitawayHotel.user.User;

public class Reservation {
	private LocalDate startDate;
	private LocalDate endDate;
	private User guest;
	private Room room;

	public Reservation(LocalDate startDate, LocalDate endDate, User guest, Room room) {
		this.startDate = startDate;
		this.endDate = endDate;
		this.guest = guest;
		this.room = room;
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
	
	@Override
	public String toString() {
		return "Room " + room.getRoom() + " is reserved by " + guest.getUsername() + " from " + startDate + " to " + endDate;
	}
}
