-- If there are any duplicates on any environment (even in the history) then this will fail.
-- We need to manually clear out any duplicates before this is released, or find a way to do it automagically.
-- Probably easiest to do a manual check.

ALTER TABLE `label_value` DROP COLUMN `viewable`;

ALTER TABLE `domain_revision_label_value` ADD COLUMN `label_id` BIGINT(20) DEFAULT NULL;
ALTER TABLE `domain_revision_label_value` ADD CONSTRAINT `fk_label` FOREIGN KEY (`label_id`) REFERENCES `label` (`id`);

UPDATE `domain_revision_label_value`
INNER JOIN `label_value` ON `domain_revision_label_value`.`label_value_id` = `label_value`.`id`
INNER JOIN `label` ON `label_value`.`label_id` = `label`.`id`
SET `domain_revision_label_value`.`label_id` = `label`.`id`;

ALTER TABLE `domain_revision_label_value` MODIFY COLUMN `label_id` BIGINT(20) NOT NULL;
ALTER TABLE `domain_revision_label_value` ADD UNIQUE KEY `idx_domain_revision_label` (`domain_revision_id`, `label_id`)