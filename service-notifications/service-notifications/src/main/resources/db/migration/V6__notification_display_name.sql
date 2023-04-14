ALTER TABLE `notification` ADD COLUMN `display_name` VARCHAR(255);

UPDATE `notification` set `display_name` = `name`;

ALTER TABLE `notification` MODIFY `display_name` VARCHAR(255) NOT NULL;