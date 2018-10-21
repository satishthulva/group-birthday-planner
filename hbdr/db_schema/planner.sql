CREATE DATABASE planner_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

GRANT ALL PRIVILEGES ON planner_db.* to planner@localhost IDENTIFIED BY 'planner@27@';

USE planner_db;

/* Information of users in the system */
CREATE TABLE IF NOT EXISTS user_registry(
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

/* Basic information about the groups that exist in the system */
CREATE TABLE IF NOT EXISTS group_registry (
/* unique id for the group, system generated */
group_id VARCHAR(64) NOT NULL, 
/* unique name given to the group */
group_name VARCHAR(128) NOT NULL,
/* number of days before which the notification has to be sent to the users about an event */
reminder_period_in_days TINYINT NOT NULL DEFAULT 4,
/* when the group is created */
creation_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
/* modification time of this record, for bookkeeping purposes */
modification_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

PRIMARY KEY(group_id),
UNIQUE(group_name)
) ENGINE = InnoDB;

/* Maintains user to group association */
CREATE TABLE IF NOT EXISTS group_members_registry (
/* Unique id of the group the user is part of */
fk_group_id VARCHAR(64) NOT NULL,
/* Unique id of the user */
fk_user_id VARCHAR(64) NOT NULL,
/* when the group to user association is created */
creation_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
/* modification time of this record, for bookkeeping purposes */
modification_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

CONSTRAINT fk_gr_gur_gid FOREIGN KEY(fk_group_id) REFERENCES group_registry(group_id),
CONSTRAINT fk_ur_gur_uid FOREIGN KEY(fk_user_id) REFERENCES user_registry(user_id),
PRIMARY KEY(fk_group_id, fk_user_id)
) ENGINE = InnoDB;

