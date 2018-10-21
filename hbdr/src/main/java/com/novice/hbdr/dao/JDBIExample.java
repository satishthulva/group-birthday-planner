package com.novice.hbdr.dao;

import java.util.List;

import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;

import com.novice.hbdr.Crypto;
import com.novice.hbdr.datamodels.User;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class JDBIExample {

	public static void main(String[] args) {
		HikariConfig hikariConfig = new HikariConfig();

		hikariConfig.setJdbcUrl("jdbc:mysql://localhost:3306/planner_db?autoReconnect=true&useUnicode=yes");

		hikariConfig.setUsername("planner");
		hikariConfig.setPassword(Crypto.decode("0d5bcb3996f7e8765ea7bde8139c1e38"));
		hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
		hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
		hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

		try (HikariDataSource hikariDataSource = new HikariDataSource(hikariConfig)) {
			Jdbi jdbi = Jdbi.create(hikariDataSource);
			jdbi.installPlugin(new SqlObjectPlugin());

			Handle handle = jdbi.open();
			UserDAO userDAO = handle.attach(UserDAO.class);
			
			List<User> users = userDAO.findUsers();
			for(User user : users) {
				System.out.println(user.getUserID().getId() + "\t" +  user.getPerson().getName() + "\t" + user.getPerson().getGender().name());
			}
		}
	}

}
