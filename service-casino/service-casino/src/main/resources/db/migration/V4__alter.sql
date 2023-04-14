ALTER TABLE bonus_revision
ADD COLUMN `visible_to_player` bit(1) NOT NULL DEFAULT 1;

ALTER TABLE bonus_revision
ADD COLUMN `player_may_cancel` bit(1) NOT NULL DEFAULT 1;

ALTER TABLE bonus_revision
ADD COLUMN `cancel_on_deposit_minimum_amount` bigint(20) DEFAULT 500;

ALTER TABLE bonus_revision
ADD COLUMN `cancel_on_bet_bigger_than_balance` bit(1) NOT NULL DEFAULT 1;
