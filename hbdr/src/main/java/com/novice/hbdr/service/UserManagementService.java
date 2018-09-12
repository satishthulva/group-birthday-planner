package com.novice.hbdr.service;

import java.util.Collection;

import com.novice.hbdr.datamodels.Birthday;
import com.novice.hbdr.datamodels.Gender;
import com.novice.hbdr.datamodels.Person;
import com.novice.hbdr.datamodels.UserID;

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
    
    /**
     * Find the person with given ID
     * 
     * @param userID Unique Id of the person
     * @return  the person with given id, if any
     */
    Person findPerson(UserID userID);
 
    /**
     * Find people with given userID
     * 
     * @param userIDs   Unique ids of people to find
     * @return  people with given userID
     */
    Collection<Person> findPeople(Collection<UserID> userIDs);
    
    /**
     * Update details about the person with given ID
     * 
     * @param userID    Unique id of the person
     * @param name      Updated name of the person
     * @param petName   Updated pet name of the person
     * @param birthday  Updated birthday of the person
     * @param gender    Updated gender of the person
     */
    void updateDetails(UserID userID, String name, String petName, Birthday birthday, Gender gender);
    
}
