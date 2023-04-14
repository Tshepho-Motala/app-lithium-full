ALTER TABLE `player_bonus_history` ADD COLUMN `request_id` BIGINT(20) NULL DEFAULT NULL;
ALTER TABLE `player_bonus_history` ADD COLUMN `description` VARCHAR(200) NULL DEFAULT NULL;
ALTER TABLE `player_bonus_history` ADD COLUMN `client_id` VARCHAR(200) NULL DEFAULT NULL;
ALTER TABLE `player_bonus_history` ADD COLUMN `session_id` BIGINT(20) NULL DEFAULT NULL;

CREATE INDEX `idx_pbh_request_id` ON `player_bonus_history` (`request_id`);
CREATE INDEX `idx_pbh_client_id` ON `player_bonus_history` (`client_id`);
CREATE INDEX `idx_pbh_session_id` ON `player_bonus_history` (`session_id`);

ALTER TABLE `player_bonus_history` ADD CONSTRAINT `idx_client_request` UNIQUE (`request_id`, `client_id`);