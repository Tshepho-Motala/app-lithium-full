ALTER TABLE `transaction` ADD COLUMN `reviewed_by`  bigint(20) DEFAULT NULL AFTER `auto_approved`;
ALTER TABLE `transaction` ADD FOREIGN KEY `fk_reviewed_by` (`reviewed_by`) REFERENCES `user` (`id`);