package edu.baylor.gitawayHotel.login;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;

import edu.baylor.gitawayHotel.user.User;
import edu.baylor.gitawayHotel.user.UserType;

/**Class related to servicing users
 * @author Nathan
 *
 */
public class UserServices {
	private static final Logger logger = LogManager.getLogger(UserServices.class);
	private static final String FILENAME = "users.json";
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
	
	public boolean isUsernameValid(String username) {
		return !isUsernameAvailable(username);
	}
	
	/**Gets if the username and password is valid in the system
	 * @param username the username to check
	 * @param password the password to check
	 * @return true if the user is authenticated, false if they are not
	 */
	public boolean isSuccessfulLogin(String username, String password) {
		User user = users.get(username);
		
		if (user == null) {
			return false;
		}
		
		return user.getPassword().equals(password);
	}
	
	public UserType getUserType(String username) {
		User user = users.get(username);
		
		if (user == null) {
			return null;
		}
		
		return user.getUserType();
	}
	
	/**Gets the map of users by username that are present in the file
	 * @return map of users or blank map for file error
	 */
	private static Map<String, User> loadUsers(File file) {
		try (FileReader reader = new FileReader(file)) {
			Gson gson = new Gson();
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
		logger.trace("UserServices writeEmptyJson() invoked");
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
		logger.trace("DefaultFileReader getLaunchDir() invoked");
		
		ProtectionDomain domain = UserServices.class.getProtectionDomain();
		CodeSource source = domain.getCodeSource();
		
		if (source == null) {
			logger.error("Unable to determine code source");
			return "";
		} 
		
		URL url = source.getLocation();
		String dir = new File(url.getPath()).getParent();
		return dir;
	}
}
