package com.novice.hbdr.datamodels;

/**
 * Meta information about the group
 * 
 * @author satish
 */
public class GroupDetails {
	/**
	 * Unique id for the group
	 */
	protected GroupID id;

	/**
	 * Name to address the group
	 */
	protected String name;

	/**
	 * Number of days in advance to send reminder
	 */
	protected int reminderPeriodInDays = -1;

	public GroupDetails(GroupID id, String name, int reminderPeriodInDays) {
		super();
		this.id = id;
		this.name = name;
		this.reminderPeriodInDays = reminderPeriodInDays;
	}

	/**
	 * @return the id
	 */
	public GroupID getId() {
		return id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the reminderPeriodInDays
	 */
	public int getReminderPeriodInDays() {
		return reminderPeriodInDays;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof GroupDetails)) {
			return false;
		}
		GroupDetails other = (GroupDetails) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return name;
	}
	
}
