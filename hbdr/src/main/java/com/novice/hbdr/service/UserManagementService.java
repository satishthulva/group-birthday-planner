package com.novice.hbdr.service;

import com.novice.hbdr.datamodels.Person;

/**
 * Manages all the registered users in the system. 
 * 
 * @author satish
 */
public interface UserManagementService {

	/**
	 * Register a new user in the system
	 * 
	 * @param person	Details of the new user
	 */
	void registerUser(Person person);
	
	/**
	 * Find the person with given email
	 * 
	 * @param email	Email Id of the person
	 * @return	the person with given email, if any
	 */
	Person findPerson(String email);
	
}
