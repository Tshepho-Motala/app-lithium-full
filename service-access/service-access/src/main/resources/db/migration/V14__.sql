-- RENAME TABLE `access_rule` TO `access_ruleset`;
ALTER TABLE `access_rule` ADD COLUMN `description` VARCHAR(255) DEFAULT NULL;
ALTER TABLE `access_rule` DROP COLUMN `accept`;
ALTER TABLE `access_rule` ADD COLUMN `default_action` VARCHAR(255) DEFAULT NULL;
ALTER TABLE `access_rule` ADD COLUMN `default_message` VARCHAR(255) DEFAULT NULL;

ALTER TABLE `access_control_list` DROP COLUMN `action`;
-- ALTER TABLE `access_control_list` CHANGE COLUMN `access_rule_id` `access_ruleset_id` BIGINT(20) NOT NULL;
ALTER TABLE `access_control_list` ADD COLUMN `action_success` VARCHAR(255) DEFAULT NULL;
ALTER TABLE `access_control_list` ADD COLUMN `action_failed` VARCHAR(255) DEFAULT NULL;

-- ALTER TABLE `external_list` CHANGE COLUMN `access_rule_id` `access_ruleset_id` BIGINT(20) NOT NULL;
ALTER TABLE `external_list` ADD COLUMN `action_success` VARCHAR(255) DEFAULT NULL;
ALTER TABLE `external_list` ADD COLUMN `action_failed` VARCHAR(255) DEFAULT NULL;
ALTER TABLE `external_list` ADD COLUMN `message` VARCHAR(255) DEFAULT NULL;