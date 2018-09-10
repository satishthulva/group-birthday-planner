package com.novice.hbdr.datamodels;

/**
 * Represents user in the system
 * 
 * @author satish
 */
public class User {

	/**
	 * Unique id of user
	 */
	private final UserID userID;
	/**
	 * Details of the user
	 */
	private final Person person;
	/**
	 * Status of the user account
	 */
	private final UserStatus status;
	/**
	 * @param userID	Unique id of user
	 * @param person	Details of the user
	 * @param status	Status of the user account
	 */
	public User(UserID userID, Person person, UserStatus status) {
		super();
		this.userID = userID;
		this.person = person;
		this.status = status;
	}
	/**
	 * @return the userID
	 */
	public UserID getUserID() {
		return userID;
	}
	/**
	 * @return the person
	 */
	public Person getPerson() {
		return person;
	}
	/**
	 * @return the status
	 */
	public UserStatus getStatus() {
		return status;
	}
}
