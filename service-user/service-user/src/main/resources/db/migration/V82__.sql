ALTER TABLE `user` MODIFY COLUMN `last_name_prefix` VARCHAR(255) DEFAULT NULL, ALGORITHM=COPY, LOCK=SHARED;
ALTER TABLE `incomplete_user` MODIFY COLUMN `last_name_prefix` VARCHAR(255) DEFAULT NULL, ALGORITHM=COPY, LOCK=SHARED;
