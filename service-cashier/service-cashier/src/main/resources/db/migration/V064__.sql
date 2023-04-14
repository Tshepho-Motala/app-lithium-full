ALTER TABLE `auto_withdrawal_rule_set` ADD COLUMN `delay` BIGINT(20) DEFAULT NULL;
ALTER TABLE `transaction_workflow_history` ADD COLUMN `process_time` DATETIME DEFAULT NULL;
CREATE INDEX `idx_twh_process_time` ON `transaction_workflow_history` (`process_time`) ALGORITHM INPLACE LOCK NONE;
