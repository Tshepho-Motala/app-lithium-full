ALTER TABLE `lithium_user`.`user`
  CHANGE COLUMN `email_opt_out` `email_opt_out` BIT(1) DEFAULT 0,
  CHANGE COLUMN `sms_opt_out` `sms_opt_out` BIT(1) DEFAULT 0,
  CHANGE COLUMN `call_opt_out` `call_opt_out` BIT(1) DEFAULT 0,
  CHANGE COLUMN `push_opt_out` `push_opt_out` BIT(1) DEFAULT 0,
  CHANGE COLUMN `leaderboard_opt_out` `leaderboard_opt_out` BIT(1) DEFAULT 0,
  CHANGE COLUMN `post_opt_out` `post_opt_out` BIT(1) DEFAULT 0,
  CHANGE COLUMN `comms_opt_in_complete` `comms_opt_in_complete` BIT(1) DEFAULT 0, ALGORITHM = INPLACE, LOCK = NONE;

UPDATE `lithium_user`.`user`
SET `email_opt_out` = false
WHERE `email_opt_out` is null;

UPDATE `lithium_user`.`user`
SET `sms_opt_out` = false
WHERE `sms_opt_out` is null;

UPDATE `lithium_user`.`user`
SET `call_opt_out` = false
WHERE `call_opt_out` is null;

UPDATE `lithium_user`.`user`
SET `push_opt_out` = false
WHERE `push_opt_out` is null;

UPDATE `lithium_user`.`user`
SET `leaderboard_opt_out` = false
WHERE `leaderboard_opt_out` is null;

UPDATE `lithium_user`.`user`
SET `post_opt_out` = false
WHERE `post_opt_out` is null;

UPDATE `lithium_user`.`user`
SET `comms_opt_in_complete` = false
WHERE `comms_opt_in_complete` is null;
