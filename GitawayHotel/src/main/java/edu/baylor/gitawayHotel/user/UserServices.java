package edu.baylor.gitawayHotel.user;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.management.InstanceAlreadyExistsException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

/**Class related to servicing users
 * includes saving users to disk; updating password for the user
 * @author Nathan
 *
 */
public class UserServices {
	private static final Logger logger = LogManager.getLogger(UserServices.class);
	private static final String FILENAME = "users.json";
	private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
	private static final List<User> defaultUsers = List.of(
			new User("admin", "password", UserType.ADMIN),
			new User("clerk", "password", UserType.HOTEL_CLERK),
			new User("guest", "password", UserType.GUEST)
			);
	
	private File diskFile;
	private Map<String, User> users;
	
	public UserServices() {
		String filePath = getFilePath(FILENAME);
		this.diskFile = getFile(filePath);
		this.users = loadUsers(this.diskFile);
	}
	
	/**Gets if the username is available (i.e. the username does not already exist
	 * @param username desired username for new user
	 * @return true if the username is available, false if it is not
	 */
	public boolean isUsernameAvailable(String username) {
		logger.trace("UserServices isUsernameAvailable() invoked");
		
		boolean usernameTaken = users.containsKey(username);
		
		return !usernameTaken;
	}
	
	/**Gets if the username is valid (the user exists in the system)
	 * @param username the username to check
	 * @return true if the user is valid, false if it is not
	 */
	public boolean isUsernameValid(String username) {
		return !isUsernameAvailable(username);
	}
	
	/**Updates the user's password by username
	 * @param username the username to modify the password for
	 * @param newPw the new password
	 * @return the updated user object
	 */
	public User updateUser(String username, String newPw) {
		logger.trace("UserServices updateUser() invoked");
		return updateUser(users.get(username), newPw);
	}
	
	/**Updates the user's password
	 * @param user the user to update the password for
	 * @param newPw the new password
	 * @return the updated user object
	 */
	public User updateUser(User user, String newPw) {
		logger.trace("UserServices updateUser() invoked");
		user.setPassword(newPw);
		
		saveUsersToDisk(this.users.values(), this.diskFile);
		
		return user;
	}
	
	/**Adds a user to the userlist
	 * @param username the username
	 * @param password the password
	 * @param userType the userType
	 * @return the created user object
	 * @throws InstanceAlreadyExistsException if the username is already taken
	 */
	public User addUser(String username, String password, UserType userType) throws InstanceAlreadyExistsException {
		logger.trace("UserServices addUser() invoked");
		if (!isUsernameAvailable(username)) {
			throw new InstanceAlreadyExistsException(username);
		}
		
		//default password if nothing entered
		if (password.equals("")) {
			password = "password";
		}
		
		//create the new user
		User user = new User();
		user.setUsername(username);
		user.setPassword(password);
		user.setUserType(userType);
		
		//add the user to the in-mem storage
		users.put(username, user);
		
		saveUsersToDisk(users.values(), diskFile);
		return user;
	}
	
	/**Saves the users to the disk
	 * @param users the users to save
	 * @param diskFile the diskfile to save to
	 */
	private static void saveUsersToDisk(Collection<User> users, File diskFile) {
		logger.trace("UserServices saveUsersToDisk() invoked");
		try (FileWriter writer = new FileWriter(diskFile)) {
			gson.toJson(users, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	/**Gets if the username and password is valid in the system
	 * @param username the username to check
	 * @param password the password to check
	 * @return true if the user is authenticated, false if they are not
	 */
	public boolean isSuccessfulLogin(String username, String password) {
		logger.trace("UserServices isSuccessfulLogin() invoked");
		User user = users.get(username);
		
		if (user == null) {
			return false;
		}
		
		return user.getPassword().equals(password);
	}
	
	/**Gets the usertype based on the username
	 * @param username the username
	 * @return the userType the user is authorized as
	 */
	public UserType getUserType(String username) {
		logger.trace("UserServices getUserType() invoked");
		User user = users.get(username);
		
		if (user == null) {
			return null;
		}
		
		return user.getUserType();
	}
	
	/**Gets the user on disk
	 * @param username
	 * @return
	 */
	public User getUser(String username) {
		logger.trace("UserServices getUser() invoked");
		User user = users.get(username);
		return user;
	}
	
	/**Gets the map of users by username that are present in the file
	 * @return map of users or blank map for file error
	 */
	private static Map<String, User> loadUsers(File file) {
		logger.trace("UserServices loadUsers() invoked");
		try (FileReader reader = new FileReader(file)) {
			User[] aUsers = gson.fromJson(reader, User[].class);
			
			List<User> users = Arrays.asList(aUsers);
			Map<String, User> usersByUsername = users.stream()
					.collect(Collectors.toMap(
							user -> user.getUsername(), 
							user -> user));
			
			return usersByUsername;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Map.of();
	}
	
	/**Gets the filepath for the known users
	 * @param fileName filename to query for
	 * @return filePath of directory
	 */
	private String getFilePath(String fileName) {
		logger.trace("UserServices getFilePath() invoked");
		
		String dir = getLaunchDir();
		if (dir.equals("")) {
			return null;
		}
		
		return dir + File.separator + fileName;
	}
	
	/**Gets the file object of the user database
	 * @param filePath the filepath to get or create a new file for
	 * @return file object found on disk or newly created
	 */
	private File getFile(String filePath) {
		logger.trace("UserServices getFile() invoked");
		
		File tmp = new File(filePath);
		
		boolean exists = tmp.exists();
		if (!exists) {
			try {
				tmp.createNewFile();
				writeDefaultJson(tmp);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} 
		return tmp;
	}
	
	/**Writes an empty json object to the file
	 * @param f file to write to
	 */
	private static void writeDefaultJson(File f) {
		
		logger.trace("UserServices writeEmptyJson() invoked");
		try (FileWriter writer = new FileWriter(f)) {
			gson.toJson(defaultUsers, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	/**Gets the launch directory of the class (either in jar or compiled)
	 * @return disk location where the java class was called from
	 */
	private static String getLaunchDir() {
		logger.trace("DefaultFileReader getLaunchDir() invoked");
		
		ProtectionDomain domain = UserServices.class.getProtectionDomain();
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

	/**Removes the user from the directory
	 * @param user
	 */
	public void removeUserByUsername(String username) {
		users.remove(username);
		
		saveUsersToDisk(users.values(), diskFile);
	}

	public Set<User> getAllGuests() {
		return users.values().stream()
				.filter(user -> UserType.GUEST.equals(user.getUserType()))
				.collect(Collectors.toSet());
	}
	
	public Set<User> getAllUsers() {
		return users.values().stream()
				.collect(Collectors.toSet());
	}
}
