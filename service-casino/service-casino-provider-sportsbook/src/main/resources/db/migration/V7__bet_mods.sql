ALTER TABLE `bet`
MODIFY COLUMN `accounting_transaction_id` bigint(20) DEFAULT NULL,
MODIFY COLUMN `balance_after` DOUBLE DEFAULT NULL;