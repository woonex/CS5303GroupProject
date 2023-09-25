package edu.baylor.gitawayHotel.user;

import com.google.gson.annotations.SerializedName;

public enum UserType {
	@SerializedName("admin")
	ADMIN,
	
	@SerializedName("clerk")
	HOTEL_CLERK,
	
	@SerializedName("guest")
	GUEST
}
