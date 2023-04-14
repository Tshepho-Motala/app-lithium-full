ALTER TABLE `reservation`
ADD COLUMN `session_id` BIGINT(20) NULL;

CREATE INDEX `idx_session_id` ON `reservation` (`session_id`) ALGORITHM INPLACE LOCK NONE;
