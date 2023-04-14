ALTER TABLE `domain_restriction_set` ADD COLUMN `alt_message_count` INT DEFAULT 0;

ALTER TABLE `user_restriction_set` ADD COLUMN `sub_type` INT DEFAULT NULL;
