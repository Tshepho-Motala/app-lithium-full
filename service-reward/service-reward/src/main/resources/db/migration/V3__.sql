ALTER TABLE `reward_revision_type`
    ADD COLUMN `notification_message` VARCHAR(255) DEFAULT NULL;
ALTER TABLE `player_reward_type_history`
    ADD COLUMN `value_given` DECIMAL(19,2) DEFAULT NULL,
    ADD COLUMN `value_in_cents` DECIMAL(19,2) DEFAULT NULL,
    ADD COLUMN `updated_date` DATETIME DEFAULT NULL,
    CHANGE COLUMN `type_counter` `value_used` DECIMAL(19,2) DEFAULT NULL;

ALTER TABLE `reward_revision_type_game`
    ADD COLUMN `game_name` VARCHAR(255) DEFAULT NULL,
    ADD COLUMN `game_id` VARCHAR(255) DEFAULT NULL;