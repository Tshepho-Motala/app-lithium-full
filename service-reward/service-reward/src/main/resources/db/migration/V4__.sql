ALTER TABLE `reward`
    ADD COLUMN `created` DATETIME DEFAULT NULL ,
    ADD COLUMN `updated` DATETIME DEFAULT NULL;

ALTER TABLE `reward_revision`
    ADD COLUMN `created` DATETIME DEFAULT NULL,
    ADD COLUMN `updated` DATETIME DEFAULT NULL;

ALTER TABLE `player_reward_type_history`
    ADD COLUMN `redeemed_date` DATETIME DEFAULT NULL;
