package com.novice.hbdr.datamodels;

/**
 * <pre>
 * User account status. Only active users can use the system.
 * Till the user generates a password and validates the account creation, account will be inactive state.
 * 
 * User account can be suspended by admin.
 * </pre>
 * @author satish
 */
public enum UserStatus {
	ACTIVE, 
	IN_ACTIVE, 
	SUSPENDED,
	DELETED;
}
