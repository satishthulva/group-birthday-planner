package com.novice.hbdr.datamodels;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a friends group
 * 
 * @author satish
 */
public class Group {

	/**
	 * Unique id for the group
	 */
	private GroupID id;
	
	/**
	 * Name to address the group
	 */
	private String name;
	
	/**
	 * Members part of the group
	 */
	private List<Person> members = new ArrayList<>();

	public Group(GroupID groupID, String name, List<Person> members) {
		super();
		this.id = groupID;
		this.name = name;
		this.members = members;
	}

	/**
	 * @return the id
	 */
	public GroupID getId() {
		return id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the members
	 */
	public List<Person> getMembers() {
		return members;
	}
	
}
