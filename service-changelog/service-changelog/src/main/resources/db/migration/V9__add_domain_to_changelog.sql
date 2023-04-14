ALTER TABLE `change_log` ADD COLUMN `domain_id` bigint(20) DEFAULT NULL;
ALTER TABLE `change_log` ADD CONSTRAINT `FKbsu5ttxt2638n80fasamgp9sw` FOREIGN KEY (`domain_id`) REFERENCES `domain` (`id`);
