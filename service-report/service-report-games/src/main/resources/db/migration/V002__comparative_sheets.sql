ALTER TABLE `report_revision` ADD COLUMN `compare_x_periods` INT(11) DEFAULT NULL;

ALTER TABLE `report_run`
ADD COLUMN `run_granularity` INT(11) DEFAULT NULL,
ADD COLUMN `run_granularity_offset` INT(11) DEFAULT NULL,
ADD COLUMN `run_parent_id`BIGINT(20) DEFAULT NULL;