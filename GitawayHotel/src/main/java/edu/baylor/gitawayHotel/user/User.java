package edu.baylor.gitawayHotel.user;

import java.util.Objects;

import com.google.gson.JsonObject;

public class User {
	private String username;
	private String password;
	private UserType userType;
	
	public User() {
		
	}
	
	
	
	public User(String username) {
		this.username = username;
	}

	public User(JsonObject jsonObject) { // for use with UserAdapter
		this.username = jsonObject.get("username").getAsString();
    	this.password = jsonObject.get("password").getAsString();
		this.userType = UserType.valueOf(jsonObject.get("userType").getAsString().toUpperCase());
	}
	
	public User(String username, String password, UserType userType) {
		this.username = username;
		this.password = password;
		this.userType = userType;
	}

	public String getUsername() {
		return username;
	}

	protected void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	protected void setPassword(String password) {
		this.password = password;
	}

	public UserType getUserType() {
		return userType;
	}

	protected void setUserType(UserType userType) {
		this.userType = userType;
	}

	@Override
	public int hashCode() {
		return Objects.hash(username);
	}

	/**A user is defined as unique by the username alone 
	 * their password is really a transient field that can change
	 *
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof User))
			return false;
		User other = (User) obj;
		return Objects.equals(username, other.username);
	}
	
	
}
