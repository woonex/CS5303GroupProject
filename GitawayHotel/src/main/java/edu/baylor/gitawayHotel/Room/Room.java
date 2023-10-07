package edu.baylor.gitawayHotel.Room;

/**
 * A concept of a room that has attributes
 * 
 * @author Nathan
 *
 */
public class Room {
    private String room;
    private String bedQty;
    private String bedType;
    private String noSmoking;

    public String getRoom() {
        return room;
    }

    public void getRoom(String room) {
        this.room = room;
    }

    public String getBedQty() {
        return bedQty;
    }

    public void setBedQty(String bedQty) {
        this.bedQty = bedQty;
    }

    public String getBedType() {
        return bedType;
    }

    public void setBedType(String bedType) {
        this.bedType = bedType;
    }

    public String getNoSmoking() {
        return noSmoking;
    }

    public void setNoSmoking(String noSmoking) {
        this.noSmoking = noSmoking;
    }
}
