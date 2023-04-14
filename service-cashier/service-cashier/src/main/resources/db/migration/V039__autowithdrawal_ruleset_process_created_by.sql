ALTER TABLE `auto_withdrawal_rule_set_process`
DROP COLUMN `created_by`;

ALTER TABLE `auto_withdrawal_rule_set_process`
ADD COLUMN `created_by_id` bigint(20) NOT NULL;

ALTER TABLE `auto_withdrawal_rule_set_process`
ADD CONSTRAINT `fk_created_by` FOREIGN KEY (`created_by_id`) REFERENCES `user` (`id`);
