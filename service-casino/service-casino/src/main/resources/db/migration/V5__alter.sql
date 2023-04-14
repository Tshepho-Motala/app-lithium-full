ALTER TABLE bonus_revision
ADD COLUMN `active_days` varchar(15) DEFAULT NULL;

ALTER TABLE bonus_revision
ADD COLUMN `active_start_time` time DEFAULT NULL;

ALTER TABLE bonus_revision
ADD COLUMN `active_end_time` time DEFAULT NULL;

ALTER TABLE bonus_revision
ADD COLUMN `active_timezone` varchar(255) DEFAULT NULL;

