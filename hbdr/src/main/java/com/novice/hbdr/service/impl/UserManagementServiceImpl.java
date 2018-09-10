package com.novice.hbdr.service.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.novice.hbdr.datamodels.Person;
import com.novice.hbdr.service.UserManagementService;

/**
 * Implements {@link UserManagementService}
 * 
 * @author satish
 */
public class UserManagementServiceImpl implements UserManagementService {

	private Map<String, Person> emailToPersonMap = new ConcurrentHashMap<>();
	
	@Override
	public void registerUser(Person person) {
		emailToPersonMap.put(person.getEmail(), person);
	}

	/* (non-Javadoc)
	 * @see com.novice.hbdr.service.UserManagementService#findPerson(java.lang.String)
	 */
	@Override
	public Person findPerson(String email) {
		return emailToPersonMap.get(email);
	}

}
