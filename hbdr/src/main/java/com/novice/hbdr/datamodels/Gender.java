/*
 * Gender.java
 */
package com.novice.hbdr.datamodels;

/**
 * Persons' gender
 * 
 * @author satishbabu
 */
public enum Gender {
	MALE("Male"), FEMALE("Female"), TRANS("Trans Gender"), UNKNOWN("Not known");

	private final String description;

	private Gender(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public String toString() {
		return description;
	}
	
	/**
	 * @return Gender
	 */
	public static Gender toGender(String gender) {
		for (Gender one : values()) {
			if (one.name().equalsIgnoreCase(gender) || one.getDescription().equalsIgnoreCase(gender))
				return one;
		}

		throw new RuntimeException("Unknown gender " + gender);
	}
}