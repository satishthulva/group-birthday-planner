package com.novice.hbdr.migration;

/**
 * Configuration for migration module
 * 
 * @author satish
 */
public interface MigrationProperty {
	/**
	 * Property storing the Database connection URL
	 */
	String DB_URL = "jdbcurl";
	/**
	 * Property storing the database user
	 */
	String DB_USER = "user";
	/**
	 * Property storing the database user password
	 */
	String DB_PASSWORD = "password";
}
