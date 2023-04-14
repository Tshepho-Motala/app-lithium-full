ALTER TABLE `verification_result` ADD COLUMN `document_decision` varchar(16) DEFAULT NULL;
ALTER TABLE `verification_result` ADD COLUMN `address_decision` varchar(16) DEFAULT NULL;
ALTER TABLE `verification_result` MODIFY COLUMN `provider_id` bigint(20) DEFAULT NULL;
ALTER TABLE `result_message` MODIFY COLUMN `description` varchar(512) NOT NULL;