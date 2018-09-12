package com.novice.hbdr.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.inject.Inject;
import com.novice.hbdr.Configuration;
import com.novice.hbdr.datamodels.Group;
import com.novice.hbdr.datamodels.GroupDetails;
import com.novice.hbdr.datamodels.GroupID;
import com.novice.hbdr.datamodels.Person;
import com.novice.hbdr.datamodels.UserID;
import com.novice.hbdr.service.GroupService;
import com.novice.hbdr.service.UserManagementService;

public class GroupServiceImpl implements GroupService {

    /**
     * Group id to details map
     */
    private Map<GroupID, GroupDetailsWithMembers> groupMap = new HashMap<>();
	/**
	 * Configuration to get storage root
	 */
	private final Configuration configuration;
	/**
	 * User management service
	 */
	private final UserManagementService userService;
	
	@Inject
	public GroupServiceImpl(Configuration configuration, UserManagementService userManagementService) {
		this.configuration = configuration;
		this.userService = userManagementService;
		
		// initialize cache
		initCache();
	}

	/**
	 * @return new group id for the group to come in
	 */
	private GroupID generateNewGroupID() {
		return new GroupID(System.nanoTime() + "");
	}

	/*
	 * (non-Javadoc)
	 * @see com.novice.hbdr.service.GroupService#registerGroup(java.lang.String, java.util.List, int)
	 */
	@Override
	public GroupID registerGroup(String name, List<UserID> members, int reminderPeriodInDays) {
		GroupID groupID = generateNewGroupID();

		GroupDetailsWithMembers groupDetails = new GroupDetailsWithMembers(groupID, name, reminderPeriodInDays, members);
		groupMap.put(groupID, groupDetails);
		
		return groupID;
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
	    
	    for(Entry<GroupID, GroupDetailsWithMembers> groupEntry : groupMap.entrySet())
	    {
	        GroupID groupID = groupEntry.getKey();
	        GroupDetailsWithMembers groupDetails = groupEntry.getValue();
	        
	        List<Person> people = new ArrayList<>(userService.findPeople(groupDetails.getMembers()));
	        
	        Group group = new Group(groupID, groupDetails.getName(), groupDetails.getReminderPeriodInDays(), people);
	        groups.add(group);
	    }
	    
	    return groups;
	}

	/**
	 * Initialize group data cache reading data from persistent storage
	 */
	private void initCache()
	{
	    for(Group group : findGroupsFromPersistentStorage())
	    {
	        Collection<UserID> members = new ArrayList<>();
	        for(Person person : group.getMembers())
	            members.add(new UserID(person.getEmail()));
	        
	        GroupDetailsWithMembers groupDetailsWithMembers = new GroupDetailsWithMembers(group.getId(), group.getName(), group.getReminderPeriodInDays(), members);
	        
	        groupMap.put(group.getId(), groupDetailsWithMembers);
	    }
	}
	
	/**
	 * Method to return groups data from perstistent storage
	 * @return groups data from perstistent storage
	 */
	private List<Group> findGroupsFromPersistentStorage()
	{
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
	
    /* (non-Javadoc)
     * @see com.novice.hbdr.service.GroupService#findGroups(com.novice.hbdr.datamodels.UserID)
     */
    @Override
    public List<GroupDetails> findGroups(UserID userID)
    {
        List<Group> allGroups = findGroups();
        List<GroupDetails> userGroups = new ArrayList<>();
        for(Group group : allGroups)
        {
            if(isGroupMember(group, userID))
                userGroups.add(group);
        }
        
        return userGroups;
    }

    /**
     * Find if the user is member of given group
     * @param group     Group to test membership against
     * @param userID    Unique id of the user
     * @return  <code>true</code>, if the user is a member of given group. <code>false</code>, otherwise.
     */
    private boolean isGroupMember(Group group, UserID userID)
    {
        for(Person person : group.getMembers())
        {
            UserID personID = new UserID(person.getEmail());
            if(personID.equals(userID))
                return true;
        }
        
        return false;
    }
   
    /**
     * Members of the group in addition to the name etc
     * 
     * @author satishbabu
     */
    private static class GroupDetailsWithMembers extends GroupDetails
    {
        /**
         * members of the group
         */
        private final Collection<UserID> members = new ArrayList<>();
        
        public GroupDetailsWithMembers(GroupID id, String name, int reminderPeriodInDays, Collection<UserID> members)
        {
            super(id, name, reminderPeriodInDays);
            if(members != null)
                this.members.addAll(members);
        }

        /**
         * @return the members
         */
        public Collection<UserID> getMembers()
        {
            return members;
        }
    }
    
}
