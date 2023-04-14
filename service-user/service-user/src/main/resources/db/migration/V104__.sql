ALTER TABLE `user_link` ADD COLUMN `created_date` datetime default null, ALGORITHM INPLACE, LOCK NONE;
ALTER TABLE `user_link` ADD COLUMN `updated_date` datetime default null, ALGORITHM INPLACE, LOCK NONE;
