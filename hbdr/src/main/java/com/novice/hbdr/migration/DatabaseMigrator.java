package com.novice.hbdr.migration;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.FluentConfiguration;

import com.novice.hbdr.Crypto;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * Manages database migration using {@link Flyway}
 * 
 * @author satish
 */
public class DatabaseMigrator {
	/**
	 * Databases to be version controlled. The first name in the list will have
	 * the `schema_version' table
	 */
	private static final String[] databases = { "planner_db" };
	/**
	 * For each production database with name DB there is a test database with
	 * name DB_test
	 */
	private static final String testDbSuffix = "_test";

	/**
	 * Find test database name for given database
	 * 
	 * @param db
	 *            Database name
	 * @return Test database name for given database
	 */
	private String getTestDBName(String db) {
		return db + testDbSuffix;
	}

	/**
	 * Find the names of the databases to be migrated, based on the choice. If
	 * <code>isProduction</code> set to <code>true</code>, return the production
	 * databases. Otherwise, return test databases
	 * 
	 * @param isProduction
	 *            <code>true</code>, if to migrate production databases
	 * @return the names of the databases to be migrated
	 */
	private String[] getDatabasesToMigrate(boolean isProduction) {
		if (isProduction) {
			return databases;
		} else {
			String[] dbsToMigrate = new String[databases.length];
			int i = 0;
			for (String db : databases) {
				dbsToMigrate[i] = getTestDBName(db);
				i += 1;
			}
			return dbsToMigrate;
		}
	}

	/**
	 * Placeholder replacements for the SQL migration scripts.
	 * 
	 * @param isProduction
	 *            <code>true</code>, if to migrate production databases
	 * @return Placeholder replacements for the SQL migration scripts
	 */
	private Map<String, String> getPlaceholders(boolean isProduction) {
		Map<String, String> placeHoldersMap = new HashMap<>();
		for (String db : databases) {
			placeHoldersMap.put(db, isProduction ? db : getTestDBName(db));
		}
		return placeHoldersMap;
	}

	/**
	 * Migrate the databases
	 * 
	 * @param isProduction
	 *            <code>true</code>, if production databases are to be migrated
	 */
	public void migrate(boolean isProduction) throws IOException {
		FluentConfiguration flywayConfig = Flyway.configure();
		// dbs to migrate
		flywayConfig.schemas(getDatabasesToMigrate(isProduction));
		// placeholders
		flywayConfig.placeholders(getPlaceholders(isProduction));
		// datasource
		DataSource dataSource = getDatasource(isProduction);
		flywayConfig.dataSource(dataSource);

		Flyway flyway = flywayConfig.load();
		flyway.migrate();

		try {
			dataSource.unwrap(HikariDataSource.class).close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Get the MySQL Connection factory
	 * 
	 * @param isProduction
	 *            <code>true</code>, if we should connect to production
	 *            database. <code>false</code>, otherwise
	 * @return MySQL connection factory
	 * @throws IOException
	 */
	private DataSource getDatasource(boolean isProduction) throws IOException {
		Properties properties = new Properties();
		properties.load(DatabaseMigrator.class.getResourceAsStream("/migration.properties"));

		HikariConfig hikariConfig = new HikariConfig();
		String dbUrl = properties.getProperty(MigrationProperty.DB_URL,
				"jdbc:mysql://localhost:3306/planner_db?autoReconnect=true&useUnicode=yes");

		// Placeholder replacement for DB connection URL
		for (Entry<String, String> dbEntry : getPlaceholders(isProduction).entrySet()) {
			dbUrl.replace(dbEntry.getKey(), dbEntry.getValue());
		}

		hikariConfig.setJdbcUrl(dbUrl);

		hikariConfig.setUsername(properties.getProperty(MigrationProperty.DB_USER, "planner"));
		hikariConfig.setPassword(Crypto
				.decode(properties.getProperty(MigrationProperty.DB_PASSWORD, "0d5bcb3996f7e8765ea7bde8139c1e38")));
		hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
		hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
		hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

		return new HikariDataSource(hikariConfig);
	}

	public static void main(String[] args) throws Exception {
		DatabaseMigrator migrator = new DatabaseMigrator();
		if (args.length > 0 && Boolean.parseBoolean(args[0]))
			migrator.migrate(true);
		else
			migrator.migrate(false);
		System.out.println("Migrated successfully");
	}

}
