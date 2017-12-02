package com.novice.hbdr.service;

import java.util.List;

import com.novice.hbdr.datamodels.Group;
import com.novice.hbdr.datamodels.Person;

/**
 * Provides API to search for people and groups based on different search criteria like name, birthday etc
 * 
 * @author satish
 */
public interface SearchService {

	/**
	 * Find people with given name from all groups
	 * 
	 * @param name	Name of interest
	 * @return	people with given name from all groups
	 */
	List<Person> findPeopleWithName(String name);
	
	/**
	 * Add given group data to index
	 * 
	 * @param group	Group to add
	 */
	void addGroup(Group group);
	
}
