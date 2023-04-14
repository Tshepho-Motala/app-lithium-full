ALTER TABLE `player_bonus_pending` ADD COLUMN `custom_free_money_amount_cents` bigint(20) DEFAULT NULL;
ALTER TABLE `player_bonus_history` ADD COLUMN `custom_free_money_amount_cents` bigint(20) DEFAULT NULL;
