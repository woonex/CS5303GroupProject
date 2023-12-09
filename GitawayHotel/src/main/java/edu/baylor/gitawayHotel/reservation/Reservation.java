package edu.baylor.gitawayHotel.reservation;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
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
	private boolean cancelled = false;
	public static final int RESERVATION_GRACE_DAYS = 2;
	

	public Reservation(LocalDate startDate, LocalDate endDate, User guest, Room room) {
		this.startDate = startDate;
		this.endDate = endDate;
		this.guest = guest;
		this.room = room;
		setDateReservationMade(LocalDate.now());
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
	void setDateReservationMade(LocalDate dateReservationMade) {
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
	
	public boolean willIncurCancellationFee() {
		return LocalDate.now().minusDays(RESERVATION_GRACE_DAYS).isAfter(getDateReservationMade());
	}
	
	public void setCancelled() {
		this.cancelled = true;
	}
	
	public long getQtyDays() {
		return ChronoUnit.DAYS.between(getStartDate(), getEndDate());
	}
	
	public double getFullCost() {
		double dayCost = getDayCost();
		long qtyDays = getQtyDays();
		
		if (wasCancelled()) {
			return dayCost * qtyDays * .8;
		} else {
			return dayCost * qtyDays;
		}
	}
	
	private static final String TAB = " ".repeat(6);
	private static final String HALFTAB = " ".repeat(3);
	
	private double getDayCost() {
		return room.getDailyCost();
	}
	
	public String getFormattedCost() {
		StringBuilder sb = new StringBuilder();
		sb.append("Reservation:");
		
		sb.append("\n");
		sb.append(TAB);
		sb.append("Start date: ");
		sb.append(getStartDate());
		sb.append(TAB + "End Date: ");
		sb.append(getEndDate());
		sb.append("\n\n");
		
		sb.append(TAB);
		sb.append("Days:");
		sb.append(TAB.repeat(2));
		sb.append("Day Cost");
		sb.append(TAB.repeat(2));
		sb.append("Modifier");
		sb.append(TAB.repeat(2) + HALFTAB);
		sb.append("Reservation Cost");
		sb.append("\n");
		
		sb.append(TAB + HALFTAB);
		sb.append(getQtyDays());
		sb.append(TAB + HALFTAB);
		sb.append("*");
		sb.append(TAB);
		sb.append("$ " + String.format("%.2f", getDayCost()));
		sb.append(TAB + HALFTAB);
		sb.append("*");
		sb.append(TAB + HALFTAB);
		
		double resCost = getFullCost();
		if (wasCancelled()) {
			sb.append("80%");
		} else {
			sb.append("100%");
		}
		
		sb.append(TAB);
		sb.append("=");
		sb.append(TAB.repeat(2) + "$ " + String.format("%.2f", resCost));
		if (wasCancelled()) {
			sb.append("\n");
			sb.append(TAB + "(Cancelled more than " + Reservation.RESERVATION_GRACE_DAYS + " days after reservation)");
		}
		sb.append("\n\n");	
		return sb.toString();
	}

	public boolean wasCancelled() {
		return this.cancelled;
	}

}
