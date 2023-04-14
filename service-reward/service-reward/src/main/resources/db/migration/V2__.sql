ALTER TABLE `reward_revision_type_game` ADD COLUMN `deleted` bit(1) NOT NULL  DEFAULT 0;
ALTER TABLE `reward_revision_type_value` ADD COLUMN `deleted` bit(1) NOT NULL  DEFAULT 0;
ALTER TABLE `reward_revision_type_game` ADD COLUMN `version` int(11) NOT NULL DEFAULT 0;