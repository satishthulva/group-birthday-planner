package com.novice.hbdr.datamodels;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a friends group
 * 
 * @author satish
 */
public class Group extends GroupDetails {
	/**
	 * Members part of the group
	 */
	private List<Person> members = new ArrayList<>();
	public Group(GroupID groupID, String name, int reminderPeriodInDays, List<Person> members) {
		super(groupID, name, reminderPeriodInDays);
		this.members = members;
	}

	/**
	 * @return the members
	 */
	public List<Person> getMembers() {
		return members;
	}
	
}
