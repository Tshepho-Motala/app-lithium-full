ALTER TABLE `settlement_debit`
ADD COLUMN `session_id` BIGINT(20) NOT NULL;

CREATE INDEX `idx_session_id` ON `settlement_debit` (`session_id`) ALGORITHM INPLACE LOCK NONE;
