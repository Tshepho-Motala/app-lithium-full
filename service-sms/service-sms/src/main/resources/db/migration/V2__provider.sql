ALTER TABLE `sms`
ADD COLUMN `provider_reference` VARCHAR(255) DEFAULT NULL,
ADD COLUMN `domain_provider_id` BIGINT(20) DEFAULT NULL,
ADD COLUMN `received_date` datetime DEFAULT NULL;
ALTER TABLE `sms`
ADD CONSTRAINT `fk_domain_provider_id` FOREIGN KEY (`domain_provider_id`) REFERENCES `domain_provider`(`id`);