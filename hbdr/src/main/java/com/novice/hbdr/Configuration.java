package com.novice.hbdr;

import java.io.File;

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
	
}
