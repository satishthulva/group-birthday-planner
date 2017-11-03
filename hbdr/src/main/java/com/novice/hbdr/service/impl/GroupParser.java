/*
 * FriendGroupParser.java
 */
package com.novice.hbdr.service.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.novice.hbdr.datamodels.Birthday;
import com.novice.hbdr.datamodels.Gender;
import com.novice.hbdr.datamodels.Group;
import com.novice.hbdr.datamodels.GroupID;
import com.novice.hbdr.datamodels.Month;
import com.novice.hbdr.datamodels.Person;

/**
 * Parse an input file containing information of a friends group
 * 
 * @author satishbabu
 */
public class GroupParser
{
    /**
     * Comment lines in the input line should start with this
     */
    private static final String COMMENT_PREFIX = "#";
    /**
     * Fields should be separated by tab, for each line, in the input file
     */
    private static final String FIELD_SEPARATOR = "\t";

    /**
     * From the file representing a group of friends, parse their data
     * 
     * @param input File containing information about group of friends
     * @return  Parsed structured information about everyone in the group
     * @throws IOException
     */
    public Group parsePersons(File input) throws IOException
    {
        return parsePersons(new FileInputStream(input));
    }
    
    /**
     * <pre>
     * From the input stream representing a group of friends, parse their data.
     * 
     * Expected format.
     * 
     * Comment lines first which start with #
     * 
     * GroupId on a separate line next
     * 
     * Group name on a separate line next
     * 
     * Group members one each on a new line
     * 
     * </pre>
     * @param input input stream containing information about group of friends
     * @return  Parsed structured information about everyone in the group
     * @throws IOException
     */
    public Group parsePersons(InputStream input) throws IOException
    {
    	GroupID groupID = null;
    	String groupName = null;
        List<Person> persons = new ArrayList<>();
        
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(input)))
        {
            String line = null;
            
            while((line = reader.readLine()) != null && line.startsWith(COMMENT_PREFIX));
            
            // group id
            groupID = new GroupID(line.trim());
            
            // group name
            line = reader.readLine();
            groupName = line.trim();
            
            while((line = reader.readLine()) != null)
            {                
                persons.add(parsePersonFromLine(line));
            }
        }
        
        return new Group(groupID, groupName, persons);
    }
    
    /**
     * Parse details of a single person
     * @param line  The record with details of person
     * @return  Parsed details of person
     */
    private Person parsePersonFromLine(String line)
    {
        String[] fields = line.split(FIELD_SEPARATOR, -1);
        //    0         1       2             3       4       5
        //  Name    PetName Day of month    Month   email   Gender
        
        return new Person(fields[0], fields[1], new Birthday(Integer.parseInt(fields[2]), Month.toMonth(fields[3])), fields[4], Gender.toGender(fields[5]));
    }
    
    
    public static void main(String[] args) throws Exception
    {
        File file = new File("/home/satishbabu/birthday_reminder/data/birthdays_friends_strand_extended.tsv");
        GroupParser parser = new GroupParser();
        parser.parsePersons(file);
    }
    
}
