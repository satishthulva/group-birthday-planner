package com.novice.hbdr.service.impl;

import java.io.File;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.novice.hbdr.Configuration;
import com.novice.hbdr.Mailer;

/**
 * Implements {@link Configuration}
 * 
 * @author satish
 */
public class ConfigurationImpl implements Configuration {

	/**
	 * the folder containing the friend groups file
	 */
	private File storageRoot;
	/**
	 * Mailing handler
	 */
	private Mailer mailer = Mailer.createMailer();
	/**
	 * Reminder mail will be sent in these many days in advance
	 */
	private int defaultReminderPeriodDays;
	
	public ConfigurationImpl() {
		initialize();
	}
	
	/**
	 * Read the storage root absolute path from JNDI names
	 */
	private void initialize() {
		try {
			InitialContext initialContext = new InitialContext();
			
			String fileName = (String)initialContext.lookup("java:comp/env/param/Storageroot");
			this.storageRoot = new File(fileName);
			
			this.defaultReminderPeriodDays = (Integer)initialContext.lookup("java:comp/env/param/DefaultReminderDays");
		} catch(NamingException e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	
	@Override
	public File findStorageRoot() {
		return storageRoot;
	}

	/* (non-Javadoc)
	 * @see com.novice.hbdr.Configuration#getMailer()
	 */
	@Override
	public Mailer getMailer() {
		return mailer;
	}

	/* (non-Javadoc)
	 * @see com.novice.hbdr.Configuration#findDefaultReminderPeriod()
	 */
	@Override
	public int findDefaultReminderPeriod() {
		return defaultReminderPeriodDays;
	}
	
}
