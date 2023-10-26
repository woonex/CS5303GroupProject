package edu.baylor.gitawayHotel.Room;

import java.util.Objects;

/**
 * A concept of a room that has attributes
 * 
 * @author Nathan
 *
 */
public class Room implements Comparable<Room> {
    private Integer room;
    private Integer bedQty;
    private String bedType;
    private Boolean noSmoking;

    public int getRoom() {
        return room;
    }

    public void setRoom(int room) {
        this.room = room;
    }

    public int getBedQty() {
        return bedQty;
    }

    public void setBedQty(int bedQty) {
        this.bedQty = bedQty;
    }

    public String getBedType() {
        return bedType;
    }

    public void setBedType(String bedType) {
        this.bedType = bedType;
    }

    public boolean getNoSmoking() {
        return noSmoking;
    }

    public void setNoSmoking(boolean noSmoking) {
        this.noSmoking = noSmoking;
    }
    
    public static boolean satisfiesRequest(Room existing, Room request) {
    	if (!(existing.bedQty >= request.bedQty)) {
    		return false;
    	} else if (existing.noSmoking != request.noSmoking) {
    		return false;
    	} else if (!Objects.equals(existing.bedType, request.bedType)) {
    		return false;
    	}
    	return true;
    }
    
    @Override
    public int compareTo(Room otherRoom) {
        // Compare based on the room number
        return Integer.compare(this.room, otherRoom.room);
    }

	@Override
	public int hashCode() {
		return Objects.hash(room);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Room other = (Room) obj;
		return room == other.room;
	}
}
