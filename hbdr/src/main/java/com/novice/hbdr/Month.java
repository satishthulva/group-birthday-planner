/*
 * Month.java
 */
package com.novice.hbdr;

/**
 * Month of the year
 * 
 * @author satishbabu
 */
public enum Month
{
    January,
    February,
    March,
    April,
    May,
    June,
    July,
    August,
    September,
    October,
    November,
    December;
    
    /**
     * @return <code>true</code>, if given year is a leap year. <code>false</code>, otherwise
     */
    private static boolean isLeapYear(int year)
    {
        if(year % 100 == 0 && year % 400 != 0)
            return false;
        
        return year % 4 == 0;
    }
    
    /**
     * @return Number of days in this month. We need to take year into consideration to handle {@link Month#February} properly for leap year.
     */
    public int getNumberOfDaysInMonth(int year)
    {
        switch (this)
        {
            case April:
                return 30;
                
            case August:
                return 31;
                
            case December:
                return 31;
                
            case February:
                if(isLeapYear(year))
                    return 29;
                else
                    return 28;
                
            case January:
                return 31;
            
            case July:
                return 31;
                
            case June:
                return 30;
                
            case March:
                return 31;
                
            case May:
                return 31;
                
            case November:
                return 30;
                
            case October:
                return 31;
                
            case September:
                return 30;
                
            default:
                throw new RuntimeException("month " + name() + " is not handled to get number of days");
        }
    }
    
    /**
     * @return Month
     */
    public static Month toMonth(String name)
    {
        for(Month month : Month.values())
        {
            if(name.equalsIgnoreCase(month.name()))
                return month;
        }
        
        throw new RuntimeException("Month " + name + " is not valid");
    }
}