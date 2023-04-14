CREATE INDEX `idx_pe_expiry_date` ON `player_exclusion` (`expiry_date`) ALGORITHM INPLACE LOCK NONE;

-- Clear out all already expired player exclusions
DELETE FROM `player_exclusion` WHERE `expiry_date` <= now();