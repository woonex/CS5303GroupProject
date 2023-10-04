package edu.baylor.gitawayHotel.user;

public class User {
	private String username;
	private String password;
	private UserType userType;
	
	public User() {
		
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
	
	
}
