package edu.baylor.gitawayHotel.Room;

/**
 * A concept of a room that has attributes
 * 
 * @author Nathan
 *
 */
public class Room {
    private int room;
    private int bedQty;
    private String bedType;
    private boolean noSmoking;

    public int getRoom() {
        return room;
    }

    public void getRoom(int room) {
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
}
