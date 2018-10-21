package com.novice.hbdr.dao.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;

import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import com.novice.hbdr.datamodels.Birthday;
import com.novice.hbdr.datamodels.Gender;
import com.novice.hbdr.datamodels.Month;
import com.novice.hbdr.datamodels.Person;
import com.novice.hbdr.datamodels.User;
import com.novice.hbdr.datamodels.UserID;
import com.novice.hbdr.datamodels.UserStatus;

/**
 * SQL query result row to {@link User} object creation
 * 
 * @author satish
 */
public class UserMapper implements RowMapper<User> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jdbi.v3.core.mapper.RowMapper#map(java.sql.ResultSet,
	 * org.jdbi.v3.core.statement.StatementContext)
	 */
	@Override
	public User map(ResultSet rs, StatementContext ctx) throws SQLException {
		UserID userID = new UserID(rs.getString(1));
		String firstName = rs.getString(2);
		String lastName = rs.getString(3);
		Gender gender = Gender.valueOf(rs.getString(4));
		String email = rs.getString(5);

		Timestamp birthdayTimestamp = rs.getTimestamp(6);
		Birthday birthday = toBirthday(birthdayTimestamp);

		UserStatus userStatus = UserStatus.valueOf(rs.getString(7));
		return new User(userID,
				new Person(firstName + (lastName != null ? " " + lastName : ""), firstName, birthday, email, gender),
				userStatus);
	}

	/**
	 * Unix timestamp to birthday conversion
	 * @param timestamp	Unix timestamp
	 * @return converted birthday
	 */
	private Birthday toBirthday(Timestamp timestamp) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(timestamp.getTime());
		Month month = Month.values()[calendar.get(Calendar.MONTH)];
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		return new Birthday(day, month);
	}

}
