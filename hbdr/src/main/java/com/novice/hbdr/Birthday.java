/*
 * Birthday.java
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
    
}