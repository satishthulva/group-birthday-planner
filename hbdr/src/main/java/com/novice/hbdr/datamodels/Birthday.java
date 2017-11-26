/*
 * Birthday.java
 */
package com.novice.hbdr.datamodels;

/**
 * Birth day of a person. We take month and specific day of the month for now.
 * 
 * @author satishbabu
 */
public class Birthday
{
    /**
     * Which day of the month
     */
    private int dayOfMonth;
    /**
     * Which month
     */
    private Month month;

    /**
     * @param dayOfMonth
     * @param month
     */
    public Birthday(int dayOfMonth, Month month)
    {
        super();
        this.dayOfMonth = dayOfMonth;
        this.month = month;
    }

    /**
     * @return the dayOfMonth
     */
    public int getDayOfMonth()
    {
        return dayOfMonth;
    }

    /**
     * @return the month
     */
    public Month getMonth()
    {
        return month;
    }
    
    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
    	return dayOfMonth + " " + month.name();
    }
    
}