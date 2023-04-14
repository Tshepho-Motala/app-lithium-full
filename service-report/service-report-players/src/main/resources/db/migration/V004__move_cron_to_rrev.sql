ALTER TABLE `report_revision` ADD COLUMN `cron` varchar(255) DEFAULT NULL;

UPDATE `report_revision`, `report`
SET `report_revision`.cron = `report`.cron
WHERE `report`.current_id = `report_revision`.id;

ALTER TABLE `report` DROP COLUMN `cron`