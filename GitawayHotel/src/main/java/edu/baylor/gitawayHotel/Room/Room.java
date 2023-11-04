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
    
    /**Gets a clone of the room without the room number
     * @return
     */
    public Room getUniqueCharacteristics() {
    	Room room = new Room();
    	room.setBedQty(getBedQty());
    	room.setBedType(getBedType());
    	room.setNoSmoking(getNoSmoking());
    	return room;
    }
    
    public static boolean satisfiesRequest(Room existing, Room request) {
    	if (!Objects.equals(existing.bedQty,request.bedQty)) {
    		return false;
    	} else if (!Objects.equals(existing.noSmoking, request.noSmoking)) {
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
		return Objects.hash(bedQty, bedType, noSmoking, room);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Room))
			return false;
		Room other = (Room) obj;
		return Objects.equals(bedQty, other.bedQty) && Objects.equals(bedType, other.bedType)
				&& Objects.equals(noSmoking, other.noSmoking) && Objects.equals(room, other.room);
	}

	@Override
	public String toString() {
		return room + ", bedQty=" + bedQty + ", bedType=" + bedType + ", noSmoking=" + noSmoking;
	}
}
