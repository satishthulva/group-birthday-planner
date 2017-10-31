/*
 * Gender.java
 */
package com.novice.hbdr.datamodels;

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