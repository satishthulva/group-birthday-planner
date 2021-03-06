CREATE TABLE IF NOT EXISTS ${planner_db}.user_registry(
/* unique id for the user, system generated */
user_id VARCHAR(64) NOT NULL,
/* first name of the user */
first_name VARCHAR(256) NOT NULL,
/* last name of the user */
last_name VARCHAR(256) DEFAULT NULL,
/* email used to register this account */
email VARCHAR(256) NOT NULL,
/* birthday of user */
birthday TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
/* when the user account is created */
creation_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
/* modification time of this record, for bookkeeping purposes */
modification_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
/* account status */
user_status ENUM('ACTIVE', 'IN_ACTIVE', 'SUSPENDED', 'DELETED') NOT NULL DEFAULT 'IN_ACTIVE',
/* salted checksum(md5?) */
password VARCHAR(128) NOT NULL,
/* gender of the user */
gender ENUM('MALE', 'FEMALE', 'TRANS', 'UNKNOWN') NOT NULL DEFAULT 'UNKNOWN',

PRIMARY KEY(user_id),
unique(email)
) ENGINE = InnoDB;