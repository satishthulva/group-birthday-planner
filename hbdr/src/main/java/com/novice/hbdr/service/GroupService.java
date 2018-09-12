package com.novice.hbdr.service;

import java.util.List;

import com.novice.hbdr.datamodels.Group;
import com.novice.hbdr.datamodels.GroupDetails;
import com.novice.hbdr.datamodels.GroupID;
import com.novice.hbdr.datamodels.UserID;

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
	 * @param name		             Name to address the group
	 * @param members	             Members of the group
	 * @param reminderPeriodInDays   Number of days in advance the reminder has to be sent
	 * 
	 * @return unique id generated for the group
	 */
	GroupID registerGroup(String name, List<UserID> members, int reminderPeriodInDays);
	
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

	/**
	 * Find all the groups given user is a member of
	 * 
	 * @param userID   Unique id of the user
	 * @return all the groups given user is a member of
	 */
	List<GroupDetails> findGroups(UserID userID);
	
}
