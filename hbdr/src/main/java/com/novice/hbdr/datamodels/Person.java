/*
 * Person.java
 */
package com.novice.hbdr.datamodels;

/**
 * Information about each person in the group. 
 * 
 * @author satishbabu
 */
public class Person
{
    /**
     * Name of the person. Give full name or first name or whatever you're comfortable with
     */
    private String name;
    /**
     * Any cute name you got for this person with in the group !
     */
    private String petName;
    /**
     * When is this persons' happy birthday ?
     */
    private Birthday birthday;
    /**
     * Psst! if we want to send you an email, where can we send it to
     */
    private String email;
    /**
     * Gender
     */
    private Gender gender;
    
    public Person(String name, String petName, Birthday birthday, String email, Gender gender)
    {
        super();
        this.name = name;
        this.petName = petName;
        this.birthday = birthday;
        this.email = email;
        this.gender = gender;
    }

    /**
     * @return the name
     */
    public String getName()
    {
        return name;
    }

    /**
     * @return the petName
     */
    public String getPetName()
    {
        return petName;
    }

    /**
     * @return the birthday
     */
    public Birthday getBirthday()
    {
        return birthday;
    }

    /**
     * @return the email
     */
    public String getEmail()
    {
        return email;
    }

    /**
     * @return the gender
     */
    public Gender getGender()
    {
        return gender;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((email == null) ? 0 : email.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (!(obj instanceof Person))
        {
            return false;
        }
        Person other = (Person) obj;
        if (email == null)
        {
            if (other.email != null)
            {
                return false;
            }
        }
        else if (!email.equals(other.email))
        {
            return false;
        }
        if (name == null)
        {
            if (other.name != null)
            {
                return false;
            }
        }
        else if (!name.equals(other.name))
        {
            return false;
        }
        return true;
    }
    
}