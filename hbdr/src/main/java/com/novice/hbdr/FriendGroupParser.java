/*
 * FriendGroupParser.java
 */
package com.novice.hbdr;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Parse an input file containing information of a friends group
 * 
 * @author satishbabu
 */
public class FriendGroupParser
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
    public List<Person> parsePersons(File input) throws IOException
    {
        return parsePersons(new FileInputStream(input));
    }
    
    /**
     * From the input stream representing a group of friends, parse their data
     * 
     * @param input input stream containing information about group of friends
     * @return  Parsed structured information about everyone in the group
     * @throws IOException
     */
    public List<Person> parsePersons(InputStream input) throws IOException
    {
        List<Person> persons = new ArrayList<>();
        
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(input)))
        {
            String line = null;
            
            while((line = reader.readLine()) != null)
            {
                if(line.startsWith(COMMENT_PREFIX))
                    continue;
                
                persons.add(parsePersonFromLine(line));
            }
        }
        
        return persons;
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
        FriendGroupParser parser = new FriendGroupParser();
        parser.parsePersons(file);
    }
    
}
