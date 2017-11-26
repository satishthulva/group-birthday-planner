package com.novice.hbdr.web.datamodels;

import com.novice.hbdr.datamodels.Birthday;
import com.novice.hbdr.datamodels.Gender;
import com.novice.hbdr.datamodels.GroupID;
import com.novice.hbdr.datamodels.Person;

/**
 * Information about a person to show in the UI
 * 
 * @author satish
 */
public class PersonDTO extends Person {
	/**
	 * Unique id of the group the person belongs to
	 */
	private GroupID groupID;
	/**
	 * Name of the group the person belongs to
	 */
	private String groupName;
	
	public PersonDTO(String name, String petName, Birthday birthday, String email, Gender gender, GroupID groupID,
			String groupName) {
		super(name, petName, birthday, email, gender);
		this.groupID = groupID;
		this.groupName = groupName;
	}

	public PersonDTO(GroupID groupID, String groupName, Person person) {
		super(person.getName(), person.getPetName(), person.getBirthday(), person.getEmail(), person.getGender());
		this.groupID = groupID;
		this.groupName = groupName;
	}
	
	/**
	 * @return the groupID
	 */
	public GroupID getGroupID() {
		return groupID;
	}

	/**
	 * @return the groupName
	 */
	public String getGroupName() {
		return groupName;
	}
	
}
