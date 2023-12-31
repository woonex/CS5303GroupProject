package edu.baylor.gitawayHotel.reservation;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import edu.baylor.gitawayHotel.Room.Room;
import edu.baylor.gitawayHotel.Room.RoomServices;
import edu.baylor.gitawayHotel.user.User;

/**Manages the reservations for the hotel
 * @author Nathan
 *
 */
public class ReservationService {
	private static final Logger logger = LogManager.getLogger(ReservationService.class);
	private static final String FILENAME = "reservations.json";
	

	private File diskFile;
	private Map<Room, List<Reservation>> reservations;
	private List<Reservation> canceled = new ArrayList<Reservation>();
	private RoomServices roomServices;

	private static final Gson gson = new GsonBuilder()
			.setPrettyPrinting()
			.registerTypeAdapter(User.class, new UserAdapter())
			.registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
			.create();

	/**Constructor
	 * @param roomServices the roomServices instance to manage rooms in hotel
	 */
	public ReservationService(RoomServices roomServices) {
		this.roomServices = roomServices;

		String filePath = getFilePath(FILENAME);
		this.diskFile = getFile(filePath);
		//writeEmptyListJson(diskFile); //todo remove
		this.reservations = loadReservations(this.diskFile, roomServices.getRooms());
	}
	
	/**Gets the unique rooms and the qty of them
	 * @return
	 */
	public Map<Room, Integer> getUniqueRoomTypes() {
		Map<Room, Integer> map = new HashMap<Room, Integer>();
		
		//loop over all rooms and get only their unique characteristics
		for (Room room : roomServices.getRooms()) {
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

	/**Gets the available rooms based on a provided startDate and endDate
	 * @param startDate the start date
	 * @param endDate the end date
	 * @return
	 */
	public Set<Room> getAvailableRooms(LocalDate startDate, LocalDate endDate) {
		//	logger.debug("Desired start date: " + startDate );
		//	logger.debug("Desired end date: " + endDate);
		Set<Room> available = new HashSet<Room>();
		for (Entry<Room, List<Reservation>> entry : reservations.entrySet()) {
			Room room = entry.getKey();
			if (roomServices.getRoomByNumber(room.getRoom()) == null) {
				continue;
			}
			List<Reservation> curReses = entry.getValue();
			boolean roomAvailable = true;
			for (Reservation res : curReses) {
				//				logger.debug("Res start: " + res.getStartDate());
				//				logger.debug("Res end: " + res.getEndDate());
				boolean startBeforeEnd = res.getStartDate().isBefore(endDate);
				boolean endAfterStart = res.getEndDate().isAfter(startDate);
				boolean item = startBeforeEnd && endAfterStart;//res.getStartDate().isBefore(endDate) || res.getEndDate().isAfter(startDate);
				if (item) {
					roomAvailable = false;
					break;
				}
			}
			if (roomAvailable) {
				available.add(room);
			}
		}
		
		return available;
	}
	
	/**Adds a reservation
	 * @param reservation
	 */
	public void addReservation(Reservation reservation) {
		//TODO throw exception if cannot
		
		List<Reservation> roomReservations = this.reservations.get(reservation.getRoom());
		if (roomReservations == null) {
			for (Room room : roomServices.getRooms()) {
				if (Room.satisfiesRequest(room, reservation.getRoom())) {
					roomReservations = this.reservations.get(room);
					
					boolean isAlreadyReserved = false;
					for (Reservation existingRes : roomReservations) {
						if (reservationOverlaps(reservation, existingRes)) {
							isAlreadyReserved=true;
							break;
						}
					}
					
					if (isAlreadyReserved) {
						continue;
					}
					reservation.setRoom(room);
					break;
				}
			}
		}
		if (roomReservations == null) {
			throw new NoSuchElementException("Could not locate suitable room");
		} else {
			for (Reservation existingRes : roomReservations) {
				if (reservationOverlaps(reservation, existingRes)) {
//					isAlreadyReserved=true;
					throw new RuntimeException("Reservation overlaps with another reservation");
				}
			}
		}
		
		
		
		roomReservations.add(reservation);
		
		saveReservations(getAllReservations());
	}
	
	public void removeReservation(Reservation reservation) {
		removeReservation(reservation, false);	
	}
	
	public void removeReservation(Reservation reservation, boolean bypassFee) {
		List<Reservation> roomRes = this.reservations.get(reservation.getRoom());
		
		Iterator<Reservation> itr = roomRes.iterator();
		while (itr.hasNext()) {
			Reservation current = itr.next();
			if (current.equals(reservation)) {
				itr.remove();
				break;
			}
		}
		
		if (!bypassFee) {
			if (reservation.willIncurCancellationFee()) {
				reservation.setCancelled();
				this.canceled.add(reservation);
			}
		}
		
		saveReservations(getAllReservations());
	}
	
	public List<Reservation> getReservationsByUser(User user) {
		return getReservationsByUser(user, false);
	}
	
	public List<Reservation> getReservationsByUser(User user, boolean includeCancelled) {
		List<Reservation> base;
		if (includeCancelled) {
			base = getAllReservations();
		} else {
			base = getReservations();
		}
		return base.stream()
				.filter(res -> res.getGuest().equals(user))
				.collect(Collectors.toList());
	}
	
	/**Gets whether a reservation overlaps another reservation
	 * @param one
	 * @param two
	 * @return
	 */
	public static boolean reservationOverlaps(Reservation one, Reservation two) {
		boolean startBeforeEnd = one.getStartDate().isBefore(two.getEndDate());
		boolean endAfterStart = one.getEndDate().isAfter(two.getStartDate());
		boolean item = startBeforeEnd && endAfterStart;//res.getStartDate().isBefore(endDate) || res.getEndDate().isAfter(startDate);
		return item;
	}
	
	/**Gets the reservations for the room
	 * @param room the room to get the reservation for
	 * @return
	 */
	public List<Reservation> getReservationsForRoom(Room room) {
		List<Reservation> tmp = this.reservations.get(room);
		if (tmp == null) {
			tmp = new ArrayList<Reservation>();
			this.reservations.put(room, tmp);
		}
		return tmp;
	}
	
	/**Gets all the reservations of the hotel
	 * @return
	 */
	public List<Reservation> getReservations() {
		//TODO possibly check each time if the roomServices has removed a room?
		return this.reservations.values().stream()
				.flatMap(List::stream)
				.sorted()
				.collect(Collectors.toList());
	}
	
	private List<Reservation> getAllReservations() {
		List<Reservation> all = new ArrayList<>(getReservations());
		all.addAll(getCanceledReservations());
		return all;
	}
	
	public List<Reservation> getCanceledReservations() {
		return this.canceled;
	}

	/**Gets the map of rooms by room number that are present in the file
	 * @return map of rooms or blank map for file error
	 */
	private Map<Room, List<Reservation>> loadReservations(File file, List<Room> rooms) {
		logger.trace("ReservationService loadReservations() invoked");
		try (FileReader reader = new FileReader(file)) {
			Reservation[] aReservations = gson.fromJson(reader, Reservation[].class);

			List<Reservation> reservations = Arrays.asList(aReservations);
			Map<Room, List<Reservation>> items = rooms.stream()
					.collect(Collectors.toMap(
							room -> room, 
							room -> new ArrayList<Reservation>()
							));

			for (Reservation res : reservations) {
				if (res.wasCancelled()) {
					canceled.add(res);
				} else {
					List<Reservation> tmp = items.get(res.getRoom());
					if (tmp == null) {
						tmp = new ArrayList<Reservation>();
						items.put(res.getRoom(), tmp);
					}
					tmp.add(res);
				}
			}
			return items;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Map.of();
	}

	/**saves the reservations
	 * @param reservations
	 */
	private void saveReservations(Collection<Reservation> reservations) {
		saveReservationsToDisk(reservations, diskFile);
	}

	/**Saves the rooms to the disk
	 * @param rooms the rooms to save
	 * @param diskFile the diskfile to save to
	 */
	private static void saveReservationsToDisk(Collection<Reservation> reservations, File diskFile) {
		logger.trace("ReservationService saveRoomsToDisk() invoked");
		try (FileWriter writer = new FileWriter(diskFile)) {
			gson.toJson(reservations, writer);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**Gets the filepath for the known rooms
	 * @param fileName filename to query for
	 * @return filePath of directory
	 */
	private String getFilePath(String fileName) {
		logger.trace("ReservationService getFilePath() invoked");

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
		logger.trace("ReservationService getFile() invoked");

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
		logger.trace("ReservationService writeEmptyJson() invoked");
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
		logger.trace("ReservationService getLaunchDir() invoked");

		ProtectionDomain domain = ReservationService.class.getProtectionDomain();
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

	public void addNewRoom(Room room) {
		this.reservations.put(room, new ArrayList<Reservation>());
	}

	public void updateRooms(List<Room> rooms) {
		for (Room room : rooms) {
			Iterator<Entry<Room, List<Reservation>>> itr = this.reservations.entrySet().iterator();
			
			while (itr.hasNext()) {
//			for (Entry<Room, List<Reservation>> entry : this.reservations.entrySet()) {
				Entry<Room, List<Reservation>> entry = itr.next();
				Room entryRoom = entry.getKey();
				if (room.getRoom() == entryRoom.getRoom()) {
					List<Reservation> tmp = entry.getValue();
					
					itr.remove();
					this.reservations.put(room, tmp);
					
					break;
				}
			}
		}
		
		
	}
}
