package com.novice.hbdr;

import java.io.File;
import java.sql.Connection;

import javax.mail.Session;

/**
 * Settings for the app
 * 
 * @author satish
 */
public interface Configuration {

	/**
	 * Find and return the folder containing the friend groups file
	 * 
	 * @return	the folder containing the friend groups file
	 */
	File findStorageRoot();

	/**
	 * Find and return proxy class to {@link Session}
	 * @return Mailer
	 */
	Mailer getMailer();
	
	/**
	 * Reminder mail will be sent in these many days in advance
	 * @return	Reminder mail will be sent in these many days in advance
	 */
	int findDefaultReminderPeriod();
	
	/**
	 * Connection for database
	 * @return	Connection for database
	 */
	Connection getDatabaseConnection();
	
}
