ALTER TABLE `login_event` ADD COLUMN `internal` bit(1) DEFAULT NULL;
ALTER TABLE `login_event` ADD COLUMN `domain_id` bigint(20) DEFAULT NULL;
ALTER TABLE `login_event` ADD COLUMN `provider_name` varchar(255) DEFAULT NULL;
ALTER TABLE `login_event` ADD COLUMN `provider_url` varchar(255) DEFAULT NULL;