CREATE TABLE IF NOT EXISTS ${planner_db}.group_registry (
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
CREATE TABLE IF NOT EXISTS ${planner_db}.group_members_registry (
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