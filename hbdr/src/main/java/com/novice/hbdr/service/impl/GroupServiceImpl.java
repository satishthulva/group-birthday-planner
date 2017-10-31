package com.novice.hbdr.service.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.inject.Inject;
import com.novice.hbdr.Configuration;
import com.novice.hbdr.datamodels.Birthday;
import com.novice.hbdr.datamodels.Group;
import com.novice.hbdr.datamodels.GroupID;
import com.novice.hbdr.datamodels.Person;
import com.novice.hbdr.service.GroupService;

public class GroupServiceImpl implements GroupService {

	/**
	 * Configuration to get storage root
	 */
	private final Configuration configuration;

	@Inject
	public GroupServiceImpl(Configuration configuration) {
		this.configuration = configuration;
	}

	/**
	 * @return new group id for the group to come in
	 */
	private GroupID generateNewGroupID() {
		return new GroupID(System.nanoTime() + "");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.novice.hbdr.service.GroupService#registerGroup(java.lang.String,
	 * java.util.List)
	 */
	@Override
	public void registerGroup(String name, List<Person> members) {
		GroupID groupID = generateNewGroupID();
		File groupFile = new File(configuration.findStorageRoot(), groupID.getId());

		try (BufferedWriter writer = new BufferedWriter(new FileWriter(groupFile))) {

			// id first
			writer.write(groupID.getId());
			writer.newLine();

			// name next
			writer.write(name);
			writer.newLine();

			String FIELD_SEPARATOR = "\t";
			// people next
			for (Person person : members) {
				// name	petName	day	month	email	gender
				StringBuilder builder = new StringBuilder();
				
				builder.append(person.getName());
				builder.append(FIELD_SEPARATOR);
				
				builder.append(person.getPetName());
				builder.append(FIELD_SEPARATOR);
				
				Birthday birthday = person.getBirthday();
				builder.append(birthday.getDayOfMonth());
				builder.append(FIELD_SEPARATOR);
				
				builder.append(birthday.getMonth().name());
				builder.append(FIELD_SEPARATOR);
				
				builder.append(person.getEmail());
				builder.append(FIELD_SEPARATOR);
				
				builder.append(person.getGender().name());
				
				writer.write(builder.toString());
				writer.newLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.novice.hbdr.service.GroupService#findGroup(com.novice.hbdr.datamodels
	 * .GroupID)
	 */
	@Override
	public Group findGroup(GroupID groupID) {
		for(Group group : findGroups()) {
			if(group.getId().equals(groupID)) {
				return group;
			}
		}
		
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.novice.hbdr.service.GroupService#findGroups()
	 */
	@Override
	public List<Group> findGroups() {
		List<Group> groups = new ArrayList<>();
		
		File[] files = configuration.findStorageRoot().listFiles();
		if(files == null || files.length == 0)
			return groups;
		
		GroupParser parser = new GroupParser();
		
		try {
			for(File file : files) {
				Group group = parser.parsePersons(file);
				groups.add(group);
			}
			
			return groups;
		} catch(IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage(), e);
		}
	}

}
