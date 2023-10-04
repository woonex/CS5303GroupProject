package edu.baylor.gitawayHotel.user;

import com.google.gson.annotations.SerializedName;

public enum UserType {
	@SerializedName("admin")
	ADMIN ("Admin"),
	
	@SerializedName("clerk")
	HOTEL_CLERK ("Hotel Clerk"),
	
	@SerializedName("guest")
	GUEST ("Guest");

	private final String prettyName;
	UserType(String prettyName) {
		this.prettyName = prettyName;
	}
	
	@Override
	public String toString() {
		return this.prettyName;
	}
}
