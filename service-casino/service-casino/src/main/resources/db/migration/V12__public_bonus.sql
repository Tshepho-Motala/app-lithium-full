ALTER TABLE `bonus_revision` 
ADD COLUMN `public_view` BIT(1) NOT NULL DEFAULT b'0' AFTER `free_money_wager_requirement`;
