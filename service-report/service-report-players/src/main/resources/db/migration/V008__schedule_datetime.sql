ALTER TABLE `report_revision`
ADD COLUMN `chosen_date_string` VARCHAR(255) DEFAULT NULL,
ADD COLUMN `chosen_time_string` VARCHAR(255) DEFAULT NULL;