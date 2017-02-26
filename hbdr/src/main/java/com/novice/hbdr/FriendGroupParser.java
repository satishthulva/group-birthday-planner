/*
 * FriendGroupParser.java
 *
 * Product: SCG
 * Strand Clinical Genomics
 *
 * Copyright 2007-2017, Strand Life Sciences
 * 5th Floor, Kirloskar Business Park, 
 * Bellary Road, Hebbal,
 * Bangalore 560024
 * India
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Strand Life Sciences., ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Strand Life Sciences.
 */
package com.novice.hbdr;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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
        List<Person> persons = new ArrayList<>();
        
        try(BufferedReader reader = new BufferedReader(new FileReader(input)))
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
    
}
