ALTER TABLE `report_run`
ADD COLUMN `period_start_date` datetime DEFAULT NULL,
ALGORITHM INPLACE,
LOCK NONE;
