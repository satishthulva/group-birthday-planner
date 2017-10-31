package com.novice.hbdr.service.impl;

import java.io.File;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.novice.hbdr.Configuration;

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
	
	public ConfigurationImpl() {
		initialize();
	}
	
	/**
	 * Read the storage root absolute path from JNDI names
	 */
	private void initialize() {
		try {
			String fileName = InitialContext.doLookup("java:comp/env/param/Storageroot");
			this.storageRoot = new File(fileName);
		} catch(NamingException e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	
	@Override
	public File findStorageRoot() {
		return storageRoot;
	}
	
}
