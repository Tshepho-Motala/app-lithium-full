ALTER TABLE `report_run` 
ADD COLUMN `filtered_records` BIGINT(20) DEFAULT NULL,
ADD COLUMN `actions_performed` BIGINT(20) DEFAULT NULL;