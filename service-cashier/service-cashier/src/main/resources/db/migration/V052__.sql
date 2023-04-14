ALTER TABLE `transaction` ADD COLUMN `direct_withdrawal` bit(1) AFTER `user_id`;
ALTER TABLE `transaction` ADD COLUMN `initiation_author` bigint(20) AFTER `direct_withdrawal`;
ALTER TABLE `transaction` ADD FOREIGN KEY `fk_initiation_author` (`initiation_author`) REFERENCES `user` (`id`);