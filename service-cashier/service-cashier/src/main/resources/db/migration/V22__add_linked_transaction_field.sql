ALTER TABLE `transaction` ADD COLUMN `linked_transaction_id` bigint(20) DEFAULT NULL;
ALTER TABLE `transaction` ADD CONSTRAINT `FK6pin8hvb5pt1fs2e5p5cukqmu` FOREIGN KEY (`linked_transaction_id`) REFERENCES `transaction` (`id`);
