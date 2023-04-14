ALTER TABLE `incomplete_user` ADD COLUMN `referrer_guid` VARCHAR(255);
ALTER TABLE `user` ADD COLUMN `referrer_guid` VARCHAR(255);