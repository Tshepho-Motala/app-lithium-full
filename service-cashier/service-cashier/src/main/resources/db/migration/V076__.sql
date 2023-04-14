ALTER TABLE `limits` ADD COLUMN `min_first_transaction_amount` bigint(20) DEFAULT NULL;
ALTER TABLE `limits` ADD COLUMN `max_first_transaction_amount` bigint(20) DEFAULT NULL;
