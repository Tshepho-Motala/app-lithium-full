ALTER TABLE `transaction_processing_attempt` ADD COLUMN `cleaned` BIT(1) NOT NULL DEFAULT 0;
CREATE INDEX `idx_transaction_processing_attempt_cleaned` ON `transaction_processing_attempt` (`cleaned`);
CREATE INDEX `idx_transaction_processing_attempt_timestamp` ON `transaction_processing_attempt` (`timestamp`);