ALTER TABLE `player_exclusionv2`
DROP INDEX `idx_pe_player_notified_post_expiry_date`,
DROP COLUMN `player_notified_post_expiry_date`;