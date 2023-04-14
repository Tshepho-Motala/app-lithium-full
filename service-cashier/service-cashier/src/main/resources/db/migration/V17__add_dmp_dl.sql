ALTER TABLE `domain_method_processor` ADD COLUMN `domain_limits_id` bigint(20) DEFAULT NULL;
ALTER TABLE `domain_method_processor` ADD CONSTRAINT `FKab5888e25d19fd5ec9f08c7ab` FOREIGN KEY (`domain_limits_id`) REFERENCES `limits` (`id`);
