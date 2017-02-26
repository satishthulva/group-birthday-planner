/*
 * Gender.java
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

/**
 * Persons' gender
 * 
 * @author satishbabu
 */
public enum Gender
{
    Male,
    Female;
    
    /**
     * @return Gender
     */
    public static Gender toGender(String gender)
    {
        for(Gender one : values())
        {
            if(one.name().equalsIgnoreCase(gender))
                return one;
        }
        
        throw new RuntimeException("Unknown gender " + gender);
    }
}