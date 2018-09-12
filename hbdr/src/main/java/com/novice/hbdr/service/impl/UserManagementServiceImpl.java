package com.novice.hbdr.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.novice.hbdr.datamodels.Birthday;
import com.novice.hbdr.datamodels.Gender;
import com.novice.hbdr.datamodels.Person;
import com.novice.hbdr.datamodels.UserID;
import com.novice.hbdr.service.UserManagementService;

/**
 * Implements {@link UserManagementService}
 * 
 * @author satish
 */
public class UserManagementServiceImpl implements UserManagementService {

	private Map<UserID, Person> personMap = new ConcurrentHashMap<>();
	
	@Override
	public void registerUser(Person person) {
	    UserID userID = new UserID(person.getEmail());
		personMap.put(userID, person);
	}

	/* (non-Javadoc)
	 * @see com.novice.hbdr.service.UserManagementService#findPerson(java.lang.String)
	 */
	@Override
	public Person findPerson(String email) {
	    UserID userID = new UserID(email);
		return personMap.get(userID);
	}

    /* (non-Javadoc)
     * @see com.novice.hbdr.service.UserManagementService#findPerson(com.novice.hbdr.datamodels.UserID)
     */
    @Override
    public Person findPerson(UserID userID)
    {
        return personMap.get(userID);
    }

    /* (non-Javadoc)
     * @see com.novice.hbdr.service.UserManagementService#findPeople(java.util.Collection)
     */
    @Override
    public Collection<Person> findPeople(Collection<UserID> userIDs)
    {
        List<Person> people = new ArrayList<>();
        
        for(UserID userID : userIDs)
        {
            Person person = findPerson(userID);
            if(person != null)
                people.add(person);
        }
        
        return people;
    }

    /* (non-Javadoc)
     * @see com.novice.hbdr.service.UserManagementService#updateDetails(com.novice.hbdr.datamodels.UserID, java.lang.String, java.lang.String, com.novice.hbdr.datamodels.Birthday, com.novice.hbdr.datamodels.Gender)
     */
    @Override
    public void updateDetails(UserID userID, String name, String petName, Birthday birthday, Gender gender)
    {
        Person oldPerson = personMap.get(userID);
        String email = oldPerson.getEmail();
        Person newPerson = new Person(name, petName, birthday, email, gender);
        personMap.put(userID, newPerson); // TODO : the problem here --> how to update the person data that is stored with the group
    }
	
}
