package edu.baylor.gitawayHotel.Room;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**Loading rooms from JSON from disk and managing saving
 * @author Nathan
 *
 */
public class RoomServices {
	private static final Logger logger = LogManager.getLogger(RoomServices.class);
	private static final String FILENAME = "rooms.json";
	private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
	
	private File diskFile;
	private Map<Integer, Room> rooms;
	
	public RoomServices() {
		String filePath = getFilePath(FILENAME);
		this.diskFile = getFile(filePath);
		this.rooms = loadRooms(this.diskFile);
	}
	
	/**Adds the room to disk
	 * @param room
	 */
	public void addRoom(Room room) {
		this.rooms.put(room.getRoom(), room);
		saveRooms(getRooms());
	}
	
	/**Removes the room from disk
	 * @param room
	 */
	public void removeRoom(Room room) {
		removeRoom(room.getRoom());
	}
	
	/**Removes the room from disk
	 * @param roomNum
	 */
	public void removeRoom(int roomNum) {
		this.rooms.remove(roomNum);
		saveRooms(getRooms());
	}
	
	/**Gets the room by the room number
	 * @param roomNum
	 * @return
	 */
	public Room getRoomByNumber(int roomNum) {
		return this.rooms.get(roomNum);
	}
	
	/**Gets the rooms sorted by numerical room number
	 * @return
	 */
	public List<Room> getRooms() {
		return rooms.entrySet().stream()
				.map(entry -> entry.getValue())
				.sorted()
				.collect(Collectors.toList());
	}
	
	/**Gets the unique rooms and the qty of them
	 * @return
	 */
	public Map<Room, Integer> getUniqueRoomTypes() {
		Map<Room, Integer> map = new HashMap<Room, Integer>();
		
		//loop over all rooms and get only their unique characteristics
		for (Room room : getRooms()) {
			Room duplicate = room.getUniqueCharacteristics();
			
			if (map.containsKey(duplicate)) {
				Integer qty = map.get(duplicate);
				map.replace(duplicate, ++qty);
			} else {
				map.put(duplicate, 1);
			}
		}
		
		return map;
	}
	
	/**Gets the map of rooms by room number that are present in the file
	 * @return map of rooms or blank map for file error
	 */
	private static Map<Integer, Room> loadRooms(File file) {
		logger.trace("RoomServices loadRooms() invoked");
		try (FileReader reader = new FileReader(file)) {
			Room[] aRooms = gson.fromJson(reader, Room[].class);
			
			List<Room> rooms = Arrays.asList(aRooms);
			Map<Integer, Room> roomsByRoomNum = rooms.stream()
					.collect(Collectors.toMap(
							room -> room.getRoom(), 
							room -> room));
			
			return roomsByRoomNum;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Map.of();
	}
	
	/**Saves the list of rooms to the disk
	 * @param rooms
	 */
	public void saveRooms(Collection<Room> rooms) {
		this.rooms = rooms.stream()
				.collect(Collectors.toMap(
						room -> room.getRoom(), 
						room -> room
					));
		saveRoomsToDisk(rooms, diskFile);
	}
	
	/**Saves the rooms to the disk
	 * @param rooms the rooms to save
	 * @param diskFile the diskfile to save to
	 */
	private static void saveRoomsToDisk(Collection<Room> rooms, File diskFile) {
		logger.trace("RoomServices saveRoomsToDisk() invoked");
		try (FileWriter writer = new FileWriter(diskFile)) {
			gson.toJson(rooms, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	/**Gets the filepath for the known rooms
	 * @param fileName filename to query for
	 * @return filePath of directory
	 */
	private String getFilePath(String fileName) {
		logger.trace("RoomServices getFilePath() invoked");
		
		String dir = getLaunchDir();
		if (dir.equals("")) {
			return null;
		}
		
		return dir + File.separator + fileName;
	}
	
	/**Gets the file object of the room database
	 * @param filePath the filepath to get or create a new file for
	 * @return file object found on disk or newly created
	 */
	private File getFile(String filePath) {
		logger.trace("RoomServices getFile() invoked");
		
		File tmp = new File(filePath);
		
		boolean exists = tmp.exists();
		if (!exists) {
			try {
				tmp.createNewFile();
				writeEmptyListJson(tmp);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} 
		return tmp;
	}
	
	/**Writes an empty json object to the file
	 * @param f file to write to
	 */
	private static void writeEmptyListJson(File f) {
		logger.trace("RoomServices writeEmptyJson() invoked");
		try (FileWriter writer = new FileWriter(f)) {
            writer.write("[\n]\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	/**Gets the launch directory of the class (either in jar or compiled)
	 * @return disk location where the java class was called from
	 */
	private static String getLaunchDir() {
		logger.trace("RoomServices getLaunchDir() invoked");
		
		ProtectionDomain domain = RoomServices.class.getProtectionDomain();
		CodeSource source = domain.getCodeSource();
		
		if (source == null) {
			logger.error("Unable to determine code source");
			return "";
		} 
		
		URL url = source.getLocation();
		String dir = new File(url.getPath()).getParent();
		dir = dir.replace("%20", " ");
		return dir;
	}
}
