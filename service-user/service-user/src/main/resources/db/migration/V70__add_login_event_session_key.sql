ALTER TABLE `login_event` ADD column `session_key` VARCHAR(255) NULL;
CREATE INDEX `idx_loginevent_session_key` ON `login_event` (`session_key`) ALGORITHM INPLACE LOCK NONE;
