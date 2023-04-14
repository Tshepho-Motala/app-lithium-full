ALTER TABLE `auto_withdrawal_rule_set` ADD COLUMN `deleted` BIT(1) DEFAULT 0;
CREATE INDEX `idx_deleted` ON `auto_withdrawal_rule_set` (`deleted`) ALGORITHM INPLACE LOCK NONE;

ALTER TABLE `auto_withdrawal_rule` ADD COLUMN `deleted` BIT(1) DEFAULT 0;
CREATE INDEX `idx_deleted` ON `auto_withdrawal_rule` (`deleted`) ALGORITHM INPLACE LOCK NONE;