package com.novice.hbdr.service;

import java.util.List;

import com.novice.hbdr.datamodels.Group;
import com.novice.hbdr.datamodels.GroupID;
import com.novice.hbdr.datamodels.Person;

/**
 * API managing friend groups data
 * 
 * @author satish
 *
 */
public interface GroupService {

	/**
	 * Register a new group with given details
	 * 
	 * @param name		Name to address the group
	 * @param members	Members of the group
	 * 
	 * @return unique id generated for the group
	 */
	GroupID registerGroup(String name, List<Person> members);
	
	/**
	 * Find friends group with given unique id
	 * 
	 * @param groupID	Unique id of the group
	 * @return	friends group with given unique id
	 */
	Group findGroup(GroupID groupID);
	
	/**
	 * Find all friend groups registered with the system
	 * 
	 * @return	all friend groups registered with the system
	 */
	List<Group> findGroups();
	
}
