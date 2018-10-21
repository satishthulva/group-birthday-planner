package com.novice.hbdr.dao;

import java.util.List;

import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.statement.SqlQuery;

import com.novice.hbdr.dao.mappers.UserMapper;
import com.novice.hbdr.datamodels.User;

/**
 * Data access layer API for user data
 * 
 * @author satish
 */
public interface UserDAO {

	/**
	 * Find all users registered with the system
	 * 
	 * @return all users registered with the system
	 */
	@SqlQuery("SELECT user_id, first_name, last_name, gender, email, birthday, user_status FROM user_registry")
	@RegisterRowMapper(UserMapper.class)
	List<User> findUsers();

}
